package com.fortysevendeg.lambdatest

import scala.concurrent.duration.Duration
import scala.concurrent.{ Await, ExecutionContext, Future }

/**
  * Companion object for LambdaState class.
  */
object LambdaState {
  /**
    * Create a new LambdaState in the testing initial state.
    *
    * @param reporter an optuonal reporter.
    * @return the new initial state object.
    */
  def apply(reporter: LambdaReporter) = new LambdaState(reporter)
}

/**
  * The immutable state object that holds all testing state.
  * Operations create new immutable state object.
  * The methods here should not be used in user test code (that extends LambdaTest).
  * These methods can be called from new LambdaAct code.
  *
  * @param reporter the reporter used to handle output when this state changes.
  * @param indent
  * @param sawFail
  * @param inTest
  * @param startTime
  */
case class LambdaState private[lambdatest] (
  reporter: LambdaReporter,
  private val indent: Int = 0,
  private val sawFail: Boolean = false,
  private val inTest: Boolean = false,
  private val startTime: Long = 0
) {
  /**
    * This method is used to run test.
    *
    * @param name     the name of the tests.
    * @param body     a LambdaAct containing all the test actions to be run.
    * @param parallel true, if the top level actions in body are to be run in parallel.
    */
  def run(name: String, body: ⇒ LambdaAct, parallel: Boolean): Unit = {
    val t1 = beginTests(name)
    val t2 = try {
      body.eval(t1, parallel)
    } catch {
      case ex: Exception ⇒
        val f = ex.getStackTrace()(0)
        val p = s"${f.getFileName} Line ${f.getLineNumber}"
        t1.unExpected(ex, p)
    }
    val t3 = t2.endTests(name)
  }

  private def beginTests(name: String): LambdaState = {
    val reporter1 = if (LambdaOptions.outHeader) reporter.report(0, s"***** running $name") else reporter
    this.copy(reporter = reporter1, startTime = System.currentTimeMillis())
  }

  private def endTests(name: String): LambdaState = {
    val elapsed = (System.currentTimeMillis() - startTime) / 1000.0
    val seconds = f"$elapsed%2.3f seconds"
    val s1 = if (reporter.failed > 0) {
      if (LambdaOptions.useColor) {
        s"${Console.RED} ${reporter.failed} failed${Console.RESET}"
      } else {
        s" ${reporter.failed} failed"
      }
    } else {
      s""
    }
    val s = s"***** $name: ${reporter.tests} tests$s1 $seconds"
    val reporter1 = if (LambdaOptions.outSummary) reporter.report(0, s) else reporter
    this.copy(reporter = reporter1)
  }

  /**
    * Called when an assertion succeeds.
    *
    * @param info a message for this assertion.
    * @param pos  the source position of the assertion.
    * @return the new state.
    */
  def success(info: String, pos: String): LambdaState = {
    val reporter1 = if (!inTest) {
      reporter.reportFail(indent, s"Fail: Assertions must be inside test ($pos)").fail("not inside")
    } else {
      reporter
    }
    val reporter2 = if (LambdaOptions.outOk && !LambdaOptions.onlyIfFail) {
      reporter1.reportOk(indent, s"Ok: $info ($pos)")
    } else {
      reporter1
    }
    this.copy(reporter = reporter2)
  }

  /**
    * Called when an assertion fails.
    *
    * @param info a message for this assertion.
    * @param pos  the source position of the assertion.
    * @return the new state.
    */
  def fail(info: String, pos: String): LambdaState = {
    val reporter1 = if (!inTest) {
      reporter.reportFail(indent, s"Fail: Assertions must be inside test ($pos)").fail("not inside")
    } else {
      reporter
        .reportFail(indent, s"Fail: $info ($pos)")
    }
    this.copy(reporter = reporter1, sawFail = true)
  }

  private[lambdatest] def eval(body: List[LambdaState ⇒ LambdaState], parallel: Boolean): LambdaState = {
    if (parallel && body.size >= 2) {
      implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global
      val first = body.head
      val t1 = Future(first(this))
      val rest = body.tail
      val state = this.copy(reporter = HoldLambdaReporter())
      val tn = for (test ← rest) yield {
        Future(test(state))
      }
      val tn1 = Future.sequence(t1 +: tn)
      val states = Await.result(tn1, Duration.Inf)
      val s1 = states.tail.foldLeft(states.head) {
        case (t, h) ⇒
          t.copy(reporter = h.reporter.asInstanceOf[HoldLambdaReporter].flush(t.reporter))
      }
      val sf = states.foldLeft(false) {
        case (b, s) ⇒ s.sawFail || b
      }
      s1.copy(sawFail = sf)
    } else {
      val t1 = body.foldLeft(this)((t, test) ⇒ {
        test(t)
      })
      t1
    }
  }

  /**
    * Used to implement a test.
    *
    * @param name     the name of the test.
    * @param body     the actions in the test.
    * @param parallel true, if the top level actions are to be run in parallel.
    * @param pos      the source position of the test.
    * @return the new state.
    */
  def test(name: String, body: ⇒ LambdaAct, parallel: Boolean, pos: String): LambdaState = {
    val reporter1 = if (inTest) {
      reporter.reportFail(indent, s"Fail: Test not permitted inside other tests ($pos)").fail("test in test")
    } else {
      reporter
    }
    val f = this.copy(reporter = reporter1, indent = indent + 1, inTest = true, sawFail = false)
    val t1 = try {
      if (LambdaOptions.onlyIfFail) {
        val h = HoldLambdaReporter().report(indent, s"Test: $name")
        val f1 = f.copy(reporter = h)
        val f2 = body.eval(f1, parallel)
        if (f2.sawFail) {
          f.copy(reporter = f2.reporter.asInstanceOf[HoldLambdaReporter].flush(f.reporter), sawFail = true)
        } else {
          f // No output wanted
        }
      } else {
        val f1 = f.copy(reporter = f.reporter.report(indent, s"Test: $name"))
        body.eval(f1, parallel)
      }
    } catch {
      case ex: Exception ⇒
        val f1 = f.copy(reporter = f.reporter.report(indent, s"Test: $name"))
        f1.unExpected(ex, pos)
    }
    val reporter3 = if (t1.sawFail) {
      t1.reporter.fail(name)
    } else {
      t1.reporter.ok(name)
    }
    this.copy(reporter = reporter3, sawFail = sawFail || t1.sawFail)
  }

  /**
    * Used to implement a labeled block.
    *
    * @param name     the name of the labeled block.
    * @param body     the actions in the labeled block.
    * @param parallel true, if the top level actions are to be run in parallel.
    * @param pos      the source position of the labeled block.
    * @return the new state.
    */

  def label(name: String = "", body: ⇒ LambdaAct, parallel: Boolean, pos: String): LambdaState = {
    val indent1 = if (name == "") 0 else 1
    val f = this.copy(indent = indent + indent1, sawFail = false)

    val t1 = try {
      if (LambdaOptions.onlyIfFail) {
        val h = HoldLambdaReporter()
        val f1 = f.copy(reporter = if (name != "") h.report(indent, name) else h)
        val f2 = body.eval(f1, parallel)
        if (f2.sawFail) {
          f.copy(reporter = f2.reporter.asInstanceOf[HoldLambdaReporter].flush(f.reporter), sawFail = true)
        } else {
          f
        }
      } else {
        val f1 = f.copy(reporter = if (name != "") f.reporter.report(indent, name) else reporter)
        body.eval(f1, parallel)
      }
    } catch {
      case ex: Exception ⇒
        val f1 = f.copy(reporter = if (name != "") f.reporter.report(indent, name) else reporter)
        f1.unExpected(ex, pos)
    }
    this.copy(reporter = t1.reporter, sawFail = sawFail || t1.sawFail)
  }

  private def traceException(ex: Exception, pos: String): LambdaState = {
    if (LambdaOptions.outExceptionTrace) {
      val stack = ex.getStackTrace.map(frame ⇒ s"${
        frame.getFileName
      } Line ${
        frame.getLineNumber
      }")
      val stack1 = stack.takeWhile(frame ⇒ frame != pos)
      val reporter1 = stack1.foldLeft(reporter) {
        (reporter, frame) ⇒
          reporter.reportFail(indent + 1, frame)
      }
      this.copy(reporter = reporter1)
    } else {
      this
    }
  }

  /**
    * Called when an unexpected exception is encounted.
    *
    * @param ex  the exception.
    * @param pos the position where the exception was detected. This is used to prune the stack trace.
    * @return the new state.
    */
  def unExpected(ex: Exception, pos: String): LambdaState = {
    val reporter1 = if (!inTest) reporter.fail("top level") else reporter
    val info = s"Unexpected exception: ${
      ex.getMessage
    }"
    val reporter2 = reporter1.reportFail(indent, s"$info ($pos)")
    this.copy(reporter = reporter2, sawFail = true).traceException(ex, pos)
  }
}
