package strips.ontology

case class SArgument(role : String, optional : Boolean, fltype : String, features : SFeatureSet)

case class SSem(fltype : String, features : SFeatureSet) {
  def <^(other : SSem) : SSem = this.copy(features = this.features <^ other.features)
}

case class SFeatureSet(feats : Map[String, String]) {
  def <^(other : SFeatureSet) : SFeatureSet = {
    //Wont work without the base feature sets
    this.copy(feats = this.feats.foldLeft(other.feats)((a,b) => a.updated(b._1, b._2)))
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
) {
  def <^(other : SOntItem) : SOntItem = this.copy(sem = this.sem <^ other.sem)
  def <^(other : Option[SOntItem]) : SOntItem = other match {
    case Some(o) => this <^ o
    case None => this
  }
}
