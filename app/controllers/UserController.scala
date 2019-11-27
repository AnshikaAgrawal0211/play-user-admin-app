package controllers

import javax.inject._

import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.i18n._
import play.api.libs.json.Json
import play.api.mvc._
import form.FormMappingData

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
        Future.successful(Ok(views.html.signUp(errorForm)))
      },
      users =>{
        repo.create(users.firstName,users.middleName,users.lastName,users.username,users.password,users.reEnterPassword,
          users.mobileNo,users.gender,users.age).map{ _ =>
          Redirect(routes.UserController.welcome()).flashing("success" -> "user.created")
        }
      }
    )
  }
}
