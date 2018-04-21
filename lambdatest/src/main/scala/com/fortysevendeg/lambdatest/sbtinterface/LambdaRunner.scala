package com.fortysevendeg.lambdatest.sbtinterface

import com.fortysevendeg.lambdatest.LambdaTest
import org.scalatools.testing._

private[lambdatest] class LambdaRunner(loggers: Array[Logger]) extends Runner2 {

  def run(testClassName: String, fingerprint: Fingerprint, eventHandler: EventHandler, args: Array[String]) {
    val c = Class.forName(testClassName)
    val cons = c.getConstructor()
    val x = cons.newInstance().asInstanceOf[LambdaTest]
    val reporter = SbtLambdaReporter(loggers, eventHandler)
    com.fortysevendeg.lambdatest.run(testClassName, x, reporter = reporter)
  }
}
