package skeleton

import domain.{Volume, Book}
import net.ruippeixotog.scalascraper.model.{Element, Document}
import net.ruippeixotog.scalascraper.scraper.ContentExtractors._
import net.ruippeixotog.scalascraper.dsl.DSL._
import org.joda.time.DateTime
import table.DB.{ParagraphCase, ChapterCase, VolumeCase, BookCase}
import util.Util

import scala.collection.mutable

trait CatalogPolicy {
  def parse(url: String, page: Document) : Book
}

trait ParagraphPolicy {
  def parse(page: Document): ParagraphCase
}

class TreeCatalogPolicy extends CatalogPolicy {

  override def parse(url: String, page: Document): Book = {
    val skeleton = new QidianSkeleton

    val title: String = page >> text(skeleton.titleSelector)
    val author: String = page >> text(skeleton.authorSelector)
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
        val chapterTitle: String = Util.elementText(chapter, skeleton.chapterTitleRelativeSelector())
        val chapterUrl: String = Util.elementLink(chapter, skeleton.paragraphLinkSelector())

        chapterSeq += 1
        ChapterCase(0, Util.randomHash(), Some(skeleton.chapterTitleFactory()(chapterTitle)), bookId,
          volumeId, chapterSeq, Some(skeleton.chapterUrlFactory()(chapterUrl)), Some(""))
      })
      new Volume(currentVolume, chapterCases)
    })
    new Book(bookCase, volumes)

  }
}

class DataListCatalogPolicy extends CatalogPolicy {

  override def parse(baseUrl: String, page: Document): Book = {
    val skeleton = new BiqugeSkeleton
    val title: String = page >> text(skeleton.titleSelector)
    val author: String = page >> text(skeleton.authorSelector)
    val bookCase = BookCase(0, Util.randomHash(), title, skeleton.authorFactory()(author), Some(null), Some(baseUrl), Some(DateTime.now()), Some(DateTime.now()))
    val items = (page >> element(skeleton.volumesSelector)).children

    var chapterSeq = -1
    var volumeSeq = -1
    var currentVolumeId: String = ""
    var currentVolumeTitle: String = ""
    var currentChapters = mutable.MutableList[ChapterCase]()

    val volumes = mutable.MutableList[Volume]()
    val bookId: String = bookCase.id
    for (item <- items) {
      if (item.tagName == skeleton.volumeTag()) {
        if (currentChapters.nonEmpty) {
          volumeSeq += 1
          val currentVolume = VolumeCase(0, currentVolumeId, Some(skeleton.volumeTitleFactory()(currentVolumeTitle)), bookId, volumeSeq)
          volumes += new Volume(currentVolume, currentChapters)
        }
        currentVolumeTitle = item.text
        currentVolumeId = Util.randomHash()
        currentChapters = mutable.MutableList[ChapterCase]()

      } else if (item.tagName == skeleton.chapterTag()) {
        val chapterTitle = Util.elementText(item, skeleton.chapterTitleRelativeSelector())
        val chapterUrl: String = Util.elementLink(item, skeleton.paragraphLinkSelector())

        val chapter = ChapterCase(0, Util.randomHash(), Some(skeleton.chapterTitleFactory()(chapterTitle)), bookId,
          currentVolumeId, chapterSeq, Some(skeleton.chapterUrlFactory()(chapterUrl, baseUrl)), Some(""))
        currentChapters += chapter
        chapterSeq += 1
      }
    }

    if (currentChapters.nonEmpty) {
      volumeSeq += 1
      val currentVolume = VolumeCase(0, currentVolumeId, Some(skeleton.volumeTitleFactory()(currentVolumeTitle)), bookId, volumeSeq)
      volumes += new Volume(currentVolume, currentChapters)
    }
    new Book(bookCase, volumes)
  }
}

class BiqugeParagraphPolicy extends ParagraphPolicy {
  override def parse(page: Document): ParagraphCase = {
    val skeleton = new BiqugeParagraphSkeleton

    val ps: Iterable[String] = page >> texts(skeleton.paragraphsSelector)

    val ps2 = ps.iterator.next().split(Character.toChars(160).mkString * 4)

    val paragraph: String = ps2.mkString("\n")

    return ParagraphCase(0, Util.randomHash(), Some(paragraph))
  }
}
