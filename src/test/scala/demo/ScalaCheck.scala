package demo

import com.fortysevendeg.lambdatest._
import org.scalacheck.Prop._
import org.scalacheck.Gen._
import org.scalacheck.Test

class ScalaCheck extends LambdaTest {
  val act = {
    label("ScalaCheck Tests") {
      test("starts with")(assertSC() {
        forAll { (a: String, b: String) =>
          (a + b).startsWith(a)
        }
      }) +
      test("concatenate")(assertSC() {
        forAll { (a: String, b: String) =>
          (a + b).length > a.length && (a + b).length > b.length
        }
      }) +
      test("gen")(assertSC(params=Test.Parameters.default.withMinSuccessfulTests(200)){
        forAll(choose(1,1000)) {n=>
        n  > 0 && n <= 1000}
      }) +
      test("exists")(assertSC(){
        exists(choose(0,10)) { _ == 3 }
      })
    }
  }
}

  object ScalaCheck {

    def main(args: Array[String]): Unit = {
      run("CB", new ScalaCheck)
    }

  }
