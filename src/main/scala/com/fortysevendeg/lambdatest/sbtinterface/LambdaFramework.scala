package com.fortysevendeg.lambdatest.sbtinterface

import org.scalatools.testing.{ Fingerprint, Framework, Logger, Runner }

private[lambdatest] class LambdaFramework extends Framework {
  override def tests(): Array[Fingerprint] = Array(new LambdaFinderprint)

  override def testRunner(testClassLoader: ClassLoader, loggers: Array[Logger]): Runner = new LambdaRunner(loggers)

  override def name(): String = "LambdaTest"
}
