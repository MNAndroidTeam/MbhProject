package com.medianet.mbhproject

import com.medianet.tools.form.*
import java.io.Serializable

data class User(
   @Length(min = 3)  var name : String="",
   @Email var email : String ="",
   var image : String ="https://scontent.ftun1-2.fna.fbcdn.net/v/t1.6435-9/119485246_1442988205895134_4990842114921899078_n.jpg?_nc_cat=101&ccb=1-3&_nc_sid=09cbfe&_nc_ohc=6RJ2KE5KxrEAX_1IZni&_nc_ht=scontent.ftun1-2.fna&oh=5c4552e76d2508425a1558cbe0f48289&oe=60999C26",
   @Password var password: String = "",
   @Phone var phone : String = "",
   @NotEmpty var gender : Int = -1,
   var age : String? = "",
   @EmbendedError var date : BetweenDates? = null,
   @EmbendedError var address: Address? = null,
   var daysOfWeek: ArrayList<String> = ArrayList()
) : Serializable


data class Address(
   var id: String = "",
   @Length(min = 1 ,max = 5) var name: String = ""
): Serializable

data class BetweenDates(
   var startDate : String = "",
   var endDate : String = ""
): Serializable

data class AgeInterval(
   val id: String = "",
   val value : String = ""
){
   override fun toString(): String {
      return value
   }
}