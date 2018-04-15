---
 layout: docs
 title: Quick Start
---

# Quick Start

Include LambdaTest jar

    "com.fortysevendeg" % "lambda-test_2.12" % "1.3.1" % "test"

    "com.fortysevendeg" % "lambda-test_2.11" % "1.1.2" % "test"
   
Each test file should include 
 
    import com.fortyseven.lambdatest._

Each test file class should extend the `LambdaTest` trait.

Each test class must define a local val `act` whose value is the
tests to be run.

If you want to run tests via SBT then the `build.sbt` file should specify

    testFrameworks += new TestFramework("com.fortysevendeg.lambdatest.sbtinterface.LambdaFramework")
   
To see a simple example look at the [Example](https://github.com/47deg/LambdaTest/blob/master/src/test/scala/demo/Example.scala) demo.
 
