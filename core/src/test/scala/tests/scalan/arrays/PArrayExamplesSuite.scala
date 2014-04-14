package tests.scalan.arrays

import scalan.{ScalanCtxStaged, ScalanCtxSeq}
import org.scalatest.{Matchers, FlatSpec}
import scalan.arrays.{PArraysDslExp, PArraysDslSeq}
import tests.GraphVizExport
import tests.BaseShouldTests


class PArrayExamplesSuite extends BaseShouldTests {

  "when mixing trait" should "be constructed in Seq context" in {
      val ctx = new ScalanCtxSeq with PArraysDslSeq with PArrayExamples {}
  }
  
  it should "be constructed in Staged context" in {
    val ctx = new ScalanCtxStaged with PArraysDslExp with PArrayExamples {}
  }

  "in seq context" should "execute functions" in {
    val ctx = new ScalanCtxSeq with PArraysDslSeq with PArrayExamples {}
    val in = Array((1,2f), (3,4f), (5,6f))
    val res = ctx.fromAndTo(in)
    res should be(in)
  }
  
  val prefix = "test-out/scalan/arrays/"
  
  def testMethod(name: String) = {
    val ctx = new ScalanCtxStaged with PArraysDslExp with PArrayExamples with GraphVizExport {
      this.invokeEnabled = true //HACK: invoke all domain methods if possible //TODO this is not how it should be specified
    }
    val f = ctx.getStagedFunc(name)
    ctx.emitDepGraph(f, s"$prefix$name.dot", false)
  }

  val whenStaged = "when staged"
  whenStaged should "fromArray" beArgFor { testMethod(_) }
  whenStaged should "fromArrayOfPairs" beArgFor { testMethod(_) }
  whenStaged should "fromAndTo" beArgFor { testMethod(_) }
  whenStaged should "mapped" beArgFor { testMethod(_) }
  whenStaged should "zippedMap" beArgFor { testMethod(_) }
  whenStaged should "mapped2" beArgFor { testMethod(_) }
  whenStaged should "splitMap" beArgFor { testMethod(_) }
  whenStaged should "splitMap2" beArgFor { testMethod(_) }
  whenStaged should "mapInc3Times" beArgFor { testMethod(_) }
  whenStaged should "splitMap3" beArgFor { testMethod(_) }
  whenStaged should "splitMapMap" beArgFor { testMethod(_) }
  whenStaged should "mapScalar" beArgFor { testMethod(_) }

}