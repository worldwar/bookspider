package actor


import akka.actor.{Actor, ActorRef, Props}
import domain.{Book}
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.Element
import net.ruippeixotog.scalascraper.scraper.ContentExtractors._
import table.DB
import util.Util

case class FetchMessage()

case class CatalogMessage(url: String)

case class ParagraphMessage(chapter_id: String, url: String)

case class ParagraphCompleteMessage(chapter_id: String, paragraph_id: String)

class CatalogActor(name: String) extends Actor {
  val size = 10
  val paragraphActors: Array[ActorRef] = Array.fill(size)(Context.actorSystem.actorOf(Props(new ParagraphActor)))

  def setParagraph(chapter: String, paragraph: String): Unit = {
    DB.updateChapterWithParagraph(chapter, paragraph)
  }

  override def receive: Receive = {
    case CatalogMessage(url) => {
      println(s"receive catalog message %url")
      doCatalog(url)
    };
    case ParagraphCompleteMessage(chapter, paragraph) => {
      setParagraph(chapter, paragraph)
    }
  }

  def dispatchCatalog(book: Book): Unit = {
    var i = 0
    book.volumes foreach { volume =>
      volume.chapters foreach { chapter =>
        paragraphActors(i % size) ! ParagraphMessage(chapter.id, chapter.originalUrl.get)
        i += 1
      }
    }
  }

  def doCatalog(url: String): Unit = {
    println("start fetch")
    val book: Book = fetchCatalog(url)
    storeCatalog(book)
    dispatchCatalog(book)
  }

  def fetchCatalog(url: String): Book = {
    val source = Util.parse(url)
    val browser = JsoupBrowser()
    val page = browser.get(url)
    source.catalogPolicy.parse(url, page)
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
