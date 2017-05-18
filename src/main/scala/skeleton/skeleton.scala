package skeleton

trait Skeleton {
  def titleSelector(): String

  def auhtorSelector(): String

  def volumesSelector(): String

  def volumeTitleRelativeSelector(): String

  def chaptersRelativeSelector(): String

  def chapterTitleRelativeSelector(): String

  def titleFactory(): Function1[String, String] = identity

  def volumeTitleFactory(): Function1[String, String] = identity

  def chapterTitleFactory(): Function1[String, String] = identity
}

class QidianSkeleton extends Skeleton {

  override def titleSelector(): String = ".book-info h1 em"

  override def chaptersRelativeSelector(): String = "ul li"

  override def auhtorSelector(): String = ".book-info h1 span a"

  override def volumeTitleRelativeSelector(): String = "h3"

  override def volumesSelector(): String = "#j-catalogWrap .volume"

  override def chapterTitleRelativeSelector(): String = ""

  override def volumeTitleFactory() = (title: String) => {
    val parts = title.split(" |·")
    if (parts.size >= 5)
      parts(2)
    else
      ""
  }
}
