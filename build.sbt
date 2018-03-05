
lazy val akkaHttpVersion = "10.0.9"
lazy val akkaVersion    = "2.4.19"

// Needed for Heroku deployment, can be removed
enablePlugins(JavaAppPackaging)


lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization    := "com.zheniatrochun",
      scalaVersion    := "2.11.8"
    )),
    name := "gateway-service",
    libraryDependencies ++= Seq(
      "com.typesafe.akka"           %% "akka-http"            % akkaHttpVersion,
      "com.typesafe.akka"           %% "akka-http-spray-json" % akkaHttpVersion,
      "com.typesafe.akka"           %% "akka-http-xml"        % akkaHttpVersion,
      "com.typesafe.akka"           %% "akka-stream"          % akkaVersion,
      "com.typesafe.akka"           %% "akka-actor"           % akkaVersion,
      "net.debasishg"               %% "redisclient"          % "3.5",
      "com.typesafe.scala-logging"  %% "scala-logging"        % "3.1.0",
      "ch.qos.logback"               % "logback-classic"      % "1.1.3",
      "com.typesafe.akka"           %% "akka-http-testkit"    % akkaHttpVersion % Test,
      "com.typesafe.akka"           %% "akka-testkit"         % akkaVersion     % Test,
      "com.typesafe.akka"           %% "akka-stream-testkit"  % akkaVersion     % Test,
      "org.scalatest"               %% "scalatest"            % "3.0.1"         % Test
    )
  )
