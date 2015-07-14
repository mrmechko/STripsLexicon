package strips

import scalaz._
import Scalaz._

//Ontology types:

trait OntType
trait Feature
trait FeatureVal
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
  val base = STags[Cat]("base")
  def apply(form : String) : String @@ Cat = STags[Cat](form)
}

object STags {
  def apply[A](a : String) : String @@ A = Tag[String, A](a)
}
