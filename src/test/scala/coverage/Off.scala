package coverage

import demo.Example
import com.fortysevendeg.lambdatest._

// format: OFF

object Off extends App {
  run("off", new Example, change = _.copy(useColor = false, outHeader = false, outSummary = false))
}
