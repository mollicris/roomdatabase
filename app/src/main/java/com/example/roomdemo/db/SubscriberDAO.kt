package com.example.roomdemo.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface SubscriberDAO {

    @Insert
    suspend fun insertSubscriber(subscriber: Subscriber) : Long

    @Update
    suspend fun updateSubscriber(subscriber: Subscriber) :Int

    @Delete
    suspend fun deleteSubscriber(subscriber: Subscriber) : Int

    @Query("DELETE FROM SUBSCRIBER_DATA_TABLE")
    suspend fun deleteAll() : Int

    @Query("SELECT * FROM SUBSCRIBER_DATA_TABLE")
    fun getAllSubscribers():LiveData<List<Subscriber>>

}