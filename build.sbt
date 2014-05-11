name := "idl"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
	"mysql" % "mysql-connector-java" % "5.1.18",
	"io.github.nremond" %% "pbkdf2-scala" % "0.4",
	"com.github.nscala-time" %% "nscala-time" % "1.0.0"
)


play.Project.playScalaSettings
