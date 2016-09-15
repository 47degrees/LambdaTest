name := "lambda-test"

organization := "com.fortysevendeg"

version := "1.0.0"

scalaVersion := "2.11.8"

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature")

viewSettings

testFrameworks += new TestFramework("com.fortysevendeg.lambdatest.sbtinterface.LambdaFramework")

libraryDependencies ++= Seq(
  "org.scalacheck" %% "scalacheck" % "1.13.2",
  "org.scala-sbt" % "test-interface" % "1.0",
  "com.typesafe" % "config" % "1.3.0",
  "com.persist" % "persist-logging_2.11" % "1.2.4" % "test"
)

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("Snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("Releases" at nexus + "service/local/staging/deploy/maven2")
}

lazy val gpgFolder = sys.env.getOrElse("GPG_FOLDER", ".")

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

licenses := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.html"))

homepage := Some(url("https://github.com/47deg/LambdaTest"))

scmInfo := Some(ScmInfo(url("https://github.com/47deg/LambdaTest"), "scm:git@github.com:47deg/LambdaTest.git"))

pomExtra := (
<developers>
  <developer>
    <name>47 Degrees (twitter: @47deg)</name>
    <email>hello@47deg.com</email>
    <url>http://http://www.47deg.com</url>
  </developer>
</developers>
)
