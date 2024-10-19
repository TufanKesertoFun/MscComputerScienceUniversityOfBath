ThisBuild / version := "1.0.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.11.10"

lazy val root = (project in file("."))
  .settings(
    name := "Apache-Spark-Demo"
  )

val sparkVersion = "2.4.8" // Use Spark version 2.4.8

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.apache.spark" %% "spark-mllib" % sparkVersion,
  "org.apache.spark" %% "spark-streaming" % sparkVersion,
  "org.apache.spark" %% "spark-avro" % sparkVersion,
  "org.apache.hadoop" % "hadoop-common" % "2.7.7"

)
