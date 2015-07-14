package strips

import com.github.mrmechko.swordnet.structures.SPos

import scalaz._
import Scalaz._

import upickle._


class TripsLexicon(val words : List[STripsWord]) {
  private val wordIndex = words.map(w => w.word -> w).groupBy(_._1).mapValues(x => x.map(_._2))
  //Map[String @@ Cat, List[STripsWord]]
  private val morphIndex = words.flatMap(w => w.morphs.map(m => m -> w)).groupBy(_._1).mapValues(v => v.map(_._2))

  val entries : (String) => Map[String, List[STripsWord]] = (form : String) => {
    morphIndex.filter(_._1.to == form).map(m => m._1.cat -> m._2)
  }

  //Return all entries for a particular form of the word
  def morphs(word : String) : Set[String] = {
    entries(word).values.flatMap(x => x.map(_.word)).toSet
  }

  def get(word : String) : Set[STripsWord] = {
    morphs(word).flatMap(wordIndex(_))
  }
}

/**
 * cat : the categorization
 * base : the word classes for this morph instance
 * to : the morphological variant
 */
case class SMorph(cat : String, to : String)

case class STripsWord(
  word : String,
  pos : String,
  classes : List[SLexClass],
  morphs : List[SMorph]
) {
  def u : String = {
    Seq("\n\tword: " + word,
    "\tpos: "+pos,
    "\tclasses:",
    classes.mkString("\t\t", "\n\t\t", "\n"),
    "\tmorphs:",
    morphs.mkString("\t\t", "\n\t\t", "\n")).mkString("\n")
  }
}

case class SLexFrame(
  desc : String,
  example : String
)

case class SLexClass(
  words : List[String],
  pos : String,
  ontType : String,
  frames : List[SLexFrame]
)



object LexiconFromXML {
  import java.io.File
  import scala.xml.{NodeSeq, XML}

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
    val name : String = STags[Word]((n \ "@name").text)

    (n \\ "POS").map(p => {
      val pos = STags[Pos]((p \ "@name").text)
      val morphs : List[SMorph] = (p \\ "MORPH").map(m => {
        SMorph(STags[Cat]((m \ "@cat").text), (m \ "@to").text)
      }).toList.+:(SMorph("base", name)) //add a 'base' to everything
      val classes = (p \\ "CLASS").map(c => {
        val ot : String = STags[OntType]((c \ "@onttype").text)
        val words : List[String] = (c \ "@words").text.split(",").toList.map(STags[Word](_))
        val frames : List[SLexFrame] = (c \\ "FRAME").map(f => {
          SLexFrame(
            STags((f \ "@desc").text),
            STags((f \ "@example").text)
          )
        }).toList

        SLexClass(words, pos, ot, frames)
      }).toList
      STripsWord(name, pos, classes, morphs)
    }).toList
  }
}

  /*
  def readTripsLexiconKeys(path2directory:String = defaultPath): List[STripsWord] = {
    System.err.println("loading words from "+path2directory)
    getListOfSubDirectories(path2directory, "W_").flatMap(e => {
      val f = XML.loadFile(path2directory+e)
      val word = (f \\ "WORD" \ "@name").text
      //printf("W::%s\n", word)

      (f \ "POS").map(n => {
        val types = ((n \ "CLASS") \\ "@onttype").map(onttype => STripsOntName.build(onttype.text)).toList
        STripsWord.build(value = word, pos = SPos((n \\ "@name").text), ontTypes = types)
      }).map(x => {
        //println(x)
        x
      })
    }).toList
  }*/
object spurious extends App {

  val lex = new TripsLexicon(LexiconFromXML("/Users/mechko/nlpTools/flaming-tyrion/lexicon/data/"))

  lex.entries("break").foreach(x => {
    println("form: "+x._1)
    println(x._2.map(_.u))
  })

  println(lex.morphs("break"))
  println(lex.morphs("breaking"))
  println(lex.morphs("breaks"))
  println(lex.morphs("broke"))

  println(lex.get("broken"))

  println(write(lex.words))
}
