import Dependencies._

lazy val scala2Version = "2.13.8"
lazy val scala3Version = "3.1.2"

lazy val demoValentin = project
  .in(file("demo-valentin"))
  .settings(
    scalaVersion := scala2Version,
    libraryDependencies ++= Seq(
      scalameta
    )
  )
  .dependsOn(syntactic, featuresSetComputer, functionalConverter)

lazy val semantic = project
  .in(file("semantic"))
  .settings(
    scalaVersion := scala3Version
  )

lazy val syntactic = project
  .in(file("syntactic"))
  .settings(
    scalaVersion := scala2Version,
    libraryDependencies ++= Seq(
      scalameta,
      junit,
      junitInterface
    )
  )

lazy val featuresSetComputer = project
  .in(file("features-set-computer"))
  .settings(
    moduleName := "features-set-computer",
    scalaVersion := scala2Version,
    libraryDependencies ++= Seq(
      scalameta,
      junit,
      junitInterface
    )
  ).dependsOn(syntactic)

lazy val functionalConverter = project
  .in(file("functional-converter"))
  .settings(
    moduleName := "functional-converter",
    scalaVersion := scala2Version,
    libraryDependencies ++= Seq(
      scalameta,
      junit,
      junitInterface
    )
  ).dependsOn(syntactic)

lazy val testkit = project
  .in(file("testkit"))
  .settings(
    scalaVersion := scala2Version,
    libraryDependencies ++= Seq(
      scalameta,
      scalaParserCombinators
    )
  )
  .dependsOn(syntactic, semantic)

lazy val testsInput = project
  .in(file("tests/input"))
  .settings(
    scalaVersion := scala3Version
  )

lazy val testsUnit = project
  .in(file("tests/unit"))
  .settings(
    scalaVersion := scala3Version,
    libraryDependencies += munit,
    Compile / compile / compileInputs := {
      (Compile / compile / compileInputs)
        .dependsOn(testsInput / Compile / compile)
        .value
    },
    fork := true,
    javaOptions += {
      val testsInputProduct = (testsInput / Compile / scalaSource).value
      s"-Dtests-input=$testsInputProduct"
    }
  )
  .dependsOn(testkit)
