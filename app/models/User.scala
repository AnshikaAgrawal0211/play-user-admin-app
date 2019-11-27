package models

case class User(firstName: String,middleName: String,
                lastName: String,userName: String,
                password: String,reEnterPassword: String,
                mobileNumber: String,gender: String,
                age: Int)
