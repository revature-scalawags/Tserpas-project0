package wowServerTracker

import org.mongodb.scala.bson.codecs.Macros._

import org.mongodb.scala.MongoClient
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.MongoCollection
import scala.concurrent.Await
import org.mongodb.scala.Observable
import scala.concurrent.duration.{Duration, SECONDS}
import org.mongodb.scala.model.Filters.equal
import com.typesafe.scalalogging.LazyLogging

/** Initialize a dao for a connection to the db
  *
  * @param mongoClient a client for initialization
  * {{{
  * val dao = WOWServerDAO(MongoClient())
  * }}}
  */
class WOWServerDAO(mongoClient: MongoClient) extends LazyLogging {
  val codecRegistry = fromRegistries(
    fromProviders(classOf[WOWServer]),
    MongoClient.DEFAULT_CODEC_REGISTRY
  )
  val db = mongoClient.getDatabase("project0db").withCodecRegistry(codecRegistry)
  val collection: MongoCollection[WOWServer] = db.getCollection("wowservers")

  /** ensure the db call finishes
    *
    * @param obs the return value of the db call
    */
  private def getResults[T](obs: Observable[T]): Seq[T] = {
      Await.result(obs.toFuture(), Duration(10, SECONDS))
  }

  /** get all servers we have collected */
  def getAll(): Seq[WOWServer] = getResults(collection.find())

  /** find one server given its name*/
  def getByName(name: String): Seq[WOWServer] = {
      getResults(collection.find(equal("name", name)))
  }

  /** Creates and inserts a WOWServer object into the db */
  def createOne(status: String, population: String, name: String, serverId: Int) {
    getResults(collection.insertOne(WOWServer(status, population, name, serverId)))
  }
}