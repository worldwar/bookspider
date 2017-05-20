package actor


import akka.actor.Actor
import domain.{Book, Volume}
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.Element
import net.ruippeixotog.scalascraper.scraper.ContentExtractors._
import org.joda.time.DateTime
import skeleton.{QidianSkeleton, Skeleton}
import table.DB
import table.DB.{BookCase, ChapterCase, VolumeCase}
import util.Util

case class FetchMessage()

case class CatalogMessage(url: String)

case class ContentMessage()

class CatalogActor(name: String) extends Actor {

  override def receive: Receive = {
    case CatalogMessage(url) => {
      println(s"receive catalog message %url")
      doCatalog(url)
    }
  }

  def parse(url: String): Skeleton = {
    new QidianSkeleton
  }


  def dispatchCatalog(book: Book): Unit = {

  }

  def doCatalog(url: String): Unit = {
    println("start fetch")
    val book: Book = fetchCatalog(url)
    storeCatalog(book)
    dispatchCatalog(book)
  }

  def fetchCatalog(url: String): Book = {
    val skeleton: Skeleton = parse(url)
    val browser = JsoupBrowser()
    val page = browser.get(url)

    val title: String = page >> text(skeleton.titleSelector)
    val author: String = page >> text(skeleton.auhtorSelector)
    val bookCase = BookCase(0, Util.randomHash(), title, author, Some(null), Some(url), Some(DateTime.now()), Some(DateTime.now()))
    val volumeItems: List[Element] = page >> elementList(skeleton.volumesSelector)

    var chapterSeq = -1
    var volumeSeq = -1
    val volumes = volumeItems.map(volume => {
      val bookId: String = bookCase.id
      val volumeId: String = Util.randomHash()
      val volumeTitle: String = volume >> text(skeleton.volumeTitleRelativeSelector)
      volumeSeq += 1
      val currentVolume = VolumeCase(0, volumeId, Some(skeleton.volumeTitleFactory()(volumeTitle)), bookId, volumeSeq)

      val chapters: List[Element] = volume >> elementList(skeleton.chaptersRelativeSelector)

      val chapterCases = chapters.map(chapter => {
        val chapterTitle: String = elementText(chapter, skeleton.chapterTitleRelativeSelector())
        val chapterUrl: String = elementLink(chapter, skeleton.paragraphLinkSelector())
        chapterSeq += 1
        ChapterCase(0, Util.randomHash(), Some(skeleton.chapterTitleFactory()(chapterTitle)), bookId, volumeId, chapterSeq, Some(chapterUrl), Some(""))
      })
      new Volume(currentVolume, chapterCases)
    })
    new Book(bookCase, volumes)
  }

  def elementText(element: Element, selector: String): String = {
    if (selector.isEmpty()) {
      return element.text
    } else {
      return element >> text(selector)
    }
  }

  def elementLink(element: Element, selector: String): String = {
    if (selector.isEmpty()) {
      return element.attr("href")
    } else {
      return element >> attr("href")(selector)
    }
  }

  def storeCatalog(book: Book): Unit = {
    DB.insertBook(book.book)
    book.volumes foreach (volume => {
      DB.insertVolume(volume.volume)
      volume.chapters foreach (chapter => {
        DB.insertChapter(chapter)
      })
    })
    println("storing catalog completes!")
  }
}
