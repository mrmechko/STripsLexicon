package strips.lexicon

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
