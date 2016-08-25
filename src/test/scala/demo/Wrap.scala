package demo

import java.net.InetAddress

import akka.actor.ActorSystem
import com.fortysevendeg.lambdatest._
import com.persist.logging._
import scala.concurrent.duration._
import scala.concurrent.Await
import scala.language.postfixOps

class Wrap extends LambdaTest {

  import java.io.{BufferedReader, FileReader}

  def fileWrap(fileName: String)(body: BufferedReader => LambdaAct): LambdaAct = {
    SingleLambdaAct(t => {
      val br = new BufferedReader(new FileReader(fileName))
      try {
        body(br).eval(t)
      } finally {
        br.close()
      }
    })
  }

  def logWrap(body: LambdaAct): LambdaAct = {
    SingleLambdaAct(t => {
      val system = ActorSystem("test")
      val host = InetAddress.getLocalHost.getHostName
      val loggingSystem = LoggingSystem(system, "test", "1.0.0", "localhost", appenderBuilders = Seq(FileAppender))
      try {
        body.eval(t)
      } finally {
        Await.result(loggingSystem.stop, 30 seconds)
        Await.result(system.terminate(), 20 seconds)
      }
    })
  }

  case class LogTest() extends ClassLogging {
    def test = log.info("Log test")
  }

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
    } +
    logWrap {
      test("LogTest") {
        LogTest().test
        assert(2 == 1 + 1, "log test")
      }
    }
  }
}

object Wrap {
  def main(args: Array[String]): Unit = {
    run("wrap", new Wrap)
  }
}
