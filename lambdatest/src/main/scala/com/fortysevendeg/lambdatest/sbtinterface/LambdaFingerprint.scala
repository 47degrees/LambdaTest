package com.fortysevendeg.lambdatest.sbtinterface

import org.scalatools.testing.SubclassFingerprint

private[lambdatest] class LambdaFinderprint extends SubclassFingerprint {
  override def isModule: Boolean = false

  override def superClassName(): String = "com.fortysevendeg.lambdatest.LambdaTest"

}
