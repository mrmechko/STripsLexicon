package strips.ontology

import com.github.mrmechko.swordnet.SWordNet
import com.github.mrmechko.swordnet.structures.{SRelationType, SKey, SPos}

//JVM only -- Is case class for serialization purposes
case class SOntology(ontItems : List[SOntItem], versioned : Option[String] = None) {
  private def ss2head(s : String) : String = SKey(s).synset.head.key

  private def testWN(s : String) : Option[String] = {
    SKey.get(s) match {
      case Some(k) => Some(k.synset.head.key)
      case None => {
        val r = s.replaceAll("%5", "%3")
        if (r != s){
          println("not found: %s.  Trying: %s".format(s, r))
          testWN(r)
        } else {
          println("didn't find %s".format(s))
          None
        }
      }
    }
  }

  private lazy val index : Map[String, SOntItem] = ontItems.map(x => x.name -> x).toMap
  private lazy val up : Map[String, SOntItem] = ontItems.map(x => {
    x.name -> index.get(x.parent)
  }).filter(_._2.isDefined).map(x => x._1 -> x._2.get).toMap
  private lazy val down : Map[String, List[SOntItem]] = ontItems.map(x => x.name -> x.children.map(y => index(y))).toMap

  ontItems.flatMap(o => {
    o.wn
  }).distinct.foreach(testWN(_))

  private lazy val wordnet: Map[String, List[SOntItem]] =
    ontItems.flatMap(o => {
      o.wn.map(_ -> o)
      .filter(wn => testWN(wn._1).isDefined) //Drops senses not found in 3.1
    }).groupBy(_._1)
    .mapValues(_.map(_._2))
    .map({ case (k,v) => ss2head(k) -> v})

  def get(name : String) : Option[SOntItem] = index.get(name)
  def -->(name : String) = get(name)

  def getWordNetSenseKeyMappings(wn : String) : List[String] = (this !# wn).map(_.name)
  def !#(wn : String) : List[SOntItem] = findSenseClasses(ss2head(wn)).keys.toList
  def !!#(wn : String) : List[(String, List[String])] = findSenseClasses(ss2head(wn)).map(x => x._1.name -> x._2).toList

  def getWordNetWordMappings(word : String) : List[String] = (this !@ word).map(_.name)

  def !@(word : String) : List[SOntItem] = SWordNet.l2S(word).map(_.key).flatMap(t => !#(t)).distinct.toList
  def !!@(word: String) : List[(String, List[String])] = SWordNet.l2S(word).map(_.key).flatMap(t => !!#(t)).toList


  //TODO: For each element on the path-list, if there is another path which is a suffix of it, drop this path
  def findSenseClasses(sense : String, ignore : List[String] = List()) : Map[SOntItem, List[String]] = {
    if(ignore.contains(ss2head(sense))) Map()
    else {
      wordnet.get(ss2head(sense)) match {
        case Some(x) => x.map(_ -> ignore.+:(ss2head(sense))).toMap
        case None => {
          SKey(ss2head(sense)).hasSemantic(SRelationType.hypernym).flatMap(_.keys).distinct.flatMap(k => findSenseClasses(ss2head(k.key), ignore.+:(ss2head(sense)))).distinct.toList.toMap
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

  def ^^(name :  String) : List[String] = {
    val p = (this ^ name).map(_.name).getOrElse("nil")
    if (p == "nil") List()
    else {
      List(p) ++ (this ^^ p)
    }
  }
  def pathToRoot(name : String) : List[String] = this ^^ name
  def pathToRoot(names : List[String]) : Map[String, List[String]] = names.map(name => name -> (this ^^ name)).toMap
  

  def children(name : String) : List[SOntItem] = down.getOrElse(name, List())
  def v(name : String) = children(name)
}
