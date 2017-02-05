package coverage

import com.fortysevendeg.lambdatest._

// format: OFF

object All extends App {
  println("***** running off")
  Off.main(args)
  run("failonly", new FailOnly)
  run("failure", new Failure)
}
