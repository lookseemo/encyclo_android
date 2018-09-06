package com.deedsit.android.bookworm

import com.deedsit.android.bookworm.Constants.*
import com.deedsit.android.bookworm.listeners.StudentListingListener
import com.deedsit.android.bookworm.listeners.UserEventListener
import com.deedsit.android.bookworm.models.User
import com.deedsit.android.bookworm.services.ServiceCallback
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener


class StudentListingManager(cloudDatabase: DatabaseReference) {

    private var serviceCallBackMap = hashMapOf<String, ServiceCallback<Any>>()
    private var studentMap = hashMapOf<String, User>()
    private var courseCode = courseSelected?.code
    private var studentListingListener = StudentListingListener { list -> onStudentListReceived(list) }

    private var userEventListener = UserEventListener { list -> onStudentDataReceived(list) }
    private var database = cloudDatabase

    fun initilizeDataReceiving() {
        if(userEventListener.callBackDataChanged == null)
            userEventListener.callBackDataChanged = {user -> onStudentDataChanged(user)}
        queryDataBase(false)
    }

    fun registerServiceCallBack(tag: String, callback: ServiceCallback<Any>) {
        if (!serviceCallBackMap.containsKey(tag)) {
            serviceCallBackMap.put(tag, callback)
        }
    }

    fun unregisterServiceCallback(tag: String) {
        serviceCallBackMap.remove(tag)
        if (serviceCallBackMap.size == 0) {
            studentListingListener.clearStudentList()
            userEventListener.clearUserList()
        }
    }

    private fun queryDataBase(userTable: Boolean) {
        if (userTable) {
            getStudentData()
        } else {
            getStudentList()
        }
    }

    private fun getStudentList() {
        // remove current one
        database.child(COURSE_KEY_REF).removeEventListener(studentListingListener as ChildEventListener)
        database.child(COURSE_KEY_REF).removeEventListener(studentListingListener as ValueEventListener)

        database.child(COURSE_KEY_REF).orderByChild("course_id").equalTo(courseCode)
                .addChildEventListener(studentListingListener)
        database.child(COURSE_KEY_REF).orderByChild("course_id").equalTo(courseCode)
                .addValueEventListener(studentListingListener)
    }

    private fun getStudentData() {
        database.child(USER_REF).removeEventListener(userEventListener as ChildEventListener)
        database.child(USER_REF).removeEventListener(userEventListener as ValueEventListener)

        database.child(USER_REF).addChildEventListener(userEventListener)
        database.child(USER_REF).addValueEventListener(userEventListener)
    }

    private fun onStudentListReceived(list: List<User>) {
        studentMap.clear()
        list.forEach {
            if (!studentMap.containsKey(it.key)) {
                studentMap.put(it.key, it)
            }
        }
        queryDataBase(true)
    }

    private fun onStudentDataReceived(list: List<User>) {
        list.forEach {
            if (studentMap.containsKey(it.key)) {
                val studentData = studentMap.get(it.key)
                studentData!!.firstName = it.firstName
                studentData.lastName = it.lastName
                studentData.username = it.username
                studentData.userType = it.userType
                studentData.email = it.email
            }
        }
        onStudentDataLoaded()
    }

    private fun onStudentDataLoaded() {
        //convert student map to list
        val studentList = studentMap.values.toMutableList()
        serviceCallBackMap.forEach {
            it.value.onDataSuccessfullyAdded(studentList)
        }
    }

    private fun onStudentDataChanged(user: User) {
        if(studentMap.contains(user.key)){
            val studentData = studentMap.get(user.key)
            studentData?.let {
                it.firstName = user.firstName
                it.lastName = user.lastName
                it.username = user.username
                it.userType = user.userType
                it.email = user.email
            }
            val studentList = studentMap.values.toMutableList()
            serviceCallBackMap.forEach{
                it.value.onDataSuccessfullyChanged(studentList)
            }
        }
    }

}