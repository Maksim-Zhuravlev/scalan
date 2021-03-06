package scalan.effects

import scalan.compilation.lms.cxx.LmsCompilerCxx
import scalan.compilation.lms.uni.{LmsBackendUni, LmsCompilerUni}
import scalan.examples.{AuthenticationsDslExp, AuthenticationsDslSeq, InteractionsDslSeq, InteractionsDslExp}
import scalan.it.lms.ItTestsUtilLmsCxx
import scalan.monads.MonadsDslExp
import scalan.primitives.EffectfulCompiler
import scalan._
import scalan.collections.{MultiMapsDslSeq}
import scalan.compilation.lms._
import scalan.compilation.lms.scalac.CommunityLmsCompilerScala
import scalan.compilation.lms.cxx.sharedptr.CommunityCxxShptrLmsBackend
import scalan.it.{ItTestsUtil, BaseItTests}

class EffectsItTests extends BaseItTests with ItTestsUtilLmsCxx
{

  trait EffectsSeq extends ScalanCommunitySeq with ScalanCommunityDslSeq
                      with MultiMapsDslSeq

  class EffectsExp extends ScalanCommunityDslExp with InteractExample with InteractionsDslExp with JNIExtractorOpsExp

  val progInteractScala = new CommunityLmsCompilerScala(new EffectsExp) with CoreBridge with EffectfulCompiler[EffectsExp]

  val progInteractUni = new LmsCompilerUni(new EffectsExp) with CoreBridge with EffectfulCompiler[EffectsExp]

  test("runInteract")  {
    val progSeq = new EffectsSeq with InteractExample with InteractionsDslSeq
    val progStaged = progInteractScala
    val in = 10
    val actual = getStagedOutput(progStaged)(_.runAppW, "runInteract", in)
  }

  test("runInteract2")  {
    val progSeq = new EffectsSeq with InteractExample with InteractionsDslSeq
    val progStaged = progInteractScala
    val in = 10
    val actual = getStagedOutput(progStaged)(_.runApp2W, "runInteract2", in)
  }

  // TODO: Slow test
  ignore("runCrossDomain")  {
    val progSeq = new EffectsSeq with CrossDomainExample
      with InteractionsDslSeq with AuthenticationsDslSeq
    val progStaged =
      new CommunityLmsCompilerScala(new ScalanCommunityDslExp with CrossDomainExample with InteractionsDslExp with AuthenticationsDslExp) with CoreBridge with EffectfulCompiler[ScalanCommunityDslExp with CrossDomainExample with InteractionsDslExp with AuthenticationsDslExp]
    val in = 10
    val actual = getStagedOutput(progStaged)(_.runAppW, "runCrossDomain", in)
  }

  trait IfBranchesExamples extends ScalanCommunityDsl {
    lazy val t1 = fun { (in: Rep[String]) =>
      IF (in.contains("abc")) THEN { console_printlnE(in) } ELSE { console_printlnE(in) }
    }
    lazy val t2 = fun { (in: Rep[String]) =>
      val input = console_readlineE()
      val user = IF (input !== (null: String)) { input } ELSE { "admin" }
      IF (in.contains(user)) THEN {
        console_printlnE(in)
      } ELSE {
        console_printlnE(in + "rejected")
      }
    }
  }

  test("ifBranches")  {
    val progStaged = new CommunityLmsCompilerScala(new ScalanCommunityDslExp with IfBranchesExamples) with CoreBridge with EffectfulCompiler[ScalanCommunityDslExp with IfBranchesExamples]
    //pending
    val in = "abc"
    ///val actual = getStagedOutput(progStaged)(progStaged.t1, "t1", in)
    val actual2 = getStagedOutput(progStaged)(_.t2, "t2", in)
  }

  class ScalanState0 extends ScalanCommunityDslExp with StateExamples with MonadsDslExp with JNIExtractorOpsExp {
    val State = new State0Manager[Int]
  }

  val progState0Scala = new CommunityLmsCompilerScala(new ScalanState0) with CoreBridge with EffectfulCompiler[ScalanState0]

  val progState0Uni = new LmsCompilerUni(new ScalanState0) with CoreBridge with EffectfulCompiler[ScalanState0]

  test("zipArrayWithIndex")  {
    val in = Array(10.0, 20.0, 30.0)
    val res = getStagedOutput(progState0Scala)(_.zipArrayWithIndexW, "zipArrayWithIndex", in)

    val resU = getStagedOutput(progState0Uni)(_.zipArrayWithIndexW, "zipArrayWithIndex", in)

    assert(res.sameElements(resU))
  }

  test("zipCollectionWithIndex")  {
    //pending
    val in = Array(10.0, 20.0, 30.0)
    val res = getStagedOutput(progState0Scala)(_.zipCollectionWithIndexW, "zipCollectionWithIndex", in)

    val resU = getStagedOutput(progState0Uni)(_.zipCollectionWithIndexW, "zipCollectionWithIndex", in)

    assert(res.sameElements(resU))
  }

  test("zipCollectionWithIndex2")  {
    //pending
    val in = Array(10.0, 20.0, 30.0)
    val res = getStagedOutput(progState0Scala)(_.zipCollectionWithIndexW2, "zipCollectionWithIndex2", in)

    val resU = getStagedOutput(progState0Uni)(_.zipCollectionWithIndexW2, "zipCollectionWithIndex2", in)

    assert(res.sameElements(resU))
  }

  test("zipCollectionWithIndex3")  {
    //pending
    val in = Array(10.0, 20.0, 30.0)
    val res = getStagedOutput(progState0Scala)(_.zipCollectionWithIndexW3, "zipCollectionWithIndex3", in)

    val resU = getStagedOutput(progState0Uni)(_.zipCollectionWithIndexW3, "zipCollectionWithIndex3", in)

    assert(res.sameElements(resU))
  }

  // TODO: Slow test, takes a very long time due to the problems with higher-kinded types
  ignore("zipCollectionWithIndex3_Free")  {
    class ScalanStateF extends ScalanCommunityDslExp with StateExamples with MonadsDslExp {
      val State = new FreeStateManager[Int]
    }

    val progStaged = new CommunityLmsCompilerScala(new ScalanStateF) with CoreBridge with EffectfulCompiler[ScalanStateF]

    val in = Array(10.0, 20.0, 30.0)
    val res = getStagedOutput(progStaged)(_.zipCollectionWithIndexW3, "zipCollectionWithIndex3_Free", in)
  }
}

class EffectsJniItTests extends BaseItTests with ItTestsUtilLmsCxx {

  class EffectsExpCxx extends ScalanCommunityDslExp with JNIExtractorOpsExp with StateExamples with MonadsDslExp
  {

    override val State = new State0Manager[Int]

    lazy val jniZipArrayWithIndexW = JNI_Wrap(zipArrayWithIndexW)

    lazy val jniZipCollectionWithIndexW = JNI_Wrap(zipCollectionWithIndexW)

    lazy val jniZipCollectionWithIndexW2 = JNI_Wrap(zipCollectionWithIndexW2)

    lazy val jniZipCollectionWithIndexW3 = JNI_Wrap(zipCollectionWithIndexW3)
  }
  val progcxx = new LmsCompilerCxx(new EffectsExpCxx) with CoreBridge with JNIBridge with EffectfulCompiler[EffectsExpCxx]

  test("jniZipArrayWithIndex") {
    generate(progcxx)(progcxx.scalan.jniZipArrayWithIndexW,"jniZipArrayWithIndex")(progcxx.defaultCompilerConfig)
  }
  test("jniZipCollectionWithIndex") {
    generate(progcxx)(progcxx.scalan.jniZipCollectionWithIndexW,"jniZipCollectionWithIndex")(progcxx.defaultCompilerConfig)
  }
  test("jniZipCollectionWithIndex2") {
    generate(progcxx)(progcxx.scalan.jniZipCollectionWithIndexW2,"jniZipCollectionWithIndex2")(progcxx.defaultCompilerConfig)
  }
  test("jniZipCollectionWithIndex3") {
    generate(progcxx)(progcxx.scalan.jniZipCollectionWithIndexW3,"jniZipCollectionWithIndex3")(progcxx.defaultCompilerConfig)
  }
}
