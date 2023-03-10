package com.example.roomdemo.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [Subscriber::class],version = 1)
abstract class SubscriberDataBase : RoomDatabase() {

    abstract val subscriberDAO:SubscriberDAO

    companion object{

        @Volatile
        private var INSTANCE : SubscriberDataBase? = null
        fun getInstance(context: Context) : SubscriberDataBase{
            synchronized(this){
                var instance : SubscriberDataBase? = INSTANCE
                if(instance==null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SubscriberDataBase::class.java,
                        "susbcriber_data_database"
                    ).build()
                    INSTANCE = instance
                }
            return instance
            }
        }

    }
}