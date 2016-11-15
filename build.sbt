name := "gressy-entities"
organization := "com.github.gressy"
version := "0.0.2"

homepage := Some(url("https://github.com/gressy/gressy-entities"))
licenses := Seq("BSD-style" -> url("http://www.opensource.org/licenses/bsd-license.php"))

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "com.typesafe.play" % "play_2.11" % "2.5.9",
  "com.typesafe.play" % "play-java-jpa_2.11" % "2.5.9",
  "org.hibernate" % "hibernate-entitymanager" % "5.2.4.Final",
  "com.fasterxml.jackson.datatype" % "jackson-datatype-hibernate5" % "2.8.5",
  "org.mindrot" % "jbcrypt" % "0.3m"
)

publishMavenStyle := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra :=
  <scm>
    <url>git@github.com:gressy/gressy-entities.git</url>
    <connection>scm:git:git@github.com:gressy/gressy-entities.git</connection>
    <developerConnection>scm:git:git@github.com:gressy/gressy-entities.git</developerConnection>
  </scm>
  <developers>
    <developer>
      <name>Anxo Soto</name>
      <email>mancontr@gmail.com</email>
      <url>https://github.com/mancontr</url>
    </developer>
    <developer>
      <name>Miguel Sanchez</name>
      <email>mschezes@gmail.com</email>
      <url>https://github.com/mschez</url>
    </developer>
  </developers>
