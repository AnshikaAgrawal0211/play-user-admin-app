package controllers

import javax.inject._
import models._
import play.api.mvc._
import form.FormMappingData
import org.mindrot.jbcrypt.BCrypt

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
      errorForm => {
        Future.successful(Redirect(routes.UserController.signUp()).flashing("Error" -> "Fill Form Correctly"))
      },
      users =>{
        repo.findByUsername(users.username).flatMap{
          case Some(value) =>
            Future.successful(Redirect(routes.UserController.signUp()).flashing("Error" -> "username Already exists"))
          case None =>
            val encryptPassword = BCrypt.hashpw(users.password, BCrypt.gensalt)
            repo.store(User(0, users.firstName, users.middleName, users.lastName,
              users.username, encryptPassword, users.mobileNo, users.gender, users.age)).map{ _ =>
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
      formWithErrors => {
        Future.successful(Redirect(routes.UserController.signIn()).flashing("Error"->"Fill Form Correctly"))
      },
      users => {
        repo.findByUsername(users.username).flatMap{

          case Some(value)=>
            repo.checkSignInValue(users.username,users.password).flatMap
            {
              case true => Future.successful(Redirect(routes.UserController.profile())
                    .flashing("Success" -> "Thank You for registration as user").withSession("user"->users.username))
              case false => Future.successful(Redirect(routes.UserController.signIn()).flashing("Error"->"Wrong Username or Password"))
            }
          case None => Future.successful(Redirect(routes.UserController.signIn()).flashing("Error"-> "No user by this username"))
        }
      })
  }
  
  def updatePassword: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.updatePassword(formData.updatePasswordForm))
  }

  def passwordUpdate: Action[AnyContent] = Action.async { implicit request =>
    formData.updatePasswordForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Redirect(routes.UserController.updatePassword()).flashing("Error" -> "Fill Form Correctly"))
      },
      users =>{
        repo.findByUsername(users.username).flatMap{
          case Some(value) =>
            val encryptPassword = BCrypt.hashpw(users.password, BCrypt.gensalt)
            repo.updatePassword(encryptPassword,users.username).map{
              case true => Redirect(routes.UserController.profile()).withNewSession
              case false => Redirect(routes.UserController.updatePassword()).flashing("Error"->"Try Again")
            }
          case None =>
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
          errorForm => {
            Future.successful(Redirect(routes.UserController.updateProfile()).flashing("Error" -> "update form currently"))
          },
          profile => {
            repo.updateProfile(profile, user).flatMap {
              case true => Future.successful(Redirect(routes.UserController.profile()).flashing("Success"->"Your Profile is updated"))
              case false => Future.successful(Redirect(routes.UserController.profile()).withNewSession)
            }
          })
      case None => Future.successful(Redirect(routes.UserController.profile()).flashing("unauthorised" -> "You need to sign in first!").withNewSession)
    }
  }
  
}
