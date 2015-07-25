package strips.pp

import scalatags.Text.all._

trait pp {
  def pp : scalatags.Text.TypedTag[String]
}
