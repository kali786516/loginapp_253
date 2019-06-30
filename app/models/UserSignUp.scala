package models


case class UserSignUp(
                       username: String,
                       password: String,
                       email: String,
                       profile: UserProfile
                     )

case class UserProfile(
                        country: String,
                        address: Option[String],
                        age: Option[Int]
                      )

object UserSignUp {





}
