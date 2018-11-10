lazy val commonSettings = commonSmlBuildSettings ++ ossPublishSettings ++ Seq(
  organization := "com.softwaremill.sapi",
  scalaVersion := "2.12.7",
  scalafmtOnCompile := true
)

val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5" % "test"
val circeVersion = "0.10.1"
val sttpVersion = "1.5.0-SNAPSHOT"

lazy val rootProject = (project in file("."))
  .settings(commonSettings: _*)
  .settings(publishArtifact := false, name := "sapi")
  .aggregate(core, openapiModel, openapiCirce, openapiCirceYaml, openapiDocs, akkaHttpServer, http4sServer, sttpClient)

lazy val core: Project = (project in file("core"))
  .settings(commonSettings: _*)
  .settings(
    name := "core",
    libraryDependencies ++= Seq(
      "com.chuusai" %% "shapeless" % "2.3.3",
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,
      "com.propensive" %% "magnolia" % "0.10.0",
      scalaTest
    )
  )

lazy val openapiModel: Project = (project in file("openapi/openapi-model"))
  .settings(commonSettings: _*)
  .settings(
    name := "openapi-model"
  )

lazy val openapiCirce: Project = (project in file("openapi/openapi-circe"))
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-magnolia-derivation" % "0.3.0"
    ),
    name := "openapi-circe"
  )
  .dependsOn(openapiModel)

lazy val openapiCirceYaml: Project = (project in file("openapi/openapi-circe-yaml"))
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-yaml" % "0.9.0"
    ),
    name := "openapi-circe-yaml"
  )
  .dependsOn(openapiCirce)

lazy val openapiDocs: Project = (project in file("docs/openapi-docs"))
  .settings(commonSettings: _*)
  .settings(
    name := "openapi-docs"
  )
  .dependsOn(openapiModel, core)

lazy val akkaHttpServer: Project = (project in file("server/akka-http-server"))
  .settings(commonSettings: _*)
  .settings(
    name := "akka-http-server",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http" % "10.1.5",
      "com.typesafe.akka" %% "akka-stream" % "2.5.18"
    )
  )
  .dependsOn(core)

lazy val http4sServer: Project = (project in file("server/http4s-server"))
  .settings(commonSettings: _*)
  .settings(
    name := "http4s-server",
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-blaze-client" % "0.18.21"
    )
  )
  .dependsOn(core)

lazy val sttpClient: Project = (project in file("client/sttp-client"))
  .settings(commonSettings: _*)
  .settings(
    name := "sttp-client",
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp" %% "core" % sttpVersion
    )
  )
  .dependsOn(core)

lazy val tests: Project = (project in file("tests"))
  .settings(commonSettings: _*)
  .settings(
    name := "tests",
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp" %% "akka-http-backend" % sttpVersion
    )
  )
  .dependsOn(akkaHttpServer, sttpClient, openapiCirceYaml, openapiDocs)
