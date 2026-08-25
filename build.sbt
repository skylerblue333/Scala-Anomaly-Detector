ThisBuild / scalaVersion := "3.3.3"
ThisBuild / version := "0.1.0"
ThisBuild / organization := "com.skycoin4444"

lazy val root = (project in file("."))
  .settings(
    name := "sky-anomaly-detector",
    libraryDependencies += "org.scalameta" %% "munit" % "1.0.2" % Test
  )
