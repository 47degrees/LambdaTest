package demo

import com.fortysevendeg.lambdatest._

// format: OFF

class Tag extends LambdaTest {
  def act =
    test("test1", tags = Set("A")) {
      assert(true)
    } +
    test("test2", tags = Set("B")) {
      assert(true)
    } +
    test("test3", tags = Set("A", "ignore")) {
      assert(true)
    } +
    test("test4", tags = Set("A", "B")) {
      assert(true)
    }
}

class AB(t: LambdaTest) extends LambdaTestRun {
  def act = label("Run A Tests") {
    changeOptions(_.copy(includeTags = Set("A"))) {
      t.act
    }
  } + label("Run B Tests") {
    changeOptions(_.copy(includeTags = Set("B"))) {
      t.act
    }
  } + label("Run All Tests ") {
    t.act
  }
}

object Tag {
  def main(args: Array[String]) = {
    run("tags", new AB(new Tag))
  }
}
