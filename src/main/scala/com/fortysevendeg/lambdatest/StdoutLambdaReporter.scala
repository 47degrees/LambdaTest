package com.fortysevendeg.lambdatest

/**
  *  Companion object for the StdOut reporter.
  */
object StdoutLambdaReporter {
  /**
    * Constructor for a StdOut reporter.
    * @return the reporter.
    */
  def apply() = new StdoutLambdaReporter()
}

/**
  * A reporter that sends test results to StdOut.
  * @param tests the number of tests.
  * @param failed  the number of failed tests.
  */
case class StdoutLambdaReporter private[lambdatest](tests: Int = 0, failed: Int = 0) extends LambdaReporter {

  private def i(depth: Int) = {
    val cnt = depth * LambdaOptions.indent
    s"${" " * cnt}"
  }

  private def out(depth: Int, s: String) = {
    val indent = i(depth)
    println(indent + s.replaceAll("[\n]", s"\n$indent"))
  }

  /**
    * Called when an assertion succeeds.
    * @param name the name of the assertion.
    * @return a new LambdaReporter.
    */
  override def ok(name: String): StdoutLambdaReporter = this.copy(tests = tests + 1)

  /**
    * Reports simple output lines (such as for labels).
    * @param depth the indentation level.
    * @param s the string to be reported.
    * @return a new LambdaReporter.
    */
  override def report(depth: Int, s: String): StdoutLambdaReporter = {
    out(depth, s)
    this
  }

  /**
    * Reports the result of a failing test.
    * @param depth the indentation level.
    * @param s the string to be reported.
    * @return a new LambdaReporter.
    */
  override def reportFail(depth: Int, s: String): StdoutLambdaReporter = {
    if (LambdaOptions.useColor) {
      out(depth, s"${Console.RED}$s${Console.RESET}")
    } else {
      out(depth, s)
    }
    this
  }

  /**
    * Reports the result of a successful test.
    * @param depth the indentation level.
    * @param s the string to be reported.
    * @return a new LambdaReporter.
    */
  override def reportOk(depth: Int, s: String): StdoutLambdaReporter = {
    if (LambdaOptions.useColor) {
      out(depth, s"${Console.GREEN}$s${Console.RESET}")
    } else {
      out(depth, s)
    }
    this
  }

  /**
    * Called when an assertion fails.
    * @param name the name of the assertion.
    * @return a new LambdaReporter.
    */
  override def fail(name: String): StdoutLambdaReporter = this.copy(failed = failed + 1, tests = tests + 1)
}
