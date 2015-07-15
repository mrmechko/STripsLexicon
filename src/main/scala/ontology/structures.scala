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
  parent : String,
  children : List[String],
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

case class SOntology(ontItems : List[SOntItem], versioned : Option[String] = None) {

  private lazy val index : Map[String, SOntItem] = ontItems.map(x => x.name -> x).toMap
  private lazy val up : Map[String, SOntItem] = ontItems.map(x => {
    x.name -> index.get(x.parent)
  }).filter(_._2.isDefined).map(x => x._1 -> x._2.get).toMap
  private lazy val down : Map[String, List[SOntItem]] = ontItems.map(x => x.name -> x.children.map(y => index(y))).toMap

  def get(name : String) : Option[SOntItem] = index.get(name)
  def -->(name : String) = get(name)

  def get2(name : String) : Option[SOntItem] = {
    get(name).map(n => n <^ get2(n.parent))
  }
  /*up.get(name) match {
    case Some(x) => {
      get2(x.name).map(index(name) <^ _)
    }//index.get(name).map(_ <^ x)
    case None => index.get(name)
  }*/

  def parent(name : String) : Option[SOntItem] = up.get(name)
  def ^(name : String) = parent(name)

  def children(name : String) : List[SOntItem] = down.getOrElse(name, List())
  def v(name : String) = children(name)
}

object spurious extends App {
  import strips.util.OntologyFromXML

  val ont = SOntology(OntologyFromXML("/Users/mechko/nlpTools/flaming-tyrion/lexicon/data/"))

  //println(ont.get("accept").get)
  //println((ont --> "accept").get)
  println("--")
  println(ont.parent("accept").get.name)
  println((ont ^ "accept").get.name)
  println("--")
  //println(ont.children("accept"))
  //println((ont v "children"))
  println("--")
  println(ont.get("bread").get.sem)
  println(ont.get2("bread").get.sem)

  println(ont.ontItems.map(_.name).map(name => ont.get(name).get.sem == ont.get2(name).get.sem))
}
