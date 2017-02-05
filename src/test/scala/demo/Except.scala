package demo

import com.fortysevendeg.lambdatest._

// format: OFF

class Except extends LambdaTest {

  def doFoo = throw new Exception("foo")

  def contains(expect: String)(ex: Throwable): Option[String] = {
    val msg = ex.getMessage
    val q = "\""
    if (msg.contains(expect)) None else Some(s"$q$msg$q does not contain $q$expect$q")
  }

  val act =
    label("Exception Tests") {
      test("Throws Exception") {
        assert(doFoo, "throw foo")
      } +
      test("Expect Exception") {
        assertEx(doFoo, check = contains("bar"))
      }
    }
}

object Except extends App {
  run("exceptions", new Example)
}