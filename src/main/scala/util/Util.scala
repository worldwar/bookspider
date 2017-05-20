package util

import java.security.MessageDigest

import skeleton.{QidianSkeleton, Skeleton}

import scala.util.Random

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

  def parse(url: String): Skeleton = {
    new QidianSkeleton
  }
}

