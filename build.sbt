name := "idl"

version := "0.1-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

scalacOptions += "-language:postfixOps"

scalacOptions += "-feature"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
	"mysql" % "mysql-connector-java" % "5.1.18",
	"io.github.nremond" %% "pbkdf2-scala" % "0.4",
	"com.github.nscala-time" %% "nscala-time" % "1.0.0"
)
