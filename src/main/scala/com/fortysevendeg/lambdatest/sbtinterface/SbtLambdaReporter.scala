package com.fortysevendeg.lambdatest.sbtinterface

import com.fortysevendeg.lambdatest.LambdaReporter
import org.scalatools.testing.{ Event, EventHandler, Logger, Result }
import com.fortysevendeg.lambdatest.LambdaOptions._

private[lambdatest] object SbtLambdaReporter {
  def apply(loggers: Array[Logger], eventHandler: EventHandler) = {
    new SbtLambdaReporter(loggers, eventHandler)
  }
}

private[lambdatest] case class SbtLambdaReporter private[sbtinterface] (
  loggers: Array[Logger],
  eventHandler: EventHandler,
  tests: Int = 0,
  failed: Int = 0,
  options: LambdaOptions = InitialLambdaOptions
) extends LambdaReporter {

  private def i(depth: Int, offset: Int = 0) = {
    val cnt = math.max((depth * options.indent) + offset, 0)
    s"${" " * cnt}"
  }

  private def fix(depth: Int, s: String, offset: Int = 0) = {
    val indent = i(depth, offset)
    indent + s.replaceAll("[\n]", s"\n$indent")
  }

  private[sbtinterface] case class E(name: String, ok: Boolean) extends Event {
    override def result(): Result = if (ok) Result.Success else Result.Failure

    override def error(): Throwable = null

    override def description(): String = name

    override def testName(): String = name
  }

  override def ok(name: String): SbtLambdaReporter = {
    eventHandler.handle(E(name, true))
    this.copy(tests = tests + 1)
  }

  override def report(depth: Int, s: String): SbtLambdaReporter = {
    for (log ← loggers) {
      log.info(fix(depth, s))
    }
    this
  }

  override def reportFail(depth: Int, s: String): SbtLambdaReporter = {
    for (log ← loggers) {
      if (options.useColor) {
        log.error(fix(depth, s"${Console.RED}$s${Console.RESET}", -1))
      } else {
        log.error(fix(depth, s, -1))
      }
    }
    this
  }

  override def reportOk(depth: Int, s: String): SbtLambdaReporter = {
    for (log ← loggers) {
      if (options.useColor) {
        log.info(fix(depth, s"${Console.GREEN}$s${Console.RESET}"))
      } else {
        log.info(fix(depth, s))
      }
    }
    this
  }

  override def fail(name: String): SbtLambdaReporter = {
    eventHandler.handle(E(name, false))
    this.copy(failed = failed + 1, tests = tests + 1)
  }

  def changeOptions(change: LambdaOptions ⇒ LambdaOptions): LambdaReporter = {
    this.copy(options = change(this.options))
  }

}
