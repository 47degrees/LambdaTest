package com.fortysevendeg

import scala.concurrent.duration._
import scala.concurrent._
import scala.language.implicitConversions
import lambdatest.LambdaOptions._
import scala.util.{ Failure, Success, Try }
import scala.language.postfixOps

/**
  * Actions used in tests.
  */
package object lambdatest {

  /**
    * A convenience abbreviation for a LambdaAct that has only a single transform.
    *
    * @param act a state transformation.
    * @return a LambdaAct the contains only the single act state tranformation.
    */
  def SingleLambdaAct(act: LambdaState ⇒ LambdaState): LambdaAct = LambdaAct(List(act))

  /**
    * Converts a Seq[LambdaAct] to a LambdaAct.
    *
    * @param x the sequence.
    * @return the LambdaAct.
    */
  implicit def GroupLambdaAct(x: Seq[LambdaAct]): LambdaAct = {
    x.foldLeft[LambdaAct](LambdaAct(List()))((a, b) ⇒ a + b)
  }

  /**
    * Get a source position string.
    *
    * @param offset stack offset (default 0).
    * @return a string representing the source position of the caller.
    */
  def srcPos(offset: Int = 0): String = {
    val f = new Exception("foo").getStackTrace.apply(2 + offset)
    s"${f.getFileName} Line ${f.getLineNumber}"
  }

  /**
    * This command is used to directly run a test.
    *
    * @param name     the name for the test.
    * @param body     the test to be run.
    * @param parallel an option to run top level actions in parallel (default false).
    * @param reporter an option to specify an alternate reporter.
    * @param change   an option to change the options for the run.
    */
  def run(
    name: String,
    body: ⇒ LambdaTestRun,
    parallel: Boolean = false,
    reporter: LambdaReporter = StdoutLambdaReporter(),
    change: LambdaOptions ⇒ LambdaOptions = (x: LambdaOptions) ⇒ x
  ): Unit = {
    LambdaState(reporter).changeOptions(change).run(name, body.act, parallel)
  }

  /**
    * An assertion action for a single boolean.
    *
    * @param test   the boolean.
    * @param info   a string to be reported.
    * @param showOk an option that if false supresses the output for success.
    * @param pos    the source position (usually defaulted).
    * @return the LambdaAct.
    */
  def assert(test: ⇒ Boolean, info: ⇒ String = "",
    showOk: Boolean = true, pos: String = srcPos()): LambdaAct = {
    SingleLambdaAct(t ⇒ try {
      if (test) {
        if (showOk) t.success(info, pos) else t
      } else {
        t.fail(info, pos)
      }
    } catch {
      case ex: Throwable ⇒
        t.unExpected(ex, pos)
    })
  }

  /**
    * An assertion action that compares two values of the same type.
    *
    * @tparam T the type of the values to be compared.
    * @param a      the first value.
    * @param b      the second value.
    * @param info   a string to be reported.
    * @param showOk an option that if false supresses the output for success.
    * @param pos    the source position (usually defaulted).
    * @return the LambdaAct.
    */
  def assertEq[T](a: ⇒ T, b: ⇒ T, info: ⇒ String = "",
    showOk: Boolean = true, pos: String = srcPos()): LambdaAct = {
    SingleLambdaAct(t ⇒ try {
      val a1 = a
      val info0 = if (info == "") "" else s" ($info)"
      val info1 = s"[$a1] $info"
      if (a1 == b) {
        if (showOk) t.success(info1, pos) else t
      } else {
        t.fail(s"[$a1 != $b] $info", pos)
      }
    } catch {
      case ex: Throwable ⇒
        t.unExpected(ex, pos)
    })
  }

  /**
    * An assertion that expects an exception to be raised.
    *
    * @param test   the code that should raise the exception.
    * @param info   a string to be reported.
    * @param showOk an option that if false supresses the output for success.
    * @param check  a function that further checks the exception. It returns None if the the check passes and
    *               Some(msg) if the check fails (where msg desribes the failure).
    * @param pos    the source position (usually defaulted).
    * @return the LambdaAct.
    */
  def assertEx(
    test: ⇒ Unit,
    info: ⇒ String = "",
    check: Throwable ⇒ Option[String] = (ex: Throwable) ⇒ None,
    showOk: Boolean = true,
    pos: String = srcPos()
  ): LambdaAct = {
    val info1 = if (info == "") "" else s" ($info)"
    SingleLambdaAct(t ⇒ try {
      test
      t.fail(s"Expected exception not raised$info1", pos)
    } catch {
      case ex: Throwable ⇒
        check(ex) match {
          case None ⇒
            if (showOk) t.success(info, pos) else t
          case Some(s) ⇒
            t.fail(s"Exception fails check ($s$info1)", pos)
        }
    })
  }

  import org.scalacheck.util.Pretty._
  import org.scalacheck.Test
  import org.scalacheck.Test.TestCallback

  /**
    * An assertion that checks a ScalaCheck property.
    *
    * @param params
    * @param showOk an option that if false supresees the output for success.
    * @param prop   the ScalaCheck property to be checked.
    * @param pos    the source position (usually defaulted).
    * @return the LambdaAct.
    */
  def assertSC(
    params: Test.Parameters = Test.Parameters.default,
    showOk: Boolean = true, pos: String = srcPos()
  )(prop: org.scalacheck.Prop): LambdaAct = {
    val resP = Promise[Test.Result]
    object cb extends TestCallback {
      override def onTestResult(name: String, res: Test.Result) = {
        resP.trySuccess(res)
      }

      def out(t: LambdaState, pos: String): LambdaState = {
        val res = Await.result(resP.future, Duration.Inf)
        if (res.passed) {
          val dis = if (res.discarded == 0) "" else s" discarded ${res.discarded}"
          if (showOk) t.success(s"Passed ${res.succeeded} tests$dis", pos) else t
        } else {
          t.fail(pretty(res), pos)
        }
      }
    }
    SingleLambdaAct(t ⇒ try {
      val param = params.withTestCallback(cb)
      Test.check(param, prop)
      cb.out(t, pos)
    } catch {
      case ex: Throwable ⇒
        t.unExpected(ex, pos)
    })
  }

  /**
    * A compund action that defines a single test.
    *
    * @param name     the name of the test.
    * @param parallel if true, run top level actions in body in parallel (default false).
    * @param tags     tags for this test (default empty).
    * @param pos      the source position (usually defaulted).
    * @param body     the actions inside the test.
    * @return the LambdaAct.
    */
  def test(name: String, parallel: Boolean = false,
    tags: Set[String] = Set.empty[String],
    pos: String = srcPos())(body: ⇒ LambdaAct): LambdaAct = {
    SingleLambdaAct(t ⇒ if (t.options.checkTags(tags)) t.test(name, body, parallel, pos) else t)
  }

  /**
    * A compund action that defines a labeled block of code.
    *
    * @param name     the name of the label.  Default, no label line output and body is not nested.
    * @param parallel if true, run top level actions in body in parallel (default false).
    * @param tags     tags for this label (default empty).
    * @param pos      the source position (usually defaulted).
    * @param body     the actions inside the label.
    * @return the LambdaAct.
    */
  def label(name: String = "", parallel: Boolean = false,
    tags: Set[String] = Set.empty[String], pos: String = srcPos())(body: ⇒ LambdaAct): LambdaAct = {
    SingleLambdaAct(t ⇒ if (t.options.checkTags(tags)) t.label(name, body, parallel, pos) else t)
  }

  /**
    * An action for nesting other actions.
    * Nest does not increase indentation of the
    * output if its body.
    * Nest is useful for functional testing.
    *
    * @param body the nested actions.
    * @param pos  the source position (usually defaulted).
    * @return the LambdaAct.
    */
  def nest(body: ⇒ LambdaAct, pos: String = srcPos()): LambdaAct = {
    SingleLambdaAct(t ⇒ t.label("", body, false, pos))
  }

  /**
    * An action that executed the code in its body
    *
    * @param body the Scala code to be executed.
    * @param pos  the source position (usually defaulted).
    * @return the LambdaAct.
    */
  def exec[T](body: ⇒ Unit, pos: String = srcPos()): LambdaAct = {
    SingleLambdaAct(t ⇒ {
      try {
        body
        t
      } catch {
        case ex: Throwable ⇒
          t.unExpected(ex, pos)
      }
    })
  }

  /**
    * Changes the options within its body.
    *
    * @param change a function to change the options.
    * @param pos    the source position (usually defaulted).
    * @param body   the actions inside.
    * @return the LambdaAct.
    */
  def changeOptions(change: LambdaOptions ⇒ LambdaOptions, pos: String = srcPos())(body: ⇒ LambdaAct): LambdaAct = {
    SingleLambdaAct(t ⇒ try {
      val t1 = t.changeOptions(change)
      body.eval(t1)
    } catch {
      case ex: Throwable ⇒
        t.unExpected(ex, pos)
    })
  }

  private def timeRound(micros: Long): FiniteDuration = {
    val min = 20 // displayed count should be greater than this
    val millis = micros / 1000
    val secs = millis / 1000
    if (secs > min) {
      secs.seconds
    } else if (millis > min) {
      millis.millis
    } else {
      micros.microsecond
    }
  }

  /**
    * This action times its body and reports how long it ran.
    *
    * @param info a string to be reported
    * @param body the actions to be timed.
    * @param pos  the source position (usually defaulted).
    * @return the LambdaAct.
    */
  def timer(info: String, pos: String = srcPos())(body: ⇒ LambdaAct): LambdaAct = {
    SingleLambdaAct {
      t ⇒
        val time0 = System.nanoTime()
        val t1 = t.label(s"Start timer: $info", body, false, "")
        val time1 = System.nanoTime()
        val total = (time1 - time0) / 1000
        t1.label(s"End timer: $info [${timeRound(total)}]", exec {}, false, "")
    }
  }

  /**
    * Ths assertion runs its body multiple time and reports the mean, max and standard deviation.
    * It fails if the specified mean or max are exceeded.
    *
    * @param info    a string to be reported.
    * @param warmup  the number of time to run its body before starting timing.
    * @param repeat  the number of times to run and time the body.
    * @param mean    the assertion fails if the mean exceeds this value.
    * @param max     the assertion fails if the max exceeds this value.
    * @param timeout the maximum time to wait for the body execution to complete.
    * @param pos     the source position (usually defaulted).
    * @param body    the code to be timed.
    * @return the LambdaAct.
    */
  def assertPerf(
    info: String,
    warmup: Int = 10,
    repeat: Int = 100,
    mean: FiniteDuration = 10 millis,
    max: FiniteDuration = 15 millis,
    timeout: FiniteDuration = 1 second,
    pos: String = srcPos()
  )(body: ⇒ Unit): LambdaAct = {
    import scala.concurrent.ExecutionContext.Implicits.global

    SingleLambdaAct {
      case t ⇒
        try {
          for (i ← 1 to warmup) {
            Await.result(Future(body), timeout)
          }
          val times = for (i ← 1 to repeat) yield {
            val t0 = System.nanoTime()
            Await.result(Future(body), timeout)
            val t1 = System.nanoTime()
            (t1 - t0) / 1000
          }
          val meanMicros = times.sum / times.length
          val maxMicros = times.max
          val stdDev = Math.sqrt((times.map(_ - meanMicros)
            .map(v ⇒ v * v).sum) / times.length).toInt
          val info1 = s"[mean:${
            timeRound(meanMicros)
          } max:${timeRound(maxMicros)} stdev:${
            timeRound(stdDev)
          }] $info"

          if (meanMicros.micros > mean) {
            t.fail(s"exceeds mean $info1 $meanMicros.micros $mean", pos)
          } else if (maxMicros.micros > max) {
            t.fail(s"exceeds max $info1", pos)
          } else {
            t.success(s"$info1", pos)
          }
        } catch {
          case ex: TimeoutException ⇒ t.fail(s"timeout: $info", pos)
          case ex: Throwable ⇒ t.unExpected(ex, pos)
        }
    }
  }

  /**
    * This assertion times its body and fails if it takes longer than max.
    *
    * @param body    the actions to be timed.
    * @param info    a string to be reported.
    * @param max     the assertion fails if the body takes longer than this.
    * @param timeout the maximum time to wait for the body execution to complete.
    * @param pos     the source position (usually defaulted).
    * @return the LambdaAct.
    */
  def assertTiming(body: ⇒ LambdaAct)(
    info: String,
    max: FiniteDuration = 100 millis,
    timeout: FiniteDuration = 1 second,
    pos: String = srcPos()
  ): LambdaAct = {
    import scala.concurrent.ExecutionContext.Implicits.global

    SingleLambdaAct {
      case t ⇒
        val t0 = System.nanoTime()
        val f1 = Future(Try(body.eval(t)))
        val r = Try(Await.result(f1, timeout))
        val t1 = System.nanoTime()
        val micros = (t1 - t0) / 1000
        val elapsed = timeRound(micros)
        val part: Int = ((elapsed / max) * 100).toInt
        val times = s"$elapsed $part%"
        r match {
          case Failure(ex) ⇒ t.fail(s"exceeded timeout [$times] $info", pos)
          case Success(Success(t1: LambdaState)) ⇒
            if (micros.micros > max) {
              t1.fail(s"exceeded max [$times] $info", pos)
            } else {
              t1.success(s"[$times] $info", pos)
            }
          case Success(Failure(ex: Throwable)) ⇒ t.unExpected(ex, pos)
        }
    }
  }

}
