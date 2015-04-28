package scalan.examples
package impl

import scala.reflect.runtime.universe._
import scalan._
import scalan.monads._
import scala.reflect.runtime.universe._
import scala.reflect._
import scalan.common.Default

// Abs -----------------------------------
trait InteractionsAbs extends Interactions with Scalan {
  self: InteractionsDsl =>

  // single proxy for each type family
  implicit def proxyInteract[A](p: Rep[Interact[A]]): Interact[A] = {
    proxyOps[Interact[A]](p)(classTag[Interact[A]])
  }

  // familyElem
  class InteractElem[A, To <: Interact[A]](implicit val eA: Elem[A])
    extends EntityElem[To] {
    override def isEntityType = true
    override lazy val tag = {
      implicit val tagA = eA.tag
      weakTypeTag[Interact[A]].asInstanceOf[WeakTypeTag[To]]
    }
    override def convert(x: Rep[Reifiable[_]]) = convertInteract(x.asRep[Interact[A]])
    def convertInteract(x : Rep[Interact[A]]): Rep[To] = {
      //assert(x.selfType1.isInstanceOf[InteractElem[_, _]])
      x.asRep[To]
    }
    override def getDefaultRep: Rep[To] = ???
  }

  implicit def interactElement[A](implicit eA: Elem[A]): Elem[Interact[A]] =
    new InteractElem[A, Interact[A]]

  implicit object InteractCompanionElem extends CompanionElem[InteractCompanionAbs] {
    lazy val tag = weakTypeTag[InteractCompanionAbs]
    protected def getDefaultRep = Interact
  }

  abstract class InteractCompanionAbs extends CompanionBase[InteractCompanionAbs] with InteractCompanion {
    override def toString = "Interact"
  }
  def Interact: Rep[InteractCompanionAbs]
  implicit def proxyInteractCompanion(p: Rep[InteractCompanion]): InteractCompanion = {
    proxyOps[InteractCompanion](p)
  }

  // elem for concrete class
  class AskElem(val iso: Iso[AskData, Ask])
    extends InteractElem[String, Ask]
    with ConcreteElem[AskData, Ask] {
    override def convertInteract(x: Rep[Interact[String]]) = // Converter is not generated by meta
!!!("Cannot convert from Interact to Ask: missing fields List(prompt)")
    override def getDefaultRep = super[ConcreteElem].getDefaultRep
    override lazy val tag = super[ConcreteElem].tag
  }

  // state representation type
  type AskData = String

  // 3) Iso for concrete class
  class AskIso
    extends Iso[AskData, Ask] {
    override def from(p: Rep[Ask]) =
      p.prompt
    override def to(p: Rep[String]) = {
      val prompt = p
      Ask(prompt)
    }
    lazy val tag = {
      weakTypeTag[Ask]
    }
    lazy val defaultRepTo = Default.defaultVal[Rep[Ask]](Ask(""))
    lazy val eTo = new AskElem(this)
  }
  // 4) constructor and deconstructor
  abstract class AskCompanionAbs extends CompanionBase[AskCompanionAbs] with AskCompanion {
    override def toString = "Ask"

    def apply(prompt: Rep[String]): Rep[Ask] =
      mkAsk(prompt)
  }
  object AskMatcher {
    def unapply(p: Rep[Interact[String]]) = unmkAsk(p)
  }
  def Ask: Rep[AskCompanionAbs]
  implicit def proxyAskCompanion(p: Rep[AskCompanionAbs]): AskCompanionAbs = {
    proxyOps[AskCompanionAbs](p)
  }

  implicit object AskCompanionElem extends CompanionElem[AskCompanionAbs] {
    lazy val tag = weakTypeTag[AskCompanionAbs]
    protected def getDefaultRep = Ask
  }

  implicit def proxyAsk(p: Rep[Ask]): Ask =
    proxyOps[Ask](p)

  implicit class ExtendedAsk(p: Rep[Ask]) {
    def toData: Rep[AskData] = isoAsk.from(p)
  }

  // 5) implicit resolution of Iso
  implicit def isoAsk: Iso[AskData, Ask] =
    new AskIso

  // 6) smart constructor and deconstructor
  def mkAsk(prompt: Rep[String]): Rep[Ask]
  def unmkAsk(p: Rep[Interact[String]]): Option[(Rep[String])]

  // elem for concrete class
  class TellElem(val iso: Iso[TellData, Tell])
    extends InteractElem[Unit, Tell]
    with ConcreteElem[TellData, Tell] {
    override def convertInteract(x: Rep[Interact[Unit]]) = // Converter is not generated by meta
!!!("Cannot convert from Interact to Tell: missing fields List(msg)")
    override def getDefaultRep = super[ConcreteElem].getDefaultRep
    override lazy val tag = super[ConcreteElem].tag
  }

  // state representation type
  type TellData = String

  // 3) Iso for concrete class
  class TellIso
    extends Iso[TellData, Tell] {
    override def from(p: Rep[Tell]) =
      p.msg
    override def to(p: Rep[String]) = {
      val msg = p
      Tell(msg)
    }
    lazy val tag = {
      weakTypeTag[Tell]
    }
    lazy val defaultRepTo = Default.defaultVal[Rep[Tell]](Tell(""))
    lazy val eTo = new TellElem(this)
  }
  // 4) constructor and deconstructor
  abstract class TellCompanionAbs extends CompanionBase[TellCompanionAbs] with TellCompanion {
    override def toString = "Tell"

    def apply(msg: Rep[String]): Rep[Tell] =
      mkTell(msg)
  }
  object TellMatcher {
    def unapply(p: Rep[Interact[Unit]]) = unmkTell(p)
  }
  def Tell: Rep[TellCompanionAbs]
  implicit def proxyTellCompanion(p: Rep[TellCompanionAbs]): TellCompanionAbs = {
    proxyOps[TellCompanionAbs](p)
  }

  implicit object TellCompanionElem extends CompanionElem[TellCompanionAbs] {
    lazy val tag = weakTypeTag[TellCompanionAbs]
    protected def getDefaultRep = Tell
  }

  implicit def proxyTell(p: Rep[Tell]): Tell =
    proxyOps[Tell](p)

  implicit class ExtendedTell(p: Rep[Tell]) {
    def toData: Rep[TellData] = isoTell.from(p)
  }

  // 5) implicit resolution of Iso
  implicit def isoTell: Iso[TellData, Tell] =
    new TellIso

  // 6) smart constructor and deconstructor
  def mkTell(msg: Rep[String]): Rep[Tell]
  def unmkTell(p: Rep[Interact[Unit]]): Option[(Rep[String])]
}

// Seq -----------------------------------
trait InteractionsSeq extends InteractionsDsl with ScalanSeq {
  self: InteractionsDslSeq =>
  lazy val Interact: Rep[InteractCompanionAbs] = new InteractCompanionAbs with UserTypeSeq[InteractCompanionAbs] {
    lazy val selfType = element[InteractCompanionAbs]
  }

  case class SeqAsk
      (override val prompt: Rep[String])

    extends Ask(prompt)
        with UserTypeSeq[Ask] {
    lazy val selfType = element[Ask]
  }
  lazy val Ask = new AskCompanionAbs with UserTypeSeq[AskCompanionAbs] {
    lazy val selfType = element[AskCompanionAbs]
  }

  def mkAsk
      (prompt: Rep[String]): Rep[Ask] =
      new SeqAsk(prompt)
  def unmkAsk(p: Rep[Interact[String]]) = p match {
    case p: Ask @unchecked =>
      Some((p.prompt))
    case _ => None
  }

  case class SeqTell
      (override val msg: Rep[String])

    extends Tell(msg)
        with UserTypeSeq[Tell] {
    lazy val selfType = element[Tell]
  }
  lazy val Tell = new TellCompanionAbs with UserTypeSeq[TellCompanionAbs] {
    lazy val selfType = element[TellCompanionAbs]
  }

  def mkTell
      (msg: Rep[String]): Rep[Tell] =
      new SeqTell(msg)
  def unmkTell(p: Rep[Interact[Unit]]) = p match {
    case p: Tell @unchecked =>
      Some((p.msg))
    case _ => None
  }
}

// Exp -----------------------------------
trait InteractionsExp extends InteractionsDsl with ScalanExp {
  self: InteractionsDslExp =>
  lazy val Interact: Rep[InteractCompanionAbs] = new InteractCompanionAbs with UserTypeDef[InteractCompanionAbs] {
    lazy val selfType = element[InteractCompanionAbs]
    override def mirror(t: Transformer) = this
  }

  case class ExpAsk
      (override val prompt: Rep[String])

    extends Ask(prompt) with UserTypeDef[Ask] {
    lazy val selfType = element[Ask]
    override def mirror(t: Transformer) = ExpAsk(t(prompt))
  }

  lazy val Ask: Rep[AskCompanionAbs] = new AskCompanionAbs with UserTypeDef[AskCompanionAbs] {
    lazy val selfType = element[AskCompanionAbs]
    override def mirror(t: Transformer) = this
  }

  object AskMethods {
    object toOper {
      def unapply(d: Def[_]): Option[Rep[Ask]] = d match {
        case MethodCall(receiver, method, _, _) if receiver.elem.isInstanceOf[AskElem] && method.getName == "toOper" =>
          Some(receiver).asInstanceOf[Option[Rep[Ask]]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[Rep[Ask]] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }
  }

  object AskCompanionMethods {
  }

  def mkAsk
    (prompt: Rep[String]): Rep[Ask] =
    new ExpAsk(prompt)
  def unmkAsk(p: Rep[Interact[String]]) = p.elem.asInstanceOf[Elem[_]] match {
    case _: AskElem @unchecked =>
      Some((p.asRep[Ask].prompt))
    case _ =>
      None
  }

  case class ExpTell
      (override val msg: Rep[String])

    extends Tell(msg) with UserTypeDef[Tell] {
    lazy val selfType = element[Tell]
    override def mirror(t: Transformer) = ExpTell(t(msg))
  }

  lazy val Tell: Rep[TellCompanionAbs] = new TellCompanionAbs with UserTypeDef[TellCompanionAbs] {
    lazy val selfType = element[TellCompanionAbs]
    override def mirror(t: Transformer) = this
  }

  object TellMethods {
    object toOper {
      def unapply(d: Def[_]): Option[Rep[Tell]] = d match {
        case MethodCall(receiver, method, _, _) if receiver.elem.isInstanceOf[TellElem] && method.getName == "toOper" =>
          Some(receiver).asInstanceOf[Option[Rep[Tell]]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[Rep[Tell]] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }
  }

  object TellCompanionMethods {
  }

  def mkTell
    (msg: Rep[String]): Rep[Tell] =
    new ExpTell(msg)
  def unmkTell(p: Rep[Interact[Unit]]) = p.elem.asInstanceOf[Elem[_]] match {
    case _: TellElem @unchecked =>
      Some((p.asRep[Tell].msg))
    case _ =>
      None
  }

  object InteractMethods {
    object toOper {
      def unapply(d: Def[_]): Option[Rep[Interact[A]] forSome {type A}] = d match {
        case MethodCall(receiver, method, _, _) if receiver.elem.isInstanceOf[InteractElem[_, _]] && method.getName == "toOper" =>
          Some(receiver).asInstanceOf[Option[Rep[Interact[A]] forSome {type A}]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[Rep[Interact[A]] forSome {type A}] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }
  }

  object InteractCompanionMethods {
  }
}
