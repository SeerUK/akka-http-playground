// Projects
lazy val akkaHttpPlayground = (project in file("."))
  .settings(commonSettings)
  .settings(
    version := "0.1.0-SNAPSHOT",
    libraryDependencies ++= Seq(akkaHttp)
  )

// Settings
lazy val commonSettings = Seq(
  scalaVersion := "2.12.1",
  organization := "com.elliotdwright",
  scalacOptions ++= Seq(
    "-deprecation",
    "-encoding", "UTF-8",
    "-target:jvm-1.8",
    "-Xlint",
    "-Yno-adapted-args",
    "-Ywarn-dead-code",
    "-Ywarn-infer-any",
    "-Ywarn-numeric-widen",
    "-Ywarn-unused-import",
    "-Ywarn-value-discard"
  ),
  javacOptions ++= Seq(
    "-source", "1.8",
    "-target", "1.8"
  ),
  assemblyJarName in assembly := s"Eidolon-${name.value.capitalize}-${version.value}.jar"
)

// Dependencies
lazy val akkaHttp = "com.typesafe.akka" %% "akka-http" % "10.0.3" % "compile"
