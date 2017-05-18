package domain

import table.DB.{BookCase, ChapterCase, VolumeCase}

class Book(_book: BookCase, _volumes: Seq[Volume]) {
  def book = _book

  def volumes = _volumes

}

class Volume(_volume: VolumeCase, _chapters: Seq[ChapterCase]) {
  def volume = _volume

  def chapters = _chapters
}

