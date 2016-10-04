package com.fortysevendeg.lambdatest

import com.typesafe.config.{ Config, ConfigFactory }

/**
  * Configuration options.
  */
object LambdaOptions {
  /**
    * The LambdaTest configuration from application.conf.
    */
  val config: Config = ConfigFactory.load().getConfig("com.fortysevendeg.lambdatest")

  /**
    * The initial options.
    */
  val InitialLambdaOptions = LambdaOptions()

  /**
    * LambdaTest configuration options.
    *
    * @param outOk             output lines for asserts that succeed.
    * @param outHeader         output an initial header line.
    * @param outSummary        output an final summery line.
    * @param outExceptionTrace output a stack trace of any unexpected exceptions.
    * @param onlyIfFail        output only those parts that contain failures.
    * @param useColor          use colors in the output.
    * @param indent            number of characters to indent each level of output.
    */
  case class LambdaOptions(

    outOk: Boolean = config.getBoolean(("outOk")),

    outHeader: Boolean = config.getBoolean(("outHeader")),

    outSummary: Boolean = config.getBoolean(("outSummary")),

    outExceptionTrace: Boolean = config.getBoolean("outExceptionTrace"),

    onlyIfFail: Boolean = config.getBoolean("onlyIfFail"),

    useColor: Boolean = config.getBoolean("useColor"),

    indent: Int = config.getInt("indent")
  )

}

