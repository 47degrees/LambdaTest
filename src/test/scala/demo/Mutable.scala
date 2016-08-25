package demo

import com.fortysevendeg.lambdatest._

class Mutable extends LambdaTest {

  val act = test("Mutable") {
    var x = 0
    assertEq(x, 0) +
    exec {
      x += 2
    } +
    assertEq(x, 2) +
    exec {
      x *= 2
    } +
    assertEq(x, 4) +
    exec {
      x = 5
    } +
    assertEq(x, 5)
  }
}

object Mutable {
  def main(args: Array[String]): Unit = {
    run("mutable", new Mutable)
  }
}
