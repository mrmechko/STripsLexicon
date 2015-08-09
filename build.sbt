name := "strips2"

val commonSettings = Seq(
  organization := "com.github.mrmechko",
  normalizedName ~= { _.replace("scala-js", "scalajs") },
  homepage := Some(url("https://github.com/mrmechko/STripsLexicon")),
  licenses += ("BSD 3-Clause", url("http://opensource.org/licenses/BSD-3-Clause")),
  scmInfo := Some(ScmInfo(
        url("https://github.com/mrmechko/STripsLexicon"),
        "scm:git:git@github.com:mrmechko/STripsLexicon.git",
        Some("scm:git:git@github.com:mrmechko/STripsLexicon.git"))),
  publishMavenStyle := true,

  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases" at nexus + "service/local/staging/deploy/maven2")
  },

  pomExtra := (
  <developers>
    <developer>
      <id>mrmechko</id>
      <name>Rik Bose</name>
      <url>https://github.com/mrmechko/</url>
    </developer>
  </developers>
  ),

  pomIncludeRepository := { _ => false }


)

lazy val root = project.in(file(".")).
  settings(commonSettings: _*).
  settings(
    publish := {},
    publishLocal := {}
  ).
  aggregate(stripsJS, stripsJVM)

lazy val strips = crossProject.in(file(".")).
  settings(commonSettings: _*).
  settings(
    version := "0.0.3",
    name := "strips2",
    scalaVersion := "2.11.6",
    libraryDependencies ++= Seq(
      "com.lihaoyi" %%% "upickle" % "0.3.4",
      "com.lihaoyi" %%% "scalatags" % "0.5.2"
    )
  ).
  jvmSettings(
    // Add JVM-specific settings here
    version := "0.0.3",
    libraryDependencies ++= Seq(
      "com.github.mrmechko" %% "swordnet" % "2.0-SNAPSHOT",
      "org.scalaz" %% "scalaz-core" % "7.1.3",
      "org.scala-lang.modules" %% "scala-xml" % "1.0.3"
    )
  ).
  jsSettings(
    // Add JS-specific settings here
  )

lazy val stripsJVM = strips.jvm
lazy val stripsJS = strips.js
