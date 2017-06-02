name := "Hello Test #1"

version := "1.0"

scalaVersion := "2.12.1"

//#resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
//fork := true
//javaOptions in run += "-Dconfig.file=local.conf"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.5.1"
libraryDependencies += "com.typesafe.akka" %% "akka-remote" % "2.5.1"
libraryDependencies += "net.ruippeixotog" %% "scala-scraper" % "1.2.1"

libraryDependencies += "org.postgresql" % "postgresql" % "42.1.1"
libraryDependencies += "com.typesafe.slick" % "slick_2.12" % "3.2.0"
libraryDependencies += "org.slf4j" % "slf4j-nop" % "1.7.25"
libraryDependencies += "com.rabbitmq" % "amqp-client" % "4.1.0"
libraryDependencies += "com.newmotion" % "akka-rabbitmq_2.12" % "4.0.0"
libraryDependencies += "org.json4s" % "json4s-native_2.12" % "3.5.2"

