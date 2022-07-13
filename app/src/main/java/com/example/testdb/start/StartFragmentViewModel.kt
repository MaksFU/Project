package com.example.testdb.start

import androidx.lifecycle.ViewModel
import com.example.testdb.database.firebase.FirebaseRepository
import com.example.testdb.utils.*

class StartFragmentViewModel: ViewModel() {
    fun authorize(type:String, onSuccess:()->Unit, onFail:(String)->Unit, onDel:()->Unit){
        when(type){
            TYPE_FIREBASE -> {
                REPOSITORY = FirebaseRepository()
                REPOSITORY.authorizeToDatabase(
                    {
                        onSuccess()
                    }, onFail, onDel)
            }
        }
    }

    fun register(type:String, onSuccess:()->Unit, onFail:(String)->Unit){
        when(type){
            TYPE_FIREBASE -> {
                REPOSITORY = FirebaseRepository()
                REPOSITORY.registerToDatabase(
                    {
                    onSuccess()
                    }, onFail)
            }
        }
    }

    fun logIn(id:String, onSuccess:()->Unit, onFail: () -> Unit){
        REPOSITORY = FirebaseRepository()
        REPOSITORY.getUserInfo(id, onSuccess, onFail)
    }
}