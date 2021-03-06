package scalan.examples

import scala.io.StdIn
import scala.reflect.runtime.universe._
import scalan._
import scalan.monads._

trait Interactions { self: InteractionsDsl =>
  val OperM: Monad[Oper]
  import OperM.toMonadic

  type RepInteract[A] = Rep[Interact[A]]
  trait Interact[A] extends Reifiable[Interact[A]] {
    implicit def eA: Elem[A]
    def toOper: Rep[Oper[A]]
  }
  trait InteractCompanion

  abstract class Ask(val prompt: Rep[String]) extends Interact[String] {
    def eA: Elem[String] = element[String]
    def toOper = {
      for {
        _ <- println(prompt)
        res <- readLine
      } yield res
    }
  }
  trait AskCompanion

  abstract class Tell(val msg: Rep[String]) extends Interact[Unit] {
    def eA: Elem[Unit] = element[Unit]
    def toOper = println(msg)
  }
  trait TellCompanion
}

trait InteractionsDsl extends ScalanDsl with impl.InteractionsAbs with Interactions
    with MonadsDsl {

  implicit def interactCont: Cont[Interact] = new Container[Interact] {
    def tag[T](implicit tT: WeakTypeTag[T]) = weakTypeTag[Interact[T]]
    def lift[T](implicit eT: Elem[T]) = element[Interact[T]]
  }

  class Interacts[F[_]:Cont](implicit I: Inject[Interact,F]) {
    def tell(msg: Rep[String]): Rep[Free[F,Unit]] = lift(Tell(msg))
    def ask(prompt: Rep[String]): Rep[Free[F,String]] = lift(Ask(prompt))
  }
  object Interacts {
    implicit def instance[F[_]:Cont](implicit I: Inject[Interact,F]): Interacts[F] = new Interacts[F]
  }

  object InteractOper extends (Interact ~> Oper) {
    def cIn = container[Interact]
    def cOut = container[Oper]
    def apply[A:Elem](i: Rep[Interact[A]]): Rep[Oper[A]] = i.toOper
  }

  def println(s: Rep[String]): Rep[Oper[Unit]]
  def readLine: Rep[Oper[String]]
}

trait InteractionsDslSeq extends InteractionsDsl with impl.InteractionsSeq with ScalanCtxSeq with MonadsDslSeq {
  def println(s: Rep[String]): Rep[Oper[Unit]] = i => (i + 1, Predef.println(s))
  def readLine: Rep[Oper[String]] = i => (i + 1, StdIn.readLine())
}

trait InteractionsDslExp extends InteractionsDsl with impl.InteractionsExp with ScalanExp with MonadsDslExp {
  def println(s: Rep[String]): Rep[Oper[Unit]] = fun { i => Println(i, s) }
  def readLine: Rep[Oper[String]] = fun { (i: Rep[Int]) => ReadLine(i) }

//  case class Println(i: Rep[Int], s: Rep[String]) extends BaseDef[(Int, Unit)]  {
//    override def mirror(t: Transformer) = Println(t(i), t(s))
//  }
//
//  case class ReadLine(i: Rep[Int]) extends BaseDef[(Int, String)]  {
//    override def mirror(t: Transformer) = ReadLine(t(i))
//  }
}