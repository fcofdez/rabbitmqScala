name := "rabbit-queues"

version := "1.0"

scalaVersion := "2.10.1"

resolvers += "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/"

libraryDependencies += "com.rabbitmq" % "amqp-client" % "2.8.1"

libraryDependencies += "com.typesafe.akka" % "akka-actor_2.10" % "2.2-SNAPSHOT"