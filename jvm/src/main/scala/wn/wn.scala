
package wn
object bottle {

  import upickle.default._

  private object Caches {
    var lemmas = Map[String, WordNetResponse]()
    var keys = Map[String, WordNetResponse]()
  }

  sealed trait WordNetResponse

  case class SingleKey(
    key : String,
    num : String,
    pos : String,
    word : String
  )

  case class Synset(
    sense : String,
    gloss : String,
    hypernyms : List[String],
    hyponyms : List[String],
    keys : List[String],
    canonical : String,
    examples : List[String],
    thiskey : SingleKey
  ) extends WordNetResponse

  case class WNError(error : String) extends WordNetResponse

  case class WordList(
    word : String,
    synsets : List[Synset]
  ) extends WordNetResponse

  def lemma(lem : String) : WordNetResponse = {
    Caches.lemmas.get(lem) match {
      case Some(l) => l
      case None => {
        val res = read[WordNetResponse](scala.io.Source.fromURL("http://localhost:5000/word?query=%s".format(lem)).mkString)
        res match {
          case s : WordList => Caches.lemmas = Caches.lemmas.updated(lem, res)
          case _ => 
        }
        res
      }
    }
  }

  def key(k : String) : WordNetResponse = {
    Caches.keys.get(k) match {
      case Some(l) => l
      case None => {
        val res = read[WordNetResponse](scala.io.Source.fromURL("http://localhost:5000/key?query=%s".format(k)).mkString)
        res match {
          case s : Synset => Caches.keys = Caches.keys.updated(k, res)
          case _ => 
        }
        res
      }
    }
  }

}
