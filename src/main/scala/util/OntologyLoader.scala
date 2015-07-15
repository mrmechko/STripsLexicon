package strips.util

import scalaz._
import Scalaz._
import STyper._
import strips.ontology._

object OntTypeBuilder {

  def sargument(role : String @@ Role, optional : Boolean, fltype : String @@ FLType, features : SFeatureSet) : SArgument = SArgument(Tag.unwrap(role), optional, Tag.unwrap(fltype), features)

  def ssem(fltype : String @@ FLType, features : SFeatureSet) : SSem = SSem(Tag.unwrap(fltype), features)

  def sfeatureset(feats : Map[String @@ FType, String @@ FVal]) : SFeatureSet = SFeatureSet(feats.map(k => Tag.unwrap(k._1) -> Tag.unwrap(k._2)))

  def sontitem(
    name : String @@ OntType,
    parent : String @@ OntType,
    children : List[String @@ OntType],
    sem : SSem,
    arguments : List[SArgument],
    words : List[String @@ Word],
    wn : List[String @@ WordNet]
  ) : SOntItem = {
    SOntItem(Tag.unwrap(name), Tag.unwrap(parent), children.map(Tag.unwrap(_)), sem, arguments, words.map(Tag.unwrap), wn.map(Tag.unwrap))
  }
}


object OntologyFromXML {
  import java.io.File
  import scala.xml.{NodeSeq, XML}
  import OntTypeBuilder._

  def getListOfWXML(directoryName: String): Array[String] = {
    (new File(directoryName)).listFiles.filter(f => f.isFile && f.getName.startsWith("ONT_")).map(_.getName)
  }

  def apply(path : String) : List[Any]= {
    printf("loading from %s\n", path)
    getListOfWXML(path).map(file => {
      //println(path+file)
      val f = XML.loadFile(path+file)
      parseOnt((f \\ "ONTTYPE"))
    }).toList
  }

  def parseFeature(n : NodeSeq) : SFeatureSet = {
    if (n.size == 0) sfeatureset(Map())
    else sfeatureset(n.head.attributes.map(k => STags[FType](k.key) -> STags[FVal](k.value.head.text)).toMap)
  }

  def parseSem(n : NodeSeq) : SSem = {
    val fltype : String @@ FLType = STags[FLType]((n \ "@fltype").text)
    val features : SFeatureSet =  parseFeature((n \ "FEATURES"))
    ssem(fltype, features)
    //println(sem)
    //sem
  }

  def parseArg(n : NodeSeq) : SArgument = {
    val role : String @@ Role = STags[Role]((n \ "@role").text)
    val optionality : Boolean = (n \ "@optionality").text == "optional"
    val fltype : String @@ FLType = STags[FLType]((n \ "@fltype").text)

    val features : SFeatureSet =  parseFeature((n \ "FEATURES"))

    sargument(role, optionality, fltype, features)
    //println(arg)
    //arg
  }

  def parseOnt(n : NodeSeq) : Any = {
    val name : String @@ OntType = STags[OntType]((n \ "@name").text)
    //println(name)
    val parent : String @@ OntType = STags[OntType]((n \ "@parent").text)

    val children = (n \\ "CHILD").map(c => STags[OntType]((c \ "@name").text)).toList
    val words = (n \\ "WORD").map(c => STags[Word]((c \ "@name").text)).toList
    val wn = (n \\ "MAPPING").map(c => STags[WordNet]((c \ "@name").text)).toList


    val arguments = (n \\ "ARGUMENT").map(a => parseArg(a)).toList
    val ssem = parseSem(n \ "SEM")

    val ont = sontitem(
      name,
      parent,
      children,
      ssem,
      arguments,
      words,
      wn
    )
    println(ont)
    println("-")
    ont
  }
}
