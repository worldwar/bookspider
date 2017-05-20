package actor

import akka.actor.Actor
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.scraper.ContentExtractors._
import skeleton.Skeleton
import table.DB
import table.DB.ParagraphCase
import util.Util

class ParagraphActor() extends Actor {

  def fetchParagraph(url: String): ParagraphCase = {

    println(this.toString + "start to download")

    val browser = JsoupBrowser()
    val page = browser.get(url)
    val skeleton: Skeleton = Util.parse(url)

    val ps: Iterable[String] = page >> texts(skeleton.paragraphsSelector)

    val paragraph: String = ps.mkString("\n")

    return ParagraphCase(0, Util.randomHash(), Some(paragraph))
  }

  def doParagraph(chapter:String, url: String): Unit = {
    val paragraph: ParagraphCase = fetchParagraph(url)
    DB.insertParagraph(paragraph)
    this.sender() ! ParagraphCompleteMessage(chapter, paragraph.id)
  }

  override def receive: Receive = {
    case ParagraphMessage(chapter, url) => {
      doParagraph(chapter, url)
    }
  }
}
