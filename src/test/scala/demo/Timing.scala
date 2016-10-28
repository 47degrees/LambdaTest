package demo

import com.fortysevendeg.lambdatest._
import scala.concurrent.duration._
import scala.language.postfixOps

// format: OFF

class Timing extends LambdaTest {

  def act = {

    def f(t: FiniteDuration) = {
      Thread.sleep(t.toMillis)
      99
    }

    // TODO use emptyLambdaAct
    timer("all") {
      test("Measure") {
        assertTiming {
          val v = f(50 millis)
          assertEq(v, 99, "test eq")
        }("this should work", max = 100 millis) +
        assertTiming {
          exec {
            f(100 millis)
          }
        }("this should fail", max = 75 millis) +
        assertTiming {
          exec {
            f(2 seconds)
          }
        }("this should timeout", max = 200 millis) +
        assertLoad("load test", warmup = 10, repeat = 20, mean = 150 millis, max = 200 millis) {
          f(100 millis)
        }
      }
    }
  }
}

object Timing {

  def main(args: Array[String]): Unit = {
    run("measure", new Timing)
  }
}


