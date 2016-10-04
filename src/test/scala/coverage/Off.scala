package coverage

import demo.Example
import com.fortysevendeg.lambdatest._

// format: OFF

object Off {
  def main(args: Array[String]): Unit = {
    run("off", new Example, change = _.copy(useColor = false, outHeader = false, outSummary = false))
  }
}
