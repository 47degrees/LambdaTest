package demo

import com.fortysevendeg.lambdatest._

// format: OFF

class Mutable extends LambdaTest {

  val act = test("Mutable") {
    var s = scala.collection.mutable.Set.empty[Int]
    assertEq(s.size, 0, "empty") +
    exec {
      s add 3
    } +
    assertEq(s.size, 1, "insert") +
    exec {
      s remove 3
    } +
    assertEq(s.size, 0, "remove")
  }
}

object Mutable {
  def main(args: Array[String]): Unit = {
    run("mutable", new Mutable)
  }
}
