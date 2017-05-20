package table

import java.sql.Timestamp

import org.joda.time.DateTime
import org.joda.time.DateTimeZone._
import slick.jdbc.PostgresProfile.api._

object DB {

  case class BookCase(key: Int,
                      id: String,
                      title: String,
                      author: String,
                      authorId: Option[String],
                      originalUrl: Option[String],
                      createdDate: Option[DateTime],
                      importDate: Option[DateTime]
                     )

  case class VolumeCase(key: Int,
                        id: String,
                        title: Option[String],
                        bookId: String,
                        seq: Int
                       )

  case class ChapterCase(key: Int,
                         id: String,
                         title: Option[String],
                         bookId: String,
                         volumeId: String,
                         seq: Int,
                         originalUrl: Option[String],
                         paragraphId: Option[String]
                        )

  case class ParagraphCase(key: Int,
                          id: String,
                          content: Option[String]
                         )

  val connectionUrl = "jdbc:postgresql://localhost:5432/readinglist"

  val db = Database.forURL(url = connectionUrl, user = "readinglist_dev", password = "readinglist_dev", driver = "org.postgresql.Driver")

  val books = TableQuery[Books]
  val volumes = TableQuery[Volumes]
  val chapters = TableQuery[Chapters]
  val paragraphs = TableQuery[Paragraphs]

  def insertBook(seq: BookCase) = {
    val inserts = DBIO.seq(
      books += BookCase.unapply(seq).get
    )
    db.run(inserts)
  }

  def insertVolume(seq: VolumeCase) = {
    val inserts = DBIO.seq(
      volumes += VolumeCase.unapply(seq).get
    )
    db.run(inserts)
  }

  def insertChapter(seq: ChapterCase) = {
    val inserts = DBIO.seq(
      chapters += ChapterCase.unapply(seq).get
    )
    db.run(inserts)
  }

  def insertParagraph(seq: ParagraphCase) = {
    val inserts = DBIO.seq(
      paragraphs += ParagraphCase.unapply(seq).get
    )
    db.run(inserts)
  }

  def updateChapterWithParagraph(chapter: String, paragraph: String): Unit = {
    val q = for {c <- chapters if c.id === chapter} yield c.paragraphId
    val updateAction = q.update(Some(paragraph))
    db.run(updateAction)
  }

  implicit val jodaDateTimeType =
    MappedColumnType.base[DateTime, Timestamp](
      dt => new Timestamp(dt.getMillis),
      ts => new DateTime(ts.getTime, UTC))

  class Books(tag: Tag) extends Table[(Int, String, String, String, Option[String], Option[String], Option[DateTime], Option[DateTime])](tag, "book") {
    def key = column[Int]("key", O.PrimaryKey, O.AutoInc)

    def id = column[String]("id")

    def title = column[String]("title")

    def author = column[String]("author")

    def authorId = column[Option[String]]("author_id")

    def originalUrl = column[Option[String]]("original_url")

    def createDate = column[Option[DateTime]]("create_date")

    def importDate = column[Option[DateTime]]("import_date")

    def * = (key, id, title, author, authorId, originalUrl, createDate, importDate)
  }

  class Volumes(tag: Tag) extends Table[(Int, String, Option[String], String, Int)](tag, "volume") {
    def key = column[Int]("key", O.PrimaryKey, O.AutoInc)

    def id = column[String]("id")

    def title = column[Option[String]]("title")

    def bookId = column[String]("book_id")

    def seq = column[Int]("seq")

    def * = (key, id, title, bookId, seq)
  }

  class Chapters(tag: Tag) extends Table[(Int, String, Option[String], String, String, Int, Option[String], Option[String])](tag, "chapter") {
    def key = column[Int]("key", O.PrimaryKey, O.AutoInc)

    def id = column[String]("id")

    def title = column[Option[String]]("title")

    def bookId = column[String]("book_id")

    def volumeId = column[String]("volume_id")

    def seq = column[Int]("seq")

    def originalUrl = column[Option[String]]("original_url")

    def paragraphId = column[Option[String]]("paragraph_id")

    def * = (key, id, title, bookId, volumeId, seq, originalUrl, paragraphId)
  }

  class Paragraphs(tag: Tag) extends Table[(Int, String, Option[String])](tag, "paragraph") {
    def key = column[Int]("key", O.PrimaryKey, O.AutoInc)

    def id = column[String]("id")

    def content = column[Option[String]]("content")

    def * = (key, id, content)
  }
}
