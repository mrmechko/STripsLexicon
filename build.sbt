name := "strips2"

lazy val root = project.in(file(".")).
  aggregate(stripsJS, stripsJVM).
  settings(
    publish := {},
    publishLocal := {}
  )

lazy val strips = crossProject.in(file(".")).
  settings(
    name := "strips2",
    version := "0.0.2",
    scalaVersion := "2.11.6",
    libraryDependencies += "com.lihaoyi" %%% "upickle" % "0.3.4"
  ).
  jvmSettings(
    // Add JVM-specific settings here
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
