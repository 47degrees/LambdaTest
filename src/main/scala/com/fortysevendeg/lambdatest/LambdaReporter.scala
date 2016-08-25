package com.fortysevendeg.lambdatest

/**
  * The common trait for all reporters.
  * Reporters are used to output test results.
  */
trait LambdaReporter {

  /**
    * The number of tests run.
    */
  val tests: Int

  /**
    * The number of tests that failed.
    */
  val failed: Int

  /**
    * Called when an assertion succeeds.
    * @param name the name of the assertion.
    * @return a new LambdaReporter.
    */
  def ok(name: String): LambdaReporter

  /**
    * @param name the name of the assertion.
    * @return a new LambdaReporter.
    */
  def fail(name: String): LambdaReporter

  /**
    * Reports simple output lines (such as for labels).
    * @param depth the indentation level.
    * @param s the string to be reported.
    * @return a new LambdaReporter.
    */
  def report(depth: Int, s: String): LambdaReporter

  /**
    * Reports the result of a successful test.
    * @param depth the indentation level.
    * @param s the string to be reported.
    * @return a new LambdaReporter.
    */
  def reportOk(depth: Int, s: String): LambdaReporter

  /**
    * Reports the result of a failing test.
    * @param depth the indentation level.
    * @param s the string to be reported.
    * @return a new LambdaReporter.
    */
  def reportFail(depth: Int, s: String): LambdaReporter
}
