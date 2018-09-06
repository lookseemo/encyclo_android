package com.deedsit.android.bookworm.listeners

import com.deedsit.android.bookworm.models.User
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

/**
 * Created by Jack on 4/20/2018.
 */
class UserEventListener(onUserDataReceived: (List<User>) -> Unit): ChildEventListener,
        ValueEventListener {

    enum class DataState{
        dsNone,
        dsAdded,
        dsChanged
    }

    private var indexDataChanged = -1
    private var userList = mutableListOf<User>()
    private val callBackInsert = onUserDataReceived
    var callBackDataChanged: ((User) -> Unit)? = null
    private var dataState = DataState.dsNone

    fun clearUserList(){
        userList.clear()
    }
    override fun onCancelled(p0: DatabaseError?) {
    }

    override fun onChildMoved(dataSnapshot: DataSnapshot?, p1: String?) {
    }

    override fun onChildChanged(dataSnapshot: DataSnapshot?, p1: String?) {
        //student details have changed
        dataSnapshot?.let {
            val userData = it.getValue(User::class.java)
            if(userData != null){
                userList.forEachIndexed{ index, user ->
                    if(user.username.equals(userData.username,false )){
                        with(user){
                            email = userData.email
                            firstName = userData.firstName
                            lastName = userData.lastName
                            userType = userData.userType
                        }
                        dataState = DataState.dsChanged
                        indexDataChanged = index
                        return@forEachIndexed
                    }
                }
            }
        }
    }

    override fun onChildAdded(dataSnapshot: DataSnapshot?, p1: String?) {
        dataSnapshot?.let {
            val user = it.getValue(User::class.java)
            if (user != null) {
                user.key = it.key
                userList.add(user)
                if(dataState != DataState.dsAdded)
                    dataState = DataState.dsAdded
            }
        }
    }

    override fun onChildRemoved(dataSnapshot: DataSnapshot?) {
    }

    override fun onDataChange(dataSnapshot: DataSnapshot?) {
        if(dataState == DataState.dsAdded)
            callBackInsert(userList)
        else if(dataState == DataState.dsChanged && indexDataChanged != -1)
            callBackDataChanged!!(userList.get(indexDataChanged))

        indexDataChanged = -1
        dataState = DataState.dsNone
    }
}