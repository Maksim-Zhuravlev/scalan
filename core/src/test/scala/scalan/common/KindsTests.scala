package scalan.common

import scala.language.reflectiveCalls
import scalan._

class KindsTests extends BaseCtxTests {

  class ConvProgStaged extends TestContext with KindsExamples with KindsDslExp {
  }
  class ConvProgSeq extends ScalanCtxSeq with KindsExamples with KindsDslSeq {
  }

  test("simple kinds tests") {
    val ctx = new ConvProgStaged
    ctx.emit("t1", ctx.t1)
  }

  test("kindMap") {
    //pending
    val ctx = new ConvProgStaged
    ctx.emit("kindMap", ctx.kindMap)
  }

}
