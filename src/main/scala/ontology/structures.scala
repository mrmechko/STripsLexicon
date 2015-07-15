package strips.ontology

case class SArgument(role : String, optional : Boolean, fltype : String, features : SFeatureSet)

case class SSem(fltype : String, features : SFeatureSet)

case class SFeatureSet(feats : Map[String, String])

case class SOntItem(name : String, parent : String, children : List[String], sem : SSem, arguments : List[SArgument], words : List[String], wn : List[String])
