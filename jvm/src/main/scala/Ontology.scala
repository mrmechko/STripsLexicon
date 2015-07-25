package strips.ontology

import com.github.mrmechko.swordnet.SWordNet
import com.github.mrmechko.swordnet.structures.{SRelationType, SKey, SPos}

//JVM only -- Is case class for serialization purposes
case class SOntology(ontItems : List[SOntItem], versioned : Option[String] = None) {

  private lazy val index : Map[String, SOntItem] = ontItems.map(x => x.name -> x).toMap
  private lazy val up : Map[String, SOntItem] = ontItems.map(x => {
    x.name -> index.get(x.parent)
  }).filter(_._2.isDefined).map(x => x._1 -> x._2.get).toMap
  private lazy val down : Map[String, List[SOntItem]] = ontItems.map(x => x.name -> x.children.map(y => index(y))).toMap
  private lazy val wordnet: Map[String, List[SOntItem]] = ontItems.flatMap(o => o.wn.map(_ -> o)).groupBy(_._1).mapValues(_.map(_._2))

  def get(name : String) : Option[SOntItem] = index.get(name)
  def -->(name : String) = get(name)

  def !#(wn : String) : List[SOntItem] = findSenseClasses(wn).keys.toList//wordnet.getOrElse(wn, List())
  def !!#(wn : String) : Map[String, List[String]] = findSenseClasses(wn).map(x => x._1.name -> x._2)

  //TODO: For each element on the path-list, if there is another path which is a suffix of it, drop this path
  def findSenseClasses(sense : String, ignore : List[String] = List()) : Map[SOntItem, List[String]] = {
    if(ignore.contains(sense)) Map()
    else {
      wordnet.get(sense) match {
        case Some(x) => x.map(_ -> ignore.+:(sense)).toMap
        case None => {
          SKey(sense).hasSemantic(SRelationType.hypernym).flatMap(_.keys).distinct.flatMap(k => findSenseClasses(k.key, ignore.+:(sense))).distinct.toList.toMap
        }
      }
    }
  }

  def getMerged(name : String) : Option[SOntItem] = {
    get(name).map(n => n <^ getMerged(n.parent))
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
