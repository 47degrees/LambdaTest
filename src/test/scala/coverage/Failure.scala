package coverage

import com.fortysevendeg.lambdatest._
import demo.Example

// format: OFF

class Failure extends LambdaTestRun {

  def fail(name: String) = {
    throw new Exception(name)
  }

  def act = {
    label("a") {
      fail("a")
      test("b") {
        assert(true)
      }
    } +
    label("a1") {
      test("b1") {
        fail("b1")
        assert(true)
      }
    } +
    label("a2") {
      test("b2") {
        assert(fail("c2"), "test exceptions")
      }
    } +
    label("a3") {
      test("b3") {
        exec {
          fail("c3")
        } +
        assert(true)
      }
    }
  }
}

object Failure extends App {
  run("exceptions", new Failure)
}
