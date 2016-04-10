import sbt._
import Keys._

object ProjectBuild extends Build {

  lazy val root = Project(
    id = "activator-play-tracing",
    base = file("."),
    settings =
      Seq (
        name := "activator-play-tracing",
        organization := "com.github.levkhomich",
        version := "0.5-SNAPSHOT",

        homepage := Some(url("https://github.com/levkhomich/akka-tracing")),
        licenses := Seq("Apache Public License 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),

        scalaVersion := "2.11.8",
        crossScalaVersions := Seq("2.10.4", "2.11.8"),
        scalacOptions in GlobalScope ++= Seq("-Xcheckinit", "-Xlint", "-deprecation", "-unchecked", "-feature", "-language:_"),
        scalacOptions in Test ++= Seq("-Yrangepos"),

        publish := (),

//        resolvers += "Maven Central Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
        libraryDependencies ++=
          Seq(
            play.sbt.PlayImport.ws,
            "org.webjars" % "bootstrap" % "2.3.1",
            "org.webjars" % "angularjs" % "1.2.16",
            "com.typesafe.akka" %% "akka-actor" % "2.4.3",
            "com.github.levkhomich" %% "akka-tracing-play" % "0.5-SNAPSHOT"
          ) ++ (
            if (scalaVersion.value == "2.11.8")
              Seq("org.scala-lang.modules" %% "scala-xml" % "1.0.3")
            else
              Seq.empty
          )
      )
  ).enablePlugins(play.sbt.PlayScala)
}
