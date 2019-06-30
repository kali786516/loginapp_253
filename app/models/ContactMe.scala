package models

import anorm.SQL
import play.api.db.DB
import play.api.Play.current
import play.api.data._
import play.api.data.Forms._

case class ContactMe(name:String,email:String,phone:String,jobspec:String)

object ContactMe {

  val form=Form(
    mapping(
      "name" -> nonEmptyText,
      "email" -> nonEmptyText,
      "phone" -> nonEmptyText,
      "jobspec" -> nonEmptyText
    ) (ContactMe.apply)(ContactMe.unapply)
  )

  def create(contact: ContactMe): Unit ={
    DB.withConnection { implicit connection =>
      SQL("INSERT INTO contactme(name,email,phone,jobspec) VALUES ({name},{email},{phone},{jobspec})").on(
        "name" -> contact.name,
        "email" -> contact.email,
        "phone" -> contact.phone,
        "jobspec" -> contact.jobspec
      ).execute()
    }
  }


}
