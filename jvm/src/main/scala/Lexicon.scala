package strips.lexicon

//JVM only
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

  def %(word : String) : Set[String] = morphs(word)

  def get(word : String) : Set[STripsWord] = {
    morphs(word).flatMap(wordIndex(_))
  }

  def -->(word : String) : Set[STripsWord] = get(word)

  def !(word : String) : List[String] = (-->(word)).flatMap(_.classes.map(_.ontType)).toList
}
