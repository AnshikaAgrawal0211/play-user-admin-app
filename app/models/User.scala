package models

case class User(id: Int,firstName: String,middleName: Option[String],
                lastName: String,userName: String,
                password: String,mobileNumber: String,
                gender: String, age: Int)
