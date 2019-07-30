name := "jpy-sns-test"

version := "0.1"

scalaVersion := "2.11.11"

libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-java-sdk" % "1.11.589",
  "org.jsoup" % "jsoup" % "1.8.3"
)

//resolvers += "micronautics/scala on bintray" at "http://dl.bintray.com/micronautics/scala"

//libraryDependencies += "com.lightbend.akka" %% "akka-stream-alpakka-sns" % "1.1.0"
