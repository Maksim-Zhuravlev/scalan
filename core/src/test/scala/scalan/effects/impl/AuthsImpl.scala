package scalan.examples
package impl

import scala.reflect.runtime.universe._
import scalan._
import scalan.monads._
import scala.reflect.runtime.universe._
import scala.reflect._
import scalan.common.Default

// Abs -----------------------------------
trait AuthenticationsAbs extends Authentications with Scalan {
  self: AuthenticationsDsl =>

  // single proxy for each type family
  implicit def proxyAuth[A](p: Rep[Auth[A]]): Auth[A] = {
    proxyOps[Auth[A]](p)(classTag[Auth[A]])
  }

  // familyElem
  class AuthElem[A, To <: Auth[A]](implicit val eA: Elem[A])
    extends EntityElem[To] {
    override def isEntityType = true
    override lazy val tag = {
      implicit val tagA = eA.tag
      weakTypeTag[Auth[A]].asInstanceOf[WeakTypeTag[To]]
    }
    override def convert(x: Rep[Reifiable[_]]) = convertAuth(x.asRep[Auth[A]])
    def convertAuth(x : Rep[Auth[A]]): Rep[To] = {
      //assert(x.selfType1.isInstanceOf[AuthElem[_, _]])
      x.asRep[To]
    }
    override def getDefaultRep: Rep[To] = ???
  }

  implicit def authElement[A](implicit eA: Elem[A]): Elem[Auth[A]] =
    new AuthElem[A, Auth[A]]

  implicit object AuthCompanionElem extends CompanionElem[AuthCompanionAbs] {
    lazy val tag = weakTypeTag[AuthCompanionAbs]
    protected def getDefaultRep = Auth
  }

  abstract class AuthCompanionAbs extends CompanionBase[AuthCompanionAbs] with AuthCompanion {
    override def toString = "Auth"
  }
  def Auth: Rep[AuthCompanionAbs]
  implicit def proxyAuthCompanion(p: Rep[AuthCompanion]): AuthCompanion = {
    proxyOps[AuthCompanion](p)
  }

  // elem for concrete class
  class LoginElem(val iso: Iso[LoginData, Login])
    extends AuthElem[$bar[Unit,String], Login]
    with ConcreteElem[LoginData, Login] {
    override def convertAuth(x: Rep[Auth[$bar[Unit,String]]]) = // Converter is not generated by meta
!!!("Cannot convert from Auth to Login: missing fields List(user, password)")
    override def getDefaultRep = super[ConcreteElem].getDefaultRep
    override lazy val tag = super[ConcreteElem].tag
  }

  // state representation type
  type LoginData = (String, String)

  // 3) Iso for concrete class
  class LoginIso
    extends Iso[LoginData, Login] {
    override def from(p: Rep[Login]) =
      (p.user, p.password)
    override def to(p: Rep[(String, String)]) = {
      val Pair(user, password) = p
      Login(user, password)
    }
    lazy val tag = {
      weakTypeTag[Login]
    }
    lazy val defaultRepTo = Default.defaultVal[Rep[Login]](Login("", ""))
    lazy val eTo = new LoginElem(this)
  }
  // 4) constructor and deconstructor
  abstract class LoginCompanionAbs extends CompanionBase[LoginCompanionAbs] with LoginCompanion {
    override def toString = "Login"
    def apply(p: Rep[LoginData]): Rep[Login] =
      isoLogin.to(p)
    def apply(user: Rep[String], password: Rep[String]): Rep[Login] =
      mkLogin(user, password)
  }
  object LoginMatcher {
    def unapply(p: Rep[Auth[$bar[Unit,String]]]) = unmkLogin(p)
  }
  def Login: Rep[LoginCompanionAbs]
  implicit def proxyLoginCompanion(p: Rep[LoginCompanionAbs]): LoginCompanionAbs = {
    proxyOps[LoginCompanionAbs](p)
  }

  implicit object LoginCompanionElem extends CompanionElem[LoginCompanionAbs] {
    lazy val tag = weakTypeTag[LoginCompanionAbs]
    protected def getDefaultRep = Login
  }

  implicit def proxyLogin(p: Rep[Login]): Login =
    proxyOps[Login](p)

  implicit class ExtendedLogin(p: Rep[Login]) {
    def toData: Rep[LoginData] = isoLogin.from(p)
  }

  // 5) implicit resolution of Iso
  implicit def isoLogin: Iso[LoginData, Login] =
    new LoginIso

  // 6) smart constructor and deconstructor
  def mkLogin(user: Rep[String], password: Rep[String]): Rep[Login]
  def unmkLogin(p: Rep[Auth[$bar[Unit,String]]]): Option[(Rep[String], Rep[String])]

  // elem for concrete class
  class HasPermissionElem(val iso: Iso[HasPermissionData, HasPermission])
    extends AuthElem[Boolean, HasPermission]
    with ConcreteElem[HasPermissionData, HasPermission] {
    override def convertAuth(x: Rep[Auth[Boolean]]) = // Converter is not generated by meta
!!!("Cannot convert from Auth to HasPermission: missing fields List(user, password)")
    override def getDefaultRep = super[ConcreteElem].getDefaultRep
    override lazy val tag = super[ConcreteElem].tag
  }

  // state representation type
  type HasPermissionData = (String, String)

  // 3) Iso for concrete class
  class HasPermissionIso
    extends Iso[HasPermissionData, HasPermission] {
    override def from(p: Rep[HasPermission]) =
      (p.user, p.password)
    override def to(p: Rep[(String, String)]) = {
      val Pair(user, password) = p
      HasPermission(user, password)
    }
    lazy val tag = {
      weakTypeTag[HasPermission]
    }
    lazy val defaultRepTo = Default.defaultVal[Rep[HasPermission]](HasPermission("", ""))
    lazy val eTo = new HasPermissionElem(this)
  }
  // 4) constructor and deconstructor
  abstract class HasPermissionCompanionAbs extends CompanionBase[HasPermissionCompanionAbs] with HasPermissionCompanion {
    override def toString = "HasPermission"
    def apply(p: Rep[HasPermissionData]): Rep[HasPermission] =
      isoHasPermission.to(p)
    def apply(user: Rep[String], password: Rep[String]): Rep[HasPermission] =
      mkHasPermission(user, password)
  }
  object HasPermissionMatcher {
    def unapply(p: Rep[Auth[Boolean]]) = unmkHasPermission(p)
  }
  def HasPermission: Rep[HasPermissionCompanionAbs]
  implicit def proxyHasPermissionCompanion(p: Rep[HasPermissionCompanionAbs]): HasPermissionCompanionAbs = {
    proxyOps[HasPermissionCompanionAbs](p)
  }

  implicit object HasPermissionCompanionElem extends CompanionElem[HasPermissionCompanionAbs] {
    lazy val tag = weakTypeTag[HasPermissionCompanionAbs]
    protected def getDefaultRep = HasPermission
  }

  implicit def proxyHasPermission(p: Rep[HasPermission]): HasPermission =
    proxyOps[HasPermission](p)

  implicit class ExtendedHasPermission(p: Rep[HasPermission]) {
    def toData: Rep[HasPermissionData] = isoHasPermission.from(p)
  }

  // 5) implicit resolution of Iso
  implicit def isoHasPermission: Iso[HasPermissionData, HasPermission] =
    new HasPermissionIso

  // 6) smart constructor and deconstructor
  def mkHasPermission(user: Rep[String], password: Rep[String]): Rep[HasPermission]
  def unmkHasPermission(p: Rep[Auth[Boolean]]): Option[(Rep[String], Rep[String])]
}

// Seq -----------------------------------
trait AuthenticationsSeq extends AuthenticationsDsl with ScalanSeq {
  self: AuthenticationsDslSeq =>
  lazy val Auth: Rep[AuthCompanionAbs] = new AuthCompanionAbs with UserTypeSeq[AuthCompanionAbs] {
    lazy val selfType = element[AuthCompanionAbs]
  }

  case class SeqLogin
      (override val user: Rep[String], override val password: Rep[String])

    extends Login(user, password)
        with UserTypeSeq[Login] {
    lazy val selfType = element[Login]
  }
  lazy val Login = new LoginCompanionAbs with UserTypeSeq[LoginCompanionAbs] {
    lazy val selfType = element[LoginCompanionAbs]
  }

  def mkLogin
      (user: Rep[String], password: Rep[String]): Rep[Login] =
      new SeqLogin(user, password)
  def unmkLogin(p: Rep[Auth[$bar[Unit,String]]]) = p match {
    case p: Login @unchecked =>
      Some((p.user, p.password))
    case _ => None
  }

  case class SeqHasPermission
      (override val user: Rep[String], override val password: Rep[String])

    extends HasPermission(user, password)
        with UserTypeSeq[HasPermission] {
    lazy val selfType = element[HasPermission]
  }
  lazy val HasPermission = new HasPermissionCompanionAbs with UserTypeSeq[HasPermissionCompanionAbs] {
    lazy val selfType = element[HasPermissionCompanionAbs]
  }

  def mkHasPermission
      (user: Rep[String], password: Rep[String]): Rep[HasPermission] =
      new SeqHasPermission(user, password)
  def unmkHasPermission(p: Rep[Auth[Boolean]]) = p match {
    case p: HasPermission @unchecked =>
      Some((p.user, p.password))
    case _ => None
  }
}

// Exp -----------------------------------
trait AuthenticationsExp extends AuthenticationsDsl with ScalanExp {
  self: AuthenticationsDslExp =>
  lazy val Auth: Rep[AuthCompanionAbs] = new AuthCompanionAbs with UserTypeDef[AuthCompanionAbs] {
    lazy val selfType = element[AuthCompanionAbs]
    override def mirror(t: Transformer) = this
  }

  case class ExpLogin
      (override val user: Rep[String], override val password: Rep[String])

    extends Login(user, password) with UserTypeDef[Login] {
    lazy val selfType = element[Login]
    override def mirror(t: Transformer) = ExpLogin(t(user), t(password))
  }

  lazy val Login: Rep[LoginCompanionAbs] = new LoginCompanionAbs with UserTypeDef[LoginCompanionAbs] {
    lazy val selfType = element[LoginCompanionAbs]
    override def mirror(t: Transformer) = this
  }

  object LoginMethods {
    object eA {
      def unapply(d: Def[_]): Option[Rep[Login]] = d match {
        case MethodCall(receiver, method, _, _) if receiver.elem.isInstanceOf[LoginElem] && method.getName == "eA" =>
          Some(receiver).asInstanceOf[Option[Rep[Login]]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[Rep[Login]] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    object toOper {
      def unapply(d: Def[_]): Option[Rep[Login]] = d match {
        case MethodCall(receiver, method, _, _) if receiver.elem.isInstanceOf[LoginElem] && method.getName == "toOper" =>
          Some(receiver).asInstanceOf[Option[Rep[Login]]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[Rep[Login]] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }
  }

  object LoginCompanionMethods {
  }

  def mkLogin
    (user: Rep[String], password: Rep[String]): Rep[Login] =
    new ExpLogin(user, password)
  def unmkLogin(p: Rep[Auth[$bar[Unit,String]]]) = p.elem.asInstanceOf[Elem[_]] match {
    case _: LoginElem @unchecked =>
      Some((p.asRep[Login].user, p.asRep[Login].password))
    case _ =>
      None
  }

  case class ExpHasPermission
      (override val user: Rep[String], override val password: Rep[String])

    extends HasPermission(user, password) with UserTypeDef[HasPermission] {
    lazy val selfType = element[HasPermission]
    override def mirror(t: Transformer) = ExpHasPermission(t(user), t(password))
  }

  lazy val HasPermission: Rep[HasPermissionCompanionAbs] = new HasPermissionCompanionAbs with UserTypeDef[HasPermissionCompanionAbs] {
    lazy val selfType = element[HasPermissionCompanionAbs]
    override def mirror(t: Transformer) = this
  }

  object HasPermissionMethods {
    object eA {
      def unapply(d: Def[_]): Option[Rep[HasPermission]] = d match {
        case MethodCall(receiver, method, _, _) if receiver.elem.isInstanceOf[HasPermissionElem] && method.getName == "eA" =>
          Some(receiver).asInstanceOf[Option[Rep[HasPermission]]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[Rep[HasPermission]] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    object toOper {
      def unapply(d: Def[_]): Option[Rep[HasPermission]] = d match {
        case MethodCall(receiver, method, _, _) if receiver.elem.isInstanceOf[HasPermissionElem] && method.getName == "toOper" =>
          Some(receiver).asInstanceOf[Option[Rep[HasPermission]]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[Rep[HasPermission]] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }
  }

  object HasPermissionCompanionMethods {
  }

  def mkHasPermission
    (user: Rep[String], password: Rep[String]): Rep[HasPermission] =
    new ExpHasPermission(user, password)
  def unmkHasPermission(p: Rep[Auth[Boolean]]) = p.elem.asInstanceOf[Elem[_]] match {
    case _: HasPermissionElem @unchecked =>
      Some((p.asRep[HasPermission].user, p.asRep[HasPermission].password))
    case _ =>
      None
  }

  object AuthMethods {
    object toOper {
      def unapply(d: Def[_]): Option[Rep[Auth[A]] forSome {type A}] = d match {
        case MethodCall(receiver, method, _, _) if receiver.elem.isInstanceOf[AuthElem[_, _]] && method.getName == "toOper" =>
          Some(receiver).asInstanceOf[Option[Rep[Auth[A]] forSome {type A}]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[Rep[Auth[A]] forSome {type A}] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }
  }

  object AuthCompanionMethods {
  }
}
