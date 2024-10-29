package com.hopeinyang.zeroknowledge.data.dto

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.toObject

data class UserInfo(
    val firstName:String ="",
    val lastName:String ="",
    val phoneNumber:String = "",
    val email:String ="",
    val userId:String ="",
    val isEmailVerified:Boolean = false,
    val secondAuth:String = "Disabled",
    val department:String ="",
    val specialty:String ="",
    val imageUrl:String ="",
    val adminUser:Boolean = false,
    val accessLevel:Int = 0
)
{
    /*companion object{
        fun toObject(doc: DocumentSnapshot):UserInfo?{
            val item = doc.toObject<UserInfo>()
            //item!!. = doc.id
            return  item

        }
    }*/
}