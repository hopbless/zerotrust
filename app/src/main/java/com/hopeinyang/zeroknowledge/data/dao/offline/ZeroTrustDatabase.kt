package com.hopeinyang.zeroknowledge.data.dao.offline

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hopeinyang.zeroknowledge.data.dto.SearchHistory

@Database(
    entities = [SearchHistory::class],
    version = 1,
    exportSchema = false
)
abstract class ZeroTrustDatabase: RoomDatabase() {
    abstract val zeroTrustDAO:ZeroTrustDAO

    companion object{
        @Volatile
        private var Instance:ZeroTrustDatabase? = null

        fun getInstance(context: Context):ZeroTrustDatabase{

            return Instance ?: synchronized(this){
                Room.databaseBuilder(
                    context, ZeroTrustDatabase::class.java, "zero_trust_database" )
                    .fallbackToDestructiveMigration()
                    .build().also { Instance= it }
            }
        }
    }
}