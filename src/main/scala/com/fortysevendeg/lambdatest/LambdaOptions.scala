package com.fortysevendeg.lambdatest

import com.typesafe.config.{ Config, ConfigFactory }

/**
  * Options used for testing.
  */
object LambdaOptions {
  /**
    * The LambdaTest configuration.
    */
  val config: Config = ConfigFactory.load().getConfig("com.fortysevendeg.lambdatest")

  /**
    * Output lines for asserts that succeed.
    */
  val outOk = config.getBoolean(("outOk"))

  /**
    * Output an initial header line.
    */
  val outHeader = config.getBoolean(("outHeader"))

  /**
    * Output an final summery line.
    */
  val outSummary = config.getBoolean(("outSummary"))

  /**
    * Output a stack trace of any unexpected exceptions.
    */
  val outExceptionTrace = config.getBoolean("outExceptionTrace")

  /**
    * Output only those parts that contain failures.
    */
  val onlyIfFail = config.getBoolean("onlyIfFail")

  /**
    *  Use colors in the output.
    */
  val useColor = config.getBoolean("useColor")

  /**
    * Number of characters to indent each level of output.
    */
  val indent = config.getInt("indent")
}
