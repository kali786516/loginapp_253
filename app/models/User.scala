package models
import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

import scala.language.postfixOps

case class User(email: String,name:String,password: String)

//case class UserProfile(country: String,address: Option[String],age: Option[Int])


object User {

  /**
    * Parse a User from a ResultSet
    */
  val simple = {
    get[String]("user.email") ~
      get[String]("user.name") ~
      get[String]("user.password") map {
      case email~name~password => User(email,name,password)
    }
  }

  /**
    * Authenticate a User.
    */
  def authenticate(email: String, password: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL(
        """
         select * from user where
         email = {email} and password = {password}
        """
      ).on(
        'email -> email,
        'password -> password
      ).as(User.simple.singleOpt)
    }
  }

  def insertNewUser(username:String,password:String,email:String): Unit ={
    DB.withConnection { implicit connection =>
      SQL("INSERT INTO user(email,name,password) select * from (select '"+email+"' as email,'"+username+"' as username,'"+password+"' as password) as tmp where not exists (select email from user where email='"+email+"') limit 1").execute()
    }
  }




}
