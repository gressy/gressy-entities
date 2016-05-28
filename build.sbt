name := "gressy-entities"
organization := "com.github.gressy"
version := "0.0.1"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "com.typesafe.play" % "play_2.11" % "2.5.3",
  "com.typesafe.play" % "play-java-jpa_2.11" % "2.5.3",
  "org.hibernate" % "hibernate-entitymanager" % "5.1.0.Final",
  "com.fasterxml.jackson.datatype" % "jackson-datatype-hibernate5" % "2.6.1",
  "org.mindrot" % "jbcrypt" % "0.3m"
)
