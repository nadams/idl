name := "idl"

version := "0.1-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

scalacOptions += "-language:postfixOps"

scalacOptions += "-feature"

javaOptions += "-XX:MaxPermSize=1024"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
	"mysql" % "mysql-connector-java" % "5.1.18",
	"io.github.nremond" %% "pbkdf2-scala" % "0.4",
	"com.github.nscala-time" %% "nscala-time" % "1.2.0"
)

val compass = taskKey[Int]("Compile sass")

val compassClean = taskKey[Int]("Clean sass")

compass := {
  "compass compile" !
}

compassClean := {
  "compass clean" !
}

compile <<= (compile in Compile).dependsOn(compass)

clean <<= clean.dependsOn(compassClean)

