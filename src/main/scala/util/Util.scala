package util

import java.security.MessageDigest

import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.scraper.ContentExtractors._
import source.source.Source
import scala.util.Random
import net.ruippeixotog.scalascraper.model.{Element}
object Util {

  def md5Hash(text: String): String = {
    val md5: MessageDigest = java.security.MessageDigest.getInstance("MD5")
    val digest: Array[Byte] = md5.digest(text.getBytes())
    digest.map(0xFF & _).map {
      "%02x".format(_)
    }.foldLeft("") {
      _ + _
    }
  }

  def randomHash(): String = Util.md5Hash(Random.alphanumeric.take(10).mkString)

  def parse(url: String): (Source) = {
    if (url.contains("qidian")) {
      source.source.qidian
    } else if (url.contains("qu.la")){
      source.source.biquge
    } else {
      source.source.biquge
    }
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
}

