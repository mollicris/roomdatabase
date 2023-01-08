package com.example.roomdemo

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Delete
import com.example.roomdemo.db.Subscriber
import com.example.roomdemo.db.SubscriberRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext



class SubscriberViewModel(private  val repository: SubscriberRepository) : ViewModel(){

    val subscribers = repository.subscribers
    private var isUpdateOrDelete : Boolean = false
    private lateinit var subscriberToUpdateOrDelete: Subscriber

    val inputName = MutableLiveData<String>()
    val inputMail = MutableLiveData<String>()

    val saveOrUpdateButtonText = MutableLiveData<String>()
    val clearAllOrDeleteButtonText = MutableLiveData<String>()

    private val statusMessage = MutableLiveData<Event<String>>()

    val message : LiveData<Event<String>>

    get() = statusMessage


    init {
        saveOrUpdateButtonText.value = "Save"
        clearAllOrDeleteButtonText.value = "ClearAll"
    }

    fun saveOrUpdate(){
        if (inputName.value == null){
            statusMessage.value = Event("Please enter Subscriber's name")
        } else if (inputMail.value == null){
            statusMessage.value = Event("Please enter Subscriber's mail")
        } else if(!Patterns.EMAIL_ADDRESS.matcher(inputMail.value).matches()){
            statusMessage.value = Event("Please enter a correct mail")
        } else{
            if (isUpdateOrDelete){
                subscriberToUpdateOrDelete.name = inputName.value!!
                subscriberToUpdateOrDelete.email = inputMail.value!!
                update(subscriberToUpdateOrDelete)
            }
            else
            {
                val name = inputName.value!!
                val email = inputMail.value!!
                insert(Subscriber(0, name, email))
                inputName.value = ""
                inputMail.value = ""
            }
        }
    }

    fun clearAllOrDelete(){
        if (isUpdateOrDelete){
            delete(subscriberToUpdateOrDelete)
        } else {
            clearAll()
        }
    }

    private fun insert(subscriber: Subscriber) = viewModelScope.launch(Dispatchers.IO) {
            val newRowId  = repository.insert(subscriber)
            withContext(Dispatchers.Main){
                if (newRowId > -1) {
                    statusMessage.value = Event("Subscriber Insert Success $newRowId")
                }else {
                    statusMessage.value = Event("Error Insert")
                }
            }
        }


    private fun update(subscriber: Subscriber) = viewModelScope.launch(Dispatchers.IO) {
        val numberOfRows = repository.update(subscriber)
        withContext(Dispatchers.Main){
            if (numberOfRows > 0) {
                inputName.value = ""
                inputMail.value = ""
                isUpdateOrDelete = false
                saveOrUpdateButtonText.value = "Save"
                clearAllOrDeleteButtonText.value = "Clear All"
                statusMessage.value = Event("Subscriber Updated Successfully $numberOfRows")
            } else {
                statusMessage.value = Event("Error Update")
            }
        }
    }

    private fun delete(subscriber: Subscriber) = viewModelScope.launch(Dispatchers.IO) {
        val numberOdRowsDeleted = repository.delete(subscriber)
        withContext(Dispatchers.Main){
            if (numberOdRowsDeleted > 0) {
                inputName.value = ""
                inputMail.value = ""
                isUpdateOrDelete = false
                saveOrUpdateButtonText.value = "Save"
                clearAllOrDeleteButtonText.value = "Clear All"
                statusMessage.value = Event("$numberOdRowsDeleted Subscriber Deleted Successfully")
            }else{
                statusMessage.value = Event("Error Deleted")
            }
        }
    }

    private fun clearAll() = viewModelScope.launch(Dispatchers.IO) {
        val numberOfRowsDeleted = repository.deleteAll()
        withContext(Dispatchers.Main){
            if (numberOfRowsDeleted > 0) {
                statusMessage.value = Event("$numberOfRowsDeleted All Subscriber Deleted Successfully")
            }else{
                statusMessage.value = Event("Error All Subscriber Deleted Successfully")
            }
        }
    }

    fun initUpdateAndDelete(subscriber: Subscriber){
        inputName.value = subscriber.name
        inputMail.value = subscriber.email
        isUpdateOrDelete= true
        subscriberToUpdateOrDelete = subscriber
        saveOrUpdateButtonText.value = "Update"
        clearAllOrDeleteButtonText.value = "Delete"
    }

}