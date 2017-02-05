package demo

import com.fortysevendeg.lambdatest._
import scala.util.Random

// format: OFF

class Generate extends LambdaTest {
  val s = List(0, 5, 6, 3)

  val act = {
    label("List Tests") {
      s.zipWithIndex.map {
        case (i, j) => {
          test(s"Elem test $j")(
            assertEq(j, i)
          )
        }
      }
    } +
    test("Gen") {
      for (j <- 1 to 10) yield {
        val i = Math.abs(Random.nextInt()) % 10
        assert(i % 2 == 0, s"$i is even", showOk = false)
      }
    }
  }
}

object Generate extends App{
    run("generate", new Generate)
}
