package wowServerTracker

import play.api.libs.json._
import scala.io.Source
import java.io.FileInputStream
import scalaj.http._
import scala.sys.process._
import java.io.File
import java.io.PrintWriter
import org.mongodb.scala.MongoClient
import org.bson.codecs.configuration.CodecRegistries.{
  fromProviders,
  fromRegistries
}
import com.mongodb.BasicDBObject
import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.duration.{Duration, SECONDS}


object Project0 {
  def main(args: Array[String]) {
    if(args.length != 2) {
      println("This is a program used to call the battlenet web API to print the server statuses of whether theyre up and their population")
      println("and then store it in a mongodb called 'project0db' in a collection called 'wowservers'")
      println("")
      println("Provide your battlenet oauth info as follows:")
      println("scala wowServerTracker.Project0 [clientId] [clientSecret]")
      return
    }

    try {
      // Get the auth token used for Oauth
      val tokenURL = s"curl -s -u ${args(0)}:${args(1)} -d grant_type=client_credentials https://us.battle.net/oauth/token"
      val token = tokenURL.!!.split("\"")(3)

      // Perform an api call to retrieve an index json
      var json = Json.parse(Http(s"https://us.api.blizzard.com/data/wow/connected-realm/index?namespace=dynamic-us&locale=en_US&access_token=${token}").asString.body)
      
      // Set up for our DAO calls
      val wowServersDAO = new WOWServerDAO(MongoClient())
      implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global
      var serverChecking = Seq[Future[JsValue]]()

      // Iterate through our given index json and parse the server jsons
      for(href <- (json \\ "href").drop(1)) {
        serverChecking :+ Future {
          val serverJson = Json.parse(Http(href.toString.drop(1).dropRight(1) + s"&access_token=${token}").asString.body)
          val status = (serverJson \ "status" \ "type").get.toString.drop(1).dropRight(1)
          val population = (serverJson \ "population" \ "type").get.toString.drop(1).dropRight(1)
          val name = (serverJson \ "realms" \ 0 \ "name" \ "en_US").get.toString.drop(1).dropRight(1)
          val serverId = (serverJson \ "id").get.toString.toInt
          wowServersDAO.createOne(status, population, name, serverId)
          println(s"Server ${name}: (status: ${status}) (population: ${population})")
        }
      }

      // wait for our futures to finish
      Await.result(Future.sequence(serverChecking), Duration(10, SECONDS))
      Thread.sleep(800)
    } 
    // Handle thrown errors
    catch {
      case e: com.fasterxml.jackson.databind.exc.MismatchedInputException => println("Autherization failed, check your arguments and make sure it is [clientId] [clientSecret]")
    }
  }
}
