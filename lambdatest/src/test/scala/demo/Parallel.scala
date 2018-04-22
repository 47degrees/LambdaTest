package demo

import com.fortysevendeg.lambdatest._

// format: OFF

class Parallel extends LambdaTest {

  val act = label("Parallel Tests", parallel = true) {
    test("Test1") {
      assertEq(1, 2, "Int eq test")
    } +
    test("Test2") {
      assertEq(2, 3, "Int eq test") +
      assert(3 == 5 - 2, "should work")
    }
  }
}

object Parallel extends App {
  run("parallel", new Parallel)
}
