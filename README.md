# STripsLexicon

## Very fast guide to getting started

```scala
import strips.ontology._
import strips.util.OntologyFromXML
  
val ont = SOntology(OntologyFromXML("/Users/mechko/nlpTools/flaming-tyrion/lexicon/data/"))

ont --> "accept"  //accept
ont ^ "accept"    //parent of accept
ont v "accept"    //children of accept

import strips.lexicon._
import strips.util.LexiconFromXML
  
val lex = new TripsLexicon(LexiconFromXML("/Users/mechko/nlpTools/flaming-tyrion/lexicon/data/"))
```
