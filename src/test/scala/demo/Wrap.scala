package demo

import com.fortysevendeg.lambdatest._

class Wrap extends LambdaTest {

  import java.io.{ BufferedReader, FileReader }

  def fileWrap(fileName: String)(body: BufferedReader ⇒ LambdaAct): LambdaAct = {
    SingleLambdaAct(t ⇒ {
      val br = new BufferedReader(new FileReader(fileName))
      try {
        body(br).eval(t)
      } finally {
        br.close()
      }
    })
  }

  // format: OFF

  def act = {
    fileWrap("foobar.txt") { f =>
      test("test1") {
        assertEq(f.readLine(), "foo", "line1") +
        assertEq(f.readLine(), "bar", "line2")
      }
    } +
    test("test2") {
      fileWrap("foobar.txt") { f =>
        assertEq(f.readLine, "zap", "line3")
      }
    }
  }
}

object Wrap extends App {
  run("wrap", new Wrap)
}
