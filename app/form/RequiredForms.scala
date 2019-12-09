package form

import play.api.data.Form
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.data.validation._
import play.api.data.validation.Constraints._

case class SignUpData(firstName: String,middleName: Option[String],
                      lastName: String,username: String,
                      password:String,confirmPassword: String,
                      mobileNumber: String,gender: String, age: Int)

case class SignInData(userName: String, password: String)

case class UpdatePasswordData(userName : String,password : String,
                              confirmPassword : String)

case class ProfileData(firstName: String,middleName: Option[String],
                       lastName: String,mobileNumber: String,
                       gender: String, age: Int)

case class AssignmentData(title: String,description:String)

object ProfileData{
  def apply(list: List[(String, Option[String], String, String, String,Int)]): ProfileData = {
    val firstName = list.head._1
    val middleName = list.head._2
    val lastName = list.head._3
    val mobileNumber = list.head._4
    val gender = list.head._5
    val age = list.head._6

    new ProfileData(firstName, middleName, lastName,mobileNumber, gender, age)
  }
}


class FormMappingData {

  val nameConstraint: Constraint[String] = Constraint("constraints.name")({
    name =>
      val errors = ("""[^A-Z a-z]""").r.findFirstIn(name) match {
        case None => Nil
        case Some(_) => Seq(ValidationError("name should not contain no. or special character"))
      }
      if (errors.isEmpty) {
        Valid
      } else {
        Invalid(errors)
      }
  })

  val middleNameConstraint: Constraint[Option[String]] = Constraint("constraints.name")({
    name =>
      if (name.isEmpty)
        Valid
      else {
        val errors = ("""[^A-Z a-z]""").r.findFirstIn(name.fold("Empty")(identity)) match {
          case None    => Nil
          case Some(_) => Seq(ValidationError("name should not contain no. or special character"))}
        if (errors.isEmpty)
          Valid
        else
          Invalid(errors)
      }
  })

  val mobileConstraint: Constraint[String] = Constraint("constraints.mobile")({
    num =>
      val errors = ("""[^0-9]""").r.findFirstIn(num) match {
        case None => Nil
        case Some(_) => Seq(ValidationError("enter integer value"))
      }
      if (errors.isEmpty && num.length == 10) {
        Valid
      } else {
        Invalid(errors)
      }
  })

  val signUpForm: Form[SignUpData] = Form {
    mapping(
      "firstName" -> nonEmptyText.verifying(nameConstraint),
      "middleName" -> optional(text).verifying(middleNameConstraint),
      "lastName" ->  nonEmptyText.verifying(nameConstraint),
      "userName"  -> nonEmptyText,
      "password" -> nonEmptyText,
      "confirmPassword" -> nonEmptyText,
      "mobileNumber" -> nonEmptyText.verifying(mobileConstraint),
      "gender" -> nonEmptyText,
      "age" -> number.verifying(min(18), max(75))
    )(SignUpData.apply )(SignUpData.unapply _)
      .verifying("Password do not match",data => data.password.equals(data.confirmPassword))
  }

  val signInForm: Form[SignInData] = Form {
    mapping(
      "userName" -> nonEmptyText,
      "password" -> nonEmptyText
    )(SignInData.apply )(SignInData.unapply)
  }

  val updatePasswordForm: Form[UpdatePasswordData]= Form {
    mapping(
      "userName" -> nonEmptyText,
      "password" -> nonEmptyText,
      "confirmPassword" -> nonEmptyText
    )(UpdatePasswordData.apply)(UpdatePasswordData.unapply)
      .verifying("Password do not match",data => data.password.equals(data.confirmPassword))
  }

  val profileForm: Form[ProfileData] = Form {
    mapping(
      "firstName" -> text.verifying("Please enter first name", firstName => !firstName.isEmpty),
      "middleName" -> optional(text),
      "lastName" -> text.verifying("Please enter last name", lastName => !lastName.isEmpty),
      "mobileNumber" -> nonEmptyText.verifying(mobileConstraint),
      "gender" -> nonEmptyText,
      "age" -> number.verifying(min(18), max( 75)),
    )(ProfileData.apply)(ProfileData.unapply _)
  }

  val AssignmentForm: Form[AssignmentData] = Form{
    mapping(
    "title" -> nonEmptyText,
    "description" -> nonEmptyText
  )(AssignmentData.apply)(AssignmentData.unapply)
  }

}
