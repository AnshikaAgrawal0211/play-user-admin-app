name := """play-app"""

version := "2.7.x"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.0"

libraryDependencies += guice
libraryDependencies += "com.typesafe.play" %% "play-slick" % "4.0.2"
libraryDependencies += "com.typesafe.play" %% "play-slick-evolutions" % "4.0.2"

libraryDependencies += "com.h2database" % "h2" % "1.4.199"

libraryDependencies += specs2 % Test
libraryDependencies +=   "org.postgresql" % "postgresql" % "42.1.4"
libraryDependencies += "org.mindrot" % "jbcrypt" % "0.4"
scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-Xfatal-warnings"
)
