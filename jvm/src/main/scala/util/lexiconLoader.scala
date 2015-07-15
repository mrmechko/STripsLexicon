package strips.util

import strips.lexicon._
import scalaz._
import Scalaz._

import STyper._

/**
 * Typesafe builders for the lexicon
 **/
object LexiconObjectBuilder {
  def smorph(cat : String @@ Cat, to : String) =
    SMorph(Tag.unwrap(cat), to)
  def stripsword(
    word : String @@ Word,
    pos : String @@ Pos,
    classes : List[SLexClass],
    morphs : List[SMorph]
  ) : STripsWord = STripsWord(Tag.unwrap(word), Tag.unwrap(pos), classes, morphs)

  def slexframe(desc : String @@ LexFrame, example : String @@ Example) : SLexFrame = {
    SLexFrame(Tag.unwrap(desc), Tag.unwrap(example))
  }

  def slexclass(
    words : List[String @@ Word],
    pos : String @@ Pos,
    ontType : String @@ OntType,
    frames : List[SLexFrame]
  ) : SLexClass = SLexClass(words <*> List(Tag.unwrap(_)), Tag.unwrap(pos), Tag.unwrap(ontType), frames)
}

object LexiconFromXML {
  import java.io.File
  import scala.xml.{NodeSeq, XML}
  import LexiconObjectBuilder._

  def getListOfWXML(directoryName: String): Array[String] = {
    (new File(directoryName)).listFiles.filter(f => f.isFile && f.getName.startsWith("W_")).map(_.getName)
  }

  def apply(path : String) : List[STripsWord]= {
    printf("loading from %s\n", path)
    getListOfWXML(path).flatMap(file => {
      //println(path+file)
      val f = XML.loadFile(path+file)
      parseWord(f \\ "WORD")
    }).toList
  }

  def parseWord(n : NodeSeq) : List[STripsWord] = {
    val name : String @@ Word = STags[Word]((n \ "@name").text)

    (n \\ "POS").map(p => {
      val pos : String @@ Pos = STags[Pos]((p \ "@name").text)
      val morphs : List[SMorph] = (p \\ "MORPH").map(m => {
        smorph(STags[Cat]((m \ "@cat").text), (m \ "@to").text)
      }).toList.+:(smorph(STags[Cat]("base"), Tag.unwrap(name))) //add a 'base' to everything
      val classes = (p \\ "CLASS").map(c => {
        val ot : String @@ OntType= STags[OntType]((c \ "@onttype").text)
        val words : List[String @@ Word] = (c \ "@words").text.split(",").toList.map(STags[Word](_))
        val frames : List[SLexFrame] = (c \\ "FRAME").map(f => {
          slexframe(
            STags((f \ "@desc").text),
            STags((f \ "@example").text)
          )
        }).toList

        slexclass(words, pos, ot, frames)
      }).toList
      stripsword(name, pos, classes, morphs)
    }).toList
  }
}
