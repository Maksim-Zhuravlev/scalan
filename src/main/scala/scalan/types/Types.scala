package scalan.types

import scala.annotation.implicitNotFound
import scala.annotation.unchecked.uncheckedVariance
import scalan.common.{DefaultOf, Common}
import Common._
import scala.language.implicitConversions
import scalan._

trait Types extends Base with TypesOps { self: TypesDsl =>

  type Ty[A] = Rep[Type[A]]
  trait Type[A] extends UserType[Type[A]] {
    implicit def eA: Elem[A]
    def typeCode: Rep[String]
    def defaultValue: Rep[A]
    def manifest: Manifest[A]
    def defaultOf: DefaultOf[Rep[A]]
  }

  abstract class BaseType[A](
      val typeCode: Rep[String],
      val defaultValue: Rep[A])(implicit eA: Elem[A])
    extends Type[A]
       with BaseTypeOps[A] { self: BaseTypeOps[A] =>
  }

}

