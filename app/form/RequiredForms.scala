package form

import play.api.data.Form
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.data.validation._
import play.api.data.validation.Constraints._

case class SignUpData(firstName: String,middleName: Option[String],
                      lastName: String,username: String,
                      password:String,reEnterPassword: String,
                      mobileNo: String,gender: String, age: Int)

case class SignInData(username: String, password: String)

case class UpdatePasswordData(username : String,password : String,
                              confirmPassword : String)

case class ProfileData(firstName: String,middleName: Option[String],
                       lastName: String,mobileNo: String,
                       gender: String, age: Int)

case class AssignmentData(title: String,description:String)

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
      val errors = ("""[^A-Z a-z]""").r.findFirstIn(name.fold("Empty")(identity)) match {
        case None => Nil
        case Some(_) => Seq(ValidationError("name should not contain no. or special character"))
      }
      if (errors.isEmpty) {
        Valid
      } else {
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
      "First Name" -> nonEmptyText.verifying(nameConstraint),
      "Middle Name" -> optional(text).verifying(middleNameConstraint),
      "Last Name" ->  nonEmptyText.verifying(nameConstraint),
      "Username"  -> nonEmptyText,
      "Password" -> nonEmptyText,
      "Re-enter Password" -> nonEmptyText,
      "Mobile No." -> nonEmptyText.verifying(mobileConstraint),
      "Gender" -> nonEmptyText,
      "Age" -> number.verifying(min(18), max(75))
    )(SignUpData.apply )(SignUpData.unapply _)
      .verifying("Password do not match",data => data.password.equals(data.reEnterPassword))
  }

  val signInForm: Form[SignInData] = Form {
    mapping(
      "Username" -> nonEmptyText,
      "password" -> nonEmptyText
    )(SignInData.apply )(SignInData.unapply)
  }

  val updatePasswordForm: Form[UpdatePasswordData]= Form {
    mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText,
      "confirm Password" -> nonEmptyText
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
