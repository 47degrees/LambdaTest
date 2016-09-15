package com.fortysevendeg

import java.util.concurrent.Future

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Promise}
import scala.language.implicitConversions

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
  def SingleLambdaAct(act: LambdaState => LambdaState): LambdaAct = LambdaAct(List(act))

  /**
    * Converts a Seq[LambdaAct] to a LambdaAct.
    * @param x the sequence.
    * @return the LambdaAct.
    */
  implicit def GroupLambdaAct(x: Seq[LambdaAct]): LambdaAct = {
    x.foldLeft[LambdaAct](LambdaAct(List()))((a, b) => a + b)
  }

  private def pos(offset: Int = 0): String = {
    val f = new Exception("foo").getStackTrace.apply(2 + offset)
    s"${f.getFileName} Line ${f.getLineNumber}"
  }

  /**
    * This command is used to directly run a test.
    *
    * @param name     the name for the test.
    * @param body     the test to be run.
    * @param parallel an option to run top level actions in parallel.
    * @param reporter an option to specify an alternate reporter.
    */
  def run(name: String,
          body: => LambdaTest,
          parallel: Boolean = false,
          reporter: LambdaReporter = StdoutLambdaReporter()): Unit = {
    LambdaState(reporter).run(name, body.act, parallel)
  }

  /**
    * An assertion action for a single boolean.
    *
    * @param test   the boolean.
    * @param info   a string to be reported.
    * @param showOk an option that if false supresses the output for success.
    * @return the LambdaAct.
    */
  def assert(test: => Boolean, info: => String = "", showOk: Boolean = true): LambdaAct = {
    val p = pos()
    SingleLambdaAct(t => try {
      if (test) {
        if (showOk) t.success(info, p) else t
      } else {
        t.fail(info, p)
      }
    } catch {
      case ex: Exception =>
        t.unExpected(ex, p)
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
    * @return the LambdaAct.
    */
  def assertEq[T](a: => T, b: => T, info: => String = "", showOk: Boolean = true): LambdaAct = {
    val p = pos()
    SingleLambdaAct(t => try {
      val a1 = a
      val info0 = if (info == "") "" else s" ($info)"
      val info1 = s"$a1$info0"
      if (a1 == b) {
        if (showOk) t.success(info1, p) else t
      }
      else {
        t.fail(s"$a1 != $b$info0", p)
      }
    } catch {
      case ex: Exception =>
        t.unExpected(ex, p)
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
    * @return the LambdaAct.
    */
  def assertEx(test: => Unit,
               info: => String = "",
               check: Exception => Option[String] = (ex: Exception) => None,
               showOk: Boolean = true): LambdaAct = {
    val p = pos()
    val info1 = if (info == "") "" else s" ($info)"
    SingleLambdaAct(t => try {
      test
      t.fail(s"Expected exception not raised$info1", p)
    } catch {
      case ex: Exception =>
        check(ex) match {
          case None =>
            if (showOk) t.success(info, p) else t
          case Some(s) =>
            t.fail(s"Exception fails check: $s$info1", p)
        }
    })
  }

  import org.scalacheck.util.Pretty._
  import org.scalacheck.Test
  import org.scalacheck.Test.TestCallback

  /**
    * An assertion that checks a ScalaCheck property.
    * @param params
    * @param showOk an option that if false supresees the output for success.
    * @param prop the ScalaCheck property to be checked.
    * @return the LambdaAct.
    */
    def assertSC(params: Test.Parameters = Test.Parameters.default, showOk: Boolean = true)(prop: org.scalacheck.Prop): LambdaAct = {
    val p = pos()
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
    SingleLambdaAct(t => try {
      val param = params.withTestCallback(cb)
      Test.check(param, prop)
      cb.out(t, p)
    } catch {
      case ex: Exception =>
        t.unExpected(ex, p)
    })
  }

  /**
    * A compund action that defines a single test.
    * @param name the name of the test.
    * @param parallel  if true, run top level actions in body in parallel.
    * @param body  the actions inside the test.
    * @return the LambdaAct.
    */
  def test(name: String, parallel: Boolean = false)(body: => LambdaAct): LambdaAct = {
    val p = pos()
    SingleLambdaAct(t => t.test(name, body, parallel, p))
  }

  /**
    * A compund action that defines a labeled block of code.
    * @param name the name of the label.
    * @param parallel  if true, run top level actions in body in parallel.
    * @param body  the actions inside the label.
    * @return the LambdaAct.
    */
  def label(name: String, parallel: Boolean = false)(body: => LambdaAct): LambdaAct = {
    val p = pos()
    SingleLambdaAct(t => t.label(name, body, parallel, p))
  }

  /**
    * An action that executed the code in its body
    * @param body the Scala code to be executed.
    * @return the LambdaAct.
    */
  def exec[T](body: => Unit): LambdaAct = {
    val p = pos()
    SingleLambdaAct(t => try {
      body
      t
    } catch {
      case ex: Exception =>
        t.unExpected(ex, p)
    })
  }
}
