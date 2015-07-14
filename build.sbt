name := "strips2"

version := "0.0.1"

scalaVersion := "2.11.6"

resolvers += Resolver.sonatypeRepo("releases")

resolvers += Resolver.sonatypeRepo("snapshots")


val lensVersion = "1.1.1"   // or "1.2.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "com.lihaoyi" %% "upickle" % "0.2.8",
  "com.github.mrmechko" %% "swordnet" % "2.0-SNAPSHOT",
  "org.scalaz" %% "scalaz-core" % "7.1.3",
  "org.scala-lang.modules" %% "scala-xml" % "1.0.3",
  "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test"
)

//addCompilerPlugin("org.scalamacros" %% "paradise" % "2.0.1" cross CrossVersion.full)


//Publishing to Sonatype

publishMavenStyle := true

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

publishTo <<= version { (v: String) =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := (
  <url>http://mrmechko.github.io/STripsLexicon</url>
    <licenses>
      <license>
        <name>BSD-style</name>
        <url>http://www.opensource.org/licenses/bsd-license.php</url>
        <distribution>repo</distribution>
      </license>
    </licenses>
    <scm>
      <url>git@github.com:mrmechko/strips.git</url>
      <connection>scm:git:git@github.com:mrmechko/strips.git</connection>
    </scm>
    <developers>
      <developer>
        <id>mrmechko</id>
        <name>Ritwik Bose</name>
        <url>http://cs.rochester.edu/~rbose</url>
      </developer>
    </developers>)
