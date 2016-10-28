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

  private def getSet(name: String): Set[String] = {
    config.getStringList(name).toArray.map(_.asInstanceOf[String]).toSet
  }

  def defaultIncludeAction(includeTags: Set[String], excludeTags: Set[String], tags: Set[String]): Boolean = {
    (includeTags.isEmpty || (tags.exists(includeTags.contains(_)))) && !tags.exists(excludeTags.contains(_))
  }

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
    * @param includeTags       actions only included if they include at least one of these tags or includeTags is empty
    * @param excludeTags       actions only include if they contain none of these tags
    * @param includeAction     function to decide if an action is included based on tags options and the action's tags
    */
  case class LambdaOptions(

    outOk: Boolean = config.getBoolean(("outOk")),

    outHeader: Boolean = config.getBoolean(("outHeader")),

    outSummary: Boolean = config.getBoolean(("outSummary")),

    outExceptionTrace: Boolean = config.getBoolean("outExceptionTrace"),

    onlyIfFail: Boolean = config.getBoolean("onlyIfFail"),

    useColor: Boolean = config.getBoolean("useColor"),

    indent: Int = config.getInt("indent"),

    includeTags: Set[String] = getSet("includeTags"),

    excludeTags: Set[String] = getSet("excludeTags"),

    includeAction: (Set[String], Set[String], Set[String]) ⇒ Boolean = defaultIncludeAction

  ) {
    /**
      * Checks is an action should be included.
      * @param tags the tags of the action.
      * @return  true if the action should be included.
      */
    final def checkTags(tags: Set[String]): Boolean = includeAction(includeTags, excludeTags, tags)
  }

}

