package controllers

import javax.inject._
import models._
import play.api.mvc._
import form.FormMappingData
import org.mindrot.jbcrypt.BCrypt
import play.api.Logger

import scala.concurrent.{ExecutionContext, Future}

class UserController @Inject()(cc: MessagesControllerComponents,
                               formData: FormMappingData,
                               repo: UserRepository
                                )(implicit ec: ExecutionContext)
  extends MessagesAbstractController(cc) {

  def welcome: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.welcome("welcome to my app!"))
  }

  def signUp: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.signUp(formData.signUpForm))
  }

  def addUser: Action[AnyContent] = Action.async { implicit request =>
    formData.signUpForm.bindFromRequest.fold(
      errorForm => { println("Error while creating an account" + errorForm )
        Future.successful(Redirect(routes.UserController.signUp()).flashing("Error" -> "Fill Form Correctly"))
      },
      users =>{
        println(s"###User ${users.username}")
        repo.findByUsername(users.username).flatMap{
          case Some(value) => println("username already exist, try with new one" + value)
            Future.successful(Redirect(routes.UserController.signUp()).flashing("Error" -> "username Already exists"))
          case None => println("UserInfo after creating an account : " + users)
            val encryptPassword = BCrypt.hashpw(users.password, BCrypt.gensalt)
            repo.store(User(0, users.firstName, users.middleName, users.lastName,
              users.username, encryptPassword, users.mobileNumber, users.gender, users.age)).map{ _ =>
              println("Redirecting to profile page")
              Redirect(routes.UserController.profile()).flashing("success"-> "user created")
            }
        }
      }
    )
  }

  def signIn: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.signIn(formData.signInForm))
  }

  def signInCheck: Action[AnyContent] =Action.async { implicit request =>
    formData.signInForm.bindFromRequest.fold(
      errorForm => { println("Error while signing in" + errorForm)
        Future.successful(Redirect(routes.UserController.signIn()).flashing("Error"->"Fill Form Correctly"))
      },
      users => {
        repo.findByUsername(users.username).flatMap{
          case Some(value)=>
            println("Checking for user" + value)
            repo.checkSignInValue(users.username,users.password).flatMap
            {
              case true =>{ println("Successfully signed in")
                Future.successful(Redirect(routes.UserController.profile())
                    .flashing("Success" -> "SignIn successfully").withSession("user"->users.username))}
              case false =>{ println("wrong username or password")
                Future.successful(Redirect(routes.UserController.signIn()).flashing("Error"->"Wrong Username or Password"))}
            }
          case None =>
            println("no username found, enter a valid username")
            Future.successful(Redirect(routes.UserController.signIn()).flashing("Error"-> "No user by this username"))
        }
      })
  }
  
  def updatePassword: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.updatePassword(formData.updatePasswordForm))
  }

  def passwordUpdate: Action[AnyContent] = Action.async { implicit request =>
    formData.updatePasswordForm.bindFromRequest.fold(
      errorForm => { println("error in password update" + errorForm)
        Future.successful(Redirect(routes.UserController.updatePassword()).flashing("Error" -> "Fill Form Correctly"))
      },
      users =>{
        repo.findByUsername(users.username).flatMap{
          case Some(value) => println("username found" + value)
            val encryptPassword = BCrypt.hashpw(users.password, BCrypt.gensalt)
            repo.updatePassword(encryptPassword,users.username).map{
              case true => println("redirecting to profile page")
                Redirect(routes.UserController.profile()).withNewSession
              case false => println("enter a valid password")
                Redirect(routes.UserController.updatePassword()).flashing("Error"->"Try Again")
            }
          case None => println("enter a valid username")
            Future.successful(Redirect(routes.UserController.updatePassword()).flashing("Error" -> "enter a valid username"))
        }
      }
    )
  }

  def home: Action[AnyContent] = Action{ implicit request =>
    Ok(views.html.home("WELCOME"))
  }

  def profile:Action[AnyContent] = Action { implicit request =>
    Ok(views.html.profile(formData.profileForm))
  }

  def updateProfile: Action[AnyContent] = Action.async { implicit request =>
    val username = request.session.get("user")
    username match {
      case Some(user) =>
        formData.profileForm.bindFromRequest.fold(
          errorForm => { println("error while updating profile" + errorForm)
            Future.successful(Redirect(routes.UserController.updateProfile()).flashing("Error" -> "update form currently"))
          },
          profile => {
            repo.updateProfile(profile, user).flatMap {
              case true => println("profile is updated")
                Future.successful(Redirect(routes.UserController.profile()).flashing("Success"->"Your Profile is updated"))
              case false => println("can't update profile")
                Future.successful(Redirect(routes.UserController.profile()).withNewSession)
            }
          })
      case None => println("unauthorised, you need to sign in")
        Future.successful(Redirect(routes.UserController.profile()).flashing("unauthorised" -> "You need to sign in first!").withNewSession)
    }
  }
  
}
