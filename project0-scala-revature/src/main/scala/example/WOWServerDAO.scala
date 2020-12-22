package wowServerTracker

import org.mongodb.scala.bson.codecs.Macros._

import org.mongodb.scala.MongoClient
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.MongoCollection
import scala.concurrent.Await
import org.mongodb.scala.Observable
import scala.concurrent.duration.{Duration, SECONDS}
import org.mongodb.scala.model.Filters.equal

/** Initialize a dao for a connection to the db
  *
  * @param mongoClient a client for initialization, if you dont have one you can 
  * just do val dao = WOWServerDAO(MongoClient())
  */
class WOWServerDAO(mongoClient: MongoClient) {
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

  /** find one server given its @param name */
  def getByName(name: String): Seq[WOWServer] = {
      getResults(collection.find(equal("name", name)))
  }

  /** Creates and inserts a WOWServer object into the db
    *
    * @param status whether the server is up or down
    * @param population how populated the server is
    * @param name the actual name of the server
    * @param serverId the id of the server used in api calls
    */
  def createOne(status: String, population: String, name: String, serverId: Int) {
    getResults(collection.insertOne(WOWServer(status, population, name, serverId)))
  }
}