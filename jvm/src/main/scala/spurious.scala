object spurious extends App {
  import strips.ontology._
  import strips.util.OntologyFromXML

  val ont = SOntology(OntologyFromXML("/Users/mechko/nlpTools/flaming-tyrion/lexicon/data/"))

  import strips.lexicon._
  import strips.util.LexiconFromXML

  val lex = new TripsLexicon(LexiconFromXML("/Users/mechko/nlpTools/flaming-tyrion/lexicon/data/"))


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
  println(ont.getMerged("bread").get.sem)

  println(ont.ontItems.map(_.name).map(name => ont.get(name).get.sem == ont.getMerged(name).get.sem))

  import upickle.default._

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
}
