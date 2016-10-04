package coverage

import com.fortysevendeg.lambdatest._
import demo.Example

// format: OFF

class FailOnly extends LambdaTestRun {
  val act =
    changeOptions(_.copy(indent = 5, onlyIfFail = true)) {
      new Example().act
    }
}

object FailOnly {
  def main(args: Array[String]): Unit = {
    run("failOnly", new FailOnly)
  }
}