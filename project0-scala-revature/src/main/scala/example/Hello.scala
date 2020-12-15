package example

import play.api.libs.json._
import scala.io.Source

object Hello {
  def main(args: Array[String]) {
    var file = io.Source.fromFile(args(0))
    println((Json.parse(file.mkString) \ "name").get)
  }
}
