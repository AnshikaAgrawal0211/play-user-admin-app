package form

import play.api.data.Form
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.data.validation._
import play.api.data.validation.Constraints._

case class SignUpData(firstName: String,middleName: String,lastName: String,username: String,password:String,reEnterPassword: String,mobileNo: String,gender: String, age: Int)
case class SignInData(username: String, password: String)

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

  val mobileConstraint: Constraint[String] = Constraint("constraints.mobile")({
    num =>
      val errors = ("""[^0-9]""").r.findFirstIn(num) match {
        case None => Nil
        case Some(_) => Seq(ValidationError("enter integer value"))
      }
      if (errors.isEmpty) {
        Valid
      } else {
        Invalid(errors)
      }
  })

  val signUpForm: Form[SignUpData] = Form {
    mapping(
      "First Name" -> nonEmptyText.verifying(nameConstraint),
      "Middle Name" -> text.verifying(nameConstraint),
      "Last Name" ->  nonEmptyText.verifying(nameConstraint),
      "Username"  -> nonEmptyText,
      "Password" -> nonEmptyText,
      "Re-enter Password" -> nonEmptyText,
      "Mobile No." -> nonEmptyText.verifying(mobileConstraint,minLength(10),maxLength(10)),
      "Gender" -> nonEmptyText,
      "Age" -> number.verifying(min(18), max(75))
    )(SignUpData.apply )(SignUpData.unapply).verifying("Password do not match",data => data.password == data.reEnterPassword)
  }

  val signInForm: Form[SignInData] = Form {
    mapping(
      "Username" -> nonEmptyText,
      "password" -> nonEmptyText
    )(SignInData.apply )(SignInData.unapply)
  }
}
