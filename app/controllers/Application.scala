package controllers

import play.api._
import play.api.mvc._
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import anorm._
import models._
import views._

object Application extends Controller {


  val loginForm = Form(
    tuple(
      "email" -> text,
      "password" -> text
    ) verifying ("Invalid email or password", result => result match {
      case (email, password) => User.authenticate(email, password).isDefined
    })
  )

  def index = Action { implicit request =>
    Ok(views.html.index(loginForm)).withNewSession
    //Redirect(routes.PlayTutorialController.home)
  }

  /**
    * Login page.
    */
  def login = Action { implicit request =>
    Ok(html.login(loginForm))
  }

  def loginsuccess(username:String)=Action {
    Ok(html.loginsuccess(username))
  }


  def backToHLoginSuccess()=Action {
    Ok(html.loginsuccess("kali.tummala@gmail.com"))
  }




  /**
    * Handle login form submission.
    */
  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.login(formWithErrors)),
      user => Redirect(routes.Application.loginsuccess(user._1)).withSession("email" -> user._1)
    )
  }

  /**
    * Sign Up Form definition.
    *
    * Once defined it handle automatically, ,
    * validation, submission, errors, redisplaying, ...
    */
  val signupForm: Form[UserSignUp] = Form(

    // Define a mapping that will handle User values
    mapping(
      "username" -> text(minLength = 4),
      "email" -> email,

      // Create a tuple mapping for the password/confirm
      "password" -> tuple(
        "main" -> text(minLength = 6),
        "confirm" -> text
      ).verifying(
        // Add an additional constraint: both passwords must match
        "Passwords don't match", passwords => passwords._1 == passwords._2
      ),

      // Create a mapping that will handle UserProfile values
      "profile" -> mapping(
        "country" -> nonEmptyText,
        "address" -> optional(text),
        "age" -> optional(number(min = 18, max = 100))
      )
        // The mapping signature matches the UserProfile case class signature,
        // so we can use default apply/unapply functions here
        (UserProfile.apply)(UserProfile.unapply),

      "accept" -> checked("You must accept the conditions")

    )
      // The mapping signature doesn't match the User case class signature,
      // so we have to define custom binding/unbinding functions
    {
      // Binding: Create a User from the mapping result (ignore the second password and the accept field)
      (username, email, passwords, profile, _) => UserSignUp(username, passwords._1, email, profile)
    }
    {
      // Unbinding: Create the mapping values from an existing User value
      user => Some(user.username, user.email, (user.password, ""), user.profile, false)
    }.verifying(
      // Add an additional constraint: The username must not be taken (you could do an SQL request here)
      "This username is not available",
      user => !Seq("admin", "guest").contains(user.username)
    )
  )

  /**
    * Display an empty form.
    */
  def form = Action {
    Ok(html.signup(signupForm));
  }

  def signup=Action {
    Ok(views.html.signup(signupForm))
  }

  /**
    * Handle form submission.
    */
  def submit = Action { implicit request =>
    signupForm.bindFromRequest.fold(
      // Form has errors, redisplay it
      errors => BadRequest(html.signup(errors)),

      // We got a valid User value, display the summary
      //user => Ok(html.summary(user))
      user => {
        //println(user.username)
        //println(user.password)
        //println(user.email)
        User.insertNewUser(user.username,user.password,user.email)
        //BookDB.create(books)
        Ok(html.summary(user))
      }
    )
  }

  def aboutme=Action {
    Ok(views.html.aboutme())
  }

  def create=Action { implicit request =>
    ContactMe.form.bindFromRequest.fold(
      errors => BadRequest(views.html.contactmeform(ContactMe.form)),
      contmeinfo => {
        ContactMe.create(contmeinfo)
        Ok(html.contactemesummary(contmeinfo))
      }
    )
  }


  def contactme=Action {
    Ok(views.html.contactmeform(ContactMe.form))
  }


  /**
    * Describe the computer form (used in both edit and create screens).
    */
  val computerForm = Form(
    mapping(
      "id" -> ignored(NotAssigned:Pk[Long]),
      "name" -> nonEmptyText,
      "introduced" -> optional(date("yyyy-MM-dd")),
      "discontinued" -> optional(date("yyyy-MM-dd")),
      "company" -> optional(longNumber)
    )(Computer.apply)(Computer.unapply)
  )

  /**
    * Display the paginated list of computers.
    *
    * @param page Current page number (starts from 0)
    * @param orderBy Column to be sorted
    * @param filter Filter applied on computer names
    */
  def list(page: Int, orderBy: Int, filter: String) = Action { implicit request =>
    Ok(html.showcomputer(
      Computer.list(page = page, orderBy = orderBy, filter = ("%"+filter+"%")),
      orderBy, filter
    ))
  }

  /**
    * Display the 'edit form' of a existing Computer.
    *
    * @param id Id of the computer to edit
    */
  def edit(id: Long) = Action {
    Computer.findById(id).map { computer =>
      Ok(html.home())
    }.getOrElse(NotFound)
  }

  /**
    * Display the 'new computer form'.
    */

  def createComputer() = Action {
    Ok(html.addcomputer(computerForm, Company.options))
  }


  /**
    * Handle the 'new computer form' submission.
    */
  /*
  def create=Action { implicit request =>
    ContactMe.form.bindFromRequest.fold(
      errors => BadRequest(views.html.contactmeform(ContactMe.form)),
      contmeinfo => {
        ContactMe.create(contmeinfo)
        Ok(html.contactemesummary(contmeinfo))
      }
    )
  }*/


  def save = Action { implicit request =>
    computerForm.bindFromRequest.fold(
      errors => BadRequest(views.html.addcomputer(errors,Company.options)),
      computers => {
        Computer.insert(computers)
        Ok(html.addcomputersummary(computers))
        //Computer.insert(computers)
        //Home.flashing("success" -> "Computer %s has been created".format(computer.name))
      }
    )
  }



}