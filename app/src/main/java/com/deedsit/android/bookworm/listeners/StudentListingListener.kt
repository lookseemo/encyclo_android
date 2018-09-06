package com.deedsit.android.bookworm.listeners

import com.deedsit.android.bookworm.models.User
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

/**
 * Created by Jack on 4/20/2018.
 */
class StudentListingListener(onStudentReceivedCallBack: (List<User>) -> Unit):
        ChildEventListener, ValueEventListener {
    private var studentList = mutableListOf<User>()
    private val callBack    = onStudentReceivedCallBack

    fun clearStudentList(){
        studentList.clear()
    }

    override fun onCancelled(p0: DatabaseError?) {
    }

    override fun onChildMoved(dataSnapshot: DataSnapshot?, p1: String?) {
    }

    override fun onChildChanged(dataSnapshot: DataSnapshot?, p1: String?) {
        // this is when a student joined/ remove the course
        addToStudentList(dataSnapshot)
    }

    override fun onChildAdded(dataSnapshot: DataSnapshot?, p1: String?) {
        //expect this to call only one
        addToStudentList(dataSnapshot)
    }

    override fun onChildRemoved(dataSnapshot: DataSnapshot?) {

    }

    override fun onDataChange(dataSnapshot: DataSnapshot?) {
        callBack(studentList)
    }

    private fun addToStudentList(dataSnapshot: DataSnapshot?){
        dataSnapshot?.let {
            if(it.child("users").hasChildren()){
                it.child("users").children.forEach{
                    var userExisted = false
                    val currDataSnapshot = it
                    studentList.forEach{
                        if(it.username.equals(currDataSnapshot.key,true))
                            userExisted = true
                    }

                    if(!userExisted) {
                        val user = User()
                        user.key = it.key
                        studentList.add(user)
                    }
                }
            }
        }
    }
}