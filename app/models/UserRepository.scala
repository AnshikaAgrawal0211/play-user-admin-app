package models

import form.ProfileData
import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape
import org.mindrot.jbcrypt.BCrypt

import scala.concurrent.{ExecutionContext, Future}

class UserRepository@Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)  {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private class UserTable(tag: Tag) extends Table[User](tag, "user") {

    def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def firstName: Rep[String] = column[String]("first name")
    def middleName: Rep[Option[String]] = column[Option[String]]("middle name")
    def lastName: Rep[String] = column[String]("last name")
    def username: Rep[String] = column[String]("username")
    def password: Rep[String] = column[String]("password")
    def mobileNumber: Rep[String] = column[String]("mobile number")
    def gender: Rep[String] = column[String]("gender")
    def age: Rep[Int] = column[Int]("age")

    def * : ProvenShape[User] = (id,firstName,middleName,lastName,username,password,
      mobileNumber,gender,age) <> (User.tupled, User.unapply)
  }

  private val user = TableQuery[UserTable]

  def updatePassword(username: String,password: String): Future[Boolean] ={
    db.run(user.filter(_.username===username).map(_.password).update(password)).map(_ > 0)
  }

  def checkSignInValue(username:String, password : String): Future[Boolean] ={
    val users: Future[List[User]] = db.run(user.filter(_.username === username).to[List].result)
    users.map { user =>
      if (user.isEmpty) {
        false
      }
      else if (BCrypt.checkpw(password, user.head.password)) {
        true
      }
      else {
        false
      } }
  }

  def findByUsername(userName: String): Future[Option[String]] = {
    db.run(user.filter(_.username === userName).map(_.username).result.headOption)
  }

  def store(userData: User): Future[Boolean] = {
    db.run(user += userData).map(_ > 0)
  }

  def updateProfile(profileData: ProfileData,username:String): Future[Boolean] ={
    db.run(user.filter(_.username===username).map(info=> (info.firstName,info.middleName,info.lastName,
      info.mobileNumber,info.age,info.gender)).update(profileData.firstName,profileData.middleName,
      profileData.lastName,profileData.mobileNo,profileData.age,profileData.gender)).map(_ > 0)
  }

  def retrieve(username: String): Future[List[User]] = {
    db.run(user.filter(_.username === username).to[List].result)
  }

}
