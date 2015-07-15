package strips.lexicon

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

//<MORPH ... />
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

/*object spurious extends App {
  import strips.util.LexiconFromXML

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

  val pickled = write(lex.words)
  val unpickled = read[List[STripsWord]](pickled)

  val nlex = new TripsLexicon(unpickled)

  println(nlex.morphs("break"))
  println(nlex.morphs("breaking"))
  println(nlex.morphs("breaks"))
  println(nlex.morphs("broke"))

  println(nlex.get("broken"))

  println(lex.words == nlex.words)
}*/
