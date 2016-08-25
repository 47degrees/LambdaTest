package demo

import com.fortysevendeg.lambdatest._

class Example extends LambdaTest {

  def doFoo = throw new Exception("foo")

  def contains(expect: String)(ex: Exception): Option[String] = {
    val msg = ex.getMessage
    val q = "\""
    if (msg.contains(expect)) None else Some(s"$q$msg$q does not contain $q$expect$q")
  }

  val act = label("Initial Tests") {
    test("Eq test") {
      assertEq(2 + 1, 3, "Int eq test1")
    }
  } +
  label("Simple Tests") {
    test("Assert Test") {
      assertEq(1, 2, "Int eq test") +
      assert(3 == 5 - 2, "should work")
    } +
    test("Throws Exception") {
      assert(doFoo, "throw foo")
    } +
    test("Expect Exception") {
      assertEx(doFoo, check = contains("bar"))
    }
  }
}

object Example {
  def main(args: Array[String]): Unit = {
    run("example", new Example)
  }
}
