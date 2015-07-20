scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "org.eclipse.jgit" % "org.eclipse.jgit" % "4.0.1.201506240215-r",
  "com.propensive" %% "rapture-json-argonaut" % "1.1.0",
  "org.scalatra" %% "scalatra" % "2.3.1",
  "org.eclipse.jetty" % "jetty-webapp" % "9.2.9.v20150224",
  "org.eclipse.jetty" % "jetty-plus" % "9.2.9.v20150224"
)
