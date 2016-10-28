package demo

import com.fortysevendeg.lambdatest._

import com.fortysevendeg.lambdatest.LambdaTest

class Bad extends LambdaTest {

  def fail(): Boolean = {
    throw new Exception("bad")
  }

  def act = {
    assert(false, "should have been in test") +
      assert(true, "should also have been in a test")
  } +
    test("throws") {
      assert(fail, "unexpected exception")
    }
}

object Bad {
  def main(args: Array[String]): Unit = {
    run("bad", new Bad)
  }
}
