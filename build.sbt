name := "NextTry"

version := "0.1"

scalaVersion := "2.12.8"

// https://mvnrepository.com/artifact/com.typesafe.akka/akka-actor
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.6.14"
libraryDependencies += "com.h2database" % "h2" % "1.4.200"

libraryDependencies += "com.typesafe.akka" %% "akka-cluster" % "2.6.14"
libraryDependencies +="com.typesafe.akka" %% "akka-http" % "10.2.4"
libraryDependencies += "com.typesafe.akka" %% "akka-actor-typed" % "2.6.14"

libraryDependencies += "com.typesafe.akka" %% "akka-cluster-typed" % "2.6.14"

libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.6.14"
libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % "10.2.4"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.0.0" % "runtime"
libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % "10.2.4"

// https://mvnrepository.com/artifact/com.typesafe.play/play-test
libraryDependencies += "com.typesafe.play" %% "play-test" % "2.8.8" % Test