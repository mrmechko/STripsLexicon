package strips.ontology

//JVM only -- Is case class for serialization purposes
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
