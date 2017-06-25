package actor

import akka.actor.Actor
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import table.DB
import table.DB.ParagraphCase
import util.Util

class ParagraphActor() extends Actor {

  def fetchParagraph(url: String): ParagraphCase = {
    val source = Util.parse(url)
    val browser = JsoupBrowser()
    val page = browser.get(url)
    source.paragraphPolicy.parse(page)
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
