package skeleton

trait CatalogSkeleton {
  def titleSelector(): String

  def titleFactory(): Function1[String, String] = identity

  def authorSelector(): String

  def authorFactory(): Function1[String, String] = identity

  def volumesSelector(): String
}

trait ParagraphSkeleton {
  def paragraphsSelector(): String
}

class BiqugeParagraphSkeleton extends ParagraphSkeleton {
  def paragraphsSelector(): String = "#content"
}

abstract class TreeSkeleton extends CatalogSkeleton {

  def volumeTitleRelativeSelector(): String

  def chaptersRelativeSelector(): String

  def chapterTitleRelativeSelector(): String

  def volumeTitleFactory(): Function1[String, String] = identity

  def chapterTitleFactory(): Function1[String, String] = identity

  def chapterUrlFactory(): Function1[String, String] = identity

  def paragraphLinkSelector(): String = "a"

  def paragraphsSelector(): String
}

abstract class DatalistSkeleton extends CatalogSkeleton {
  def volumeTag(): String = "dt"

  def chapterTag(): String = "dd"

  def volumeTitleRelativeSelector(): String = ""

  def volumeTitleFactory(): Function1[String, String] = identity

  def chapterTitleRelativeSelector(): String = "a"

  def chapterTitleFactory(): Function1[String, String] = identity

  def chapterUrlFactory(): (String, String) => String

  def paragraphLinkSelector(): String
}

class QidianSkeleton extends TreeSkeleton {

  override def titleSelector(): String = ".book-info h1 em"

  override def chaptersRelativeSelector(): String = "ul li"

  override def authorSelector(): String = ".book-info h1 span a"

  override def volumeTitleRelativeSelector(): String = "h3"

  override def volumesSelector(): String = "#j-catalogWrap .volume"

  override def chapterTitleRelativeSelector(): String = "a"

  override def volumeTitleFactory() = (title: String) => {
    val parts = title.split(" |·")
    if (parts.size >= 5)
      parts(2)
    else
      ""
  }

  override def paragraphsSelector(): String = ".read-content p"

  override def chapterUrlFactory() = (url: String) => {
    if (!url.isEmpty) {
      if (url.startsWith("//")) {
        "http:" + url
      } else if (!url.startsWith("http")) {
        "http://" + url
      } else {
        url
      }
    } else {
      url
    }
  }
}

class BiqugeSkeleton extends DatalistSkeleton {

  override def titleSelector(): String = "#info h1"

  override def authorSelector(): String = "#info p"

  override def authorFactory() = (author: String) => {
    val parts = author.split("：")
    if (parts.size == 2)
      parts(1)
    else
      author
  }

  override def volumesSelector(): String = "#list dl"

  override def chapterUrlFactory() = (url: String, base: String) => {
    val parts = base.split("/")

    if (parts.size >= 3)
      parts(0) + "//" + parts(2) + url
    else
      base + url
  }

  override def paragraphLinkSelector(): String = "a"
}
