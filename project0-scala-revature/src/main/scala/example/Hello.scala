package example

import play.api.libs.json._
import scala.io.Source

object Hello {
  def main(args: Array[String]) {
    try {
      var file = io.Source.fromFile(args(0))
      println((Json.parse(file.mkString) \ "name").get)
    } catch {
      case e: java.io.FileNotFoundException => println("Couldn't find that file.")
      case e: java.io.IOException => println("Had an IOException trying to read that file")
    }
  }
}
