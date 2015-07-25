package strips.ontology

import scalatags.Text.all._
import strips.pp._

case class SArgument(role : String, optional : Boolean, fltype : String, features : SFeatureSet) extends pp {
  override def pp : scalatags.Text.TypedTag[String] = span(role)
}

case class SSem(fltype : String, features : SFeatureSet) extends pp {
  def <^(other : SSem) : SSem = this.copy(features = this.features <^ other.features)
  override def pp : scalatags.Text.TypedTag[String] = span(fltype)
}

case class SFeatureSet(feats : Map[String, String]) extends pp {
  def <^(other : SFeatureSet) : SFeatureSet = {
    //Wont work without the base feature sets
    this.copy(feats = this.feats.foldLeft(other.feats)((a,b) => a.updated(b._1, b._2)))
  }
  override def pp : scalatags.Text.TypedTag[String] = {
    ul(cls := "sfeatureset")(
      for (f <- feats.toList) yield li(cls := "sfeature")(span(cls := "sfeaturename")(f._1), span(cls := "sfeatureval")(f._2))
    )
  }
}

case class SOntItem(
  name : String,
  parent : String,          //can be extracted into a tree structure
  children : List[String],  //leaving only sem, arguments and lex as members of the node
  sem : SSem,
  arguments : List[SArgument],
  words : List[String],
  wn : List[String]
) extends pp {
  def <^(other : SOntItem) : SOntItem = this.copy(sem = this.sem <^ other.sem)
  def <^(other : Option[SOntItem]) : SOntItem = other match {
    case Some(o) => this <^ o
    case None => this
  }

  override def pp : scalatags.Text.TypedTag[String] = {
    div(cls := "sont")(
      span(cls := "sontName")(name),
      span(cls := "sontParent")(parent),
      ul(cls := "sontChildren")(
        for (c <- children) yield li(cls := "sontChild")(c)
      ),
      sem.pp,
      ul(cls := "sontSargs")(
        for (a <- arguments) yield li(cls := "sontSarg")(a.pp)
      ),
      ul(cls := "sontWords")(
        for (w <- words) yield li(cls := "sontWord")(w)
      ),
      ul(cls := "sontWN")(
        for (w <- wn) yield li(cls := "sontWN")(w)
      )
    )
  }
}
