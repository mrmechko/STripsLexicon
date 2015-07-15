package strips.util

import scalaz._
import Scalaz._

//Ontology types:
object STyper {
  trait OntType
  trait FType
  trait FVal
  trait FLType
  trait Role
  trait WordNet
  trait ArgRole
  trait SemFrame
  trait Example
  trait Gloss
  trait Word
  trait LexFrame
  trait Cat
  trait Pos

  object Cat {
    val base = "base"
    def apply(form : String) : String = form
  }

  object STags {
    def apply[A](a : String) : String @@ A = Tag[String, A](a)
  }
}
