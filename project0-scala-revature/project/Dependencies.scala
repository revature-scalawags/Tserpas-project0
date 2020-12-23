import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.2.2"
  lazy val play = "com.typesafe.play" %% "play-json" % "2.8.0"
  lazy val mongoDriver = "org.mongodb.scala" % "mongo-scala-driver_2.13" % "4.2.0-beta1"
  lazy val scalaj = "org.scalaj" %% "scalaj-http" % "2.4.2"
  lazy val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
  lazy val logback = "ch.qos.logback" % "logback-classic" % "1.2.3"
}
