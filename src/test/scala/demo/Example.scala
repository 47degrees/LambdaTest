package demo

import com.fortysevendeg.lambdatest._

// format: OFF

class Example extends LambdaTest {

  val act = label("Initial Tests") {
    test("Eq test") {
      assertEq(2 + 1, 3, "Int eq test1")
    }
  } +
  label("Simple Tests") {
    test("Assert Test") {
      assertEq(1, 2, "Int eq test") +
      assert(3 == 5 - 2, "should work")
    }
  }
}

object Example {
  def main(args: Array[String]): Unit = {
    run("example", new Example)
  }
}
