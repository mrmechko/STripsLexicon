
package wn
object bottle {

  import upickle.default._
  import io.Source._

  private object Caches {
    var lemmas = Map[String, WordNetResponse]()
    var keys = Map[String, WordNetResponse]()
    var heads = {

      fromInputStream(getClass.getResourceAsStream("/heads")).mkString.split("\n").flatMap(k=>{
        val line = k.split(" ").toList
        line.map(s => s -> line.head)
      })
    }.toMap

    var hypernyms = {
      fromInputStream(getClass.getResourceAsStream("/hypernyms")).mkString.split("\n").flatMap(k=>{
        val line = k.split(" ").toList
        line.map(s => line.head -> line.tail)
      })
    }.toMap
  }

  sealed trait WordNetResponse

  case class SimpleKey(key : String, hypernyms : List[String]) extends WordNetResponse

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
        Caches.lemmas = Caches.lemmas.updated(lem, res)
        res
      }
    }
  }

  def key(k : String) : WordNetResponse = {
    Caches.keys.get(k) match {
      case Some(l) => l
      case None => {
        val res = read[WordNetResponse](scala.io.Source.fromURL("http://localhost:5000/key?query=%s".format(k)).mkString)
        Caches.keys = Caches.keys.updated(k, res)
        res
      }
    }
  }

  def simpleKey(k : String) : WordNetResponse = {
    Caches.heads.get(k) match {
      case Some(l) => SimpleKey(l, Caches.hypernyms.getOrElse(l, List()))
      case None => WNError("%s not found".format(k))
    }
  }

}
