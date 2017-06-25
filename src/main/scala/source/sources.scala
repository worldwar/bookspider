package source

import skeleton.{BiqugeParagraphPolicy, TreeCatalogPolicy, DataListCatalogPolicy}


object source {

  import skeleton.CatalogPolicy
  import skeleton.ParagraphPolicy

  case class Source(catalogPolicy: CatalogPolicy, paragraphPolicy: ParagraphPolicy)

  val qidian = new Source(new TreeCatalogPolicy, new BiqugeParagraphPolicy)

  val biquge = new Source(new DataListCatalogPolicy, new BiqugeParagraphPolicy)
}
