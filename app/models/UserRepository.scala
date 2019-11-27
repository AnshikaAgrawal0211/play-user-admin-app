package models

import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape

import scala.concurrent.{ExecutionContext, Future}

class UserRepository@Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)  {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private class UserTable(tag: Tag) extends Table[User](tag, "user") {

    def firstName: Rep[String] = column[String]("first name")
    def middleName: Rep[String] = column[String]("middle name")
    def lastName: Rep[String] = column[String]("last name")
    def username: Rep[String] = column[String]("username",O.PrimaryKey)
    def password: Rep[String] = column[String]("password")
    def reEnterPassword: Rep[String] = column[String]("re-enter password")
    def mobileNumber: Rep[String] = column[String]("mobile number")
    def gender: Rep[String] = column[String]("gender")
    def age: Rep[Int] = column[Int]("age")

    def * : ProvenShape[User] = (firstName,middleName,lastName,username,password,reEnterPassword,
      mobileNumber,gender,age) <> ((User.apply _).tupled, User.unapply)
  }

  private val user = TableQuery[UserTable]

  def create(firstName: String,middleName: String,
             lastName: String,userName: String,
             password: String,reEnterPassword: String,
             mobileNumber: String,gender: String,
             age: Int): Future[User] = db.run {
    (user.map(u => (u.firstName,u.middleName,u.lastName,u.username,u.password,u.reEnterPassword,
      u.mobileNumber,u.gender,u.age))
      returning user.map(_.username)
      into ((userDetails, username) => User(userDetails._1,userDetails._2,
      userDetails._3,username,userDetails._5,userDetails._6,userDetails._7,userDetails._8,userDetails._9))
      ) += (firstName,middleName,lastName,userName,password,reEnterPassword,mobileNumber,gender,age)
  }

  def list(): Future[Seq[User]] = db.run {
    user.result
  }
}
