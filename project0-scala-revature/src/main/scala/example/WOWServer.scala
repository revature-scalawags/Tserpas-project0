package wowServerTracker

import org.bson.types.ObjectId

/** for use in standardizing data about the collected wow servers 
  *
  * @param status whether the server is up or down
  * @param population how populated the server is
  * @param name the actual name of the server
  * @param serverId the id of the server used in api calls
  */ 
case class WOWServer(_id: ObjectId, status: String, population: String, name: String, serverId: Int) {}

/** gives a WOWServer object an ObjectId() for mongodb entries if one is not specified */
object WOWServer {
  def apply(status: String, population: String, name: String, serverId: Int): WOWServer =
    WOWServer(new ObjectId(), status, population, name, serverId)
}