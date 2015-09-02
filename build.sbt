scalaVersion := "2.11.7"

scalaSource in Compile := baseDirectory.value / "app"

assemblyOutputPath in assembly := target.value / "origin.jar"

copyResources in Compile := {
  val from = baseDirectory.value / "interface"
  val to = (classDirectory in Compile).value / "interface"
  IO.copyDirectory(from, to, overwrite = true)
  from ** "*" x Path.rebase(from, to)
}

libraryDependencies ++= Seq(
  "org.eclipse.jgit" % "org.eclipse.jgit" % "4.0.1.201506240215-r",
  "com.propensive" %% "rapture-json-argonaut" % "1.1.0",
  "com.auth0" % "java-jwt" % "2.1.0",
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.2",
  "org.scalatra" %% "scalatra" % "2.3.1",
  "org.eclipse.jetty" % "jetty-webapp" % "9.3.2.v20150730"
)
