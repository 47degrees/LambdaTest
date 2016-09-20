package com.fortysevendeg.lambdatest

private[lambdatest] case class HoldItem(kind: String, depth: Int, s: String)

/**
  * The companion object for the hold reporter.
  */
object HoldLambdaReporter {
  /**
    * The contructor for a hold reporter.
    * @return the hold reporter.
    */
  def apply() = new HoldLambdaReporter()
}

/**
  * A reporter that holds all messages  to reported until
  * the flush method is called.
  * @param tests the number of tests.
  * @param failed the number of failed tests.
  * @param hold
  */
case class HoldLambdaReporter private[lambdatest] (
  tests: Int = 0,
  failed: Int = 0,
  private val hold: List[HoldItem] = List.empty[HoldItem]
) extends LambdaReporter {

  /**
    * Called when an assertion succeeds.
    * @param name the name of the assertion.
    * @return a new LambdaReporter.
    */
  override def ok(name: String): HoldLambdaReporter = {
    this.copy(tests = tests + 1, hold = HoldItem("ok", 0, name) +: hold)
  }

  /**
    * Reports simple output lines (such as for labels).
    * @param depth the indentation level.
    * @param s the string to be reported.
    * @return a new LambdaReporter.
    */
  override def report(depth: Int, s: String): HoldLambdaReporter = {
    this.copy(hold = HoldItem("report", depth, s) +: hold)
  }

  /**
    * Reports the result of a failing test.
    * @param depth the indentation level.
    * @param s the string to be reported.
    * @return a new LambdaReporter.
    */
  override def reportFail(depth: Int, s: String): HoldLambdaReporter =
    this.copy(hold = HoldItem("reportFail", depth, s) +: hold)

  /**
    * Reports the result of a successful test.
    * @param depth the indentation level.
    * @param s the string to be reported.
    * @return a new LambdaReporter.
    */
  override def reportOk(depth: Int, s: String): HoldLambdaReporter = {
    this.copy(hold = HoldItem("reportOk", depth, s) +: hold)
  }

  /**
    * Called when an assertion fails.
    * @param name the name of the assertion.
    * @return a new LambdaReporter.
    */
  override def fail(name: String): HoldLambdaReporter = {
    this.copy(failed = failed + 1, tests = tests + 1, hold = HoldItem("failed", 0, name) +: hold)
  }

  /**
    * Moves held messages to another reporter.
    * @param reporter the reporter to move the messages to.
    * @return reporter after all messages have been added to it.
    */
  def flush(reporter: LambdaReporter): LambdaReporter = {
    hold.reverse.foldLeft(reporter) {
      case (reporter, i) ⇒
        i.kind match {
          case "ok" ⇒ reporter.ok(i.s)
          case "failed" ⇒ reporter.fail(i.s)
          case "report" ⇒ reporter.report(i.depth, i.s)
          case "reportOk" ⇒ reporter.reportOk(i.depth, i.s)
          case "reportFail" ⇒ reporter.reportFail(i.depth, i.s)
        }
    }
  }
}
