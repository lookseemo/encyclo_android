package com.deedsit.android.bookworm.ui.adapter

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.deedsit.android.bookworm.R
import com.deedsit.android.bookworm.models.User
import kotlinx.android.synthetic.main.student_listing_row.view.*

/**
 * Created by Jack on 4/20/2018.
 */
class StudentListingRecycleViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var studentList = mutableListOf<User>()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        var view: View?
        var studentViewHolder: StudentViewHolder? = null

        parent?.let {
            var layoutInflater = LayoutInflater.from(it.context)
            view = layoutInflater.inflate(R.layout.student_listing_row, it, false)
            view?.let {
                studentViewHolder = StudentViewHolder(it)
            }
        }
        return studentViewHolder!!
    }

    override fun getItemCount(): Int {
        return studentList?.size ?: 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        // Set alternate colours
        if (position % 2 != 0) {
            holder?.let {
                it.itemView.studentMasterRowLayout.setBackgroundColor(ContextCompat.getColor(it
                        .itemView.context,
                        R.color.background_rating_color))
            }
        }

        studentList?.let {
            val user = it.get(position)
            (holder as StudentViewHolder).bindView(user)
        }
    }

    fun swapList(newStudentList: MutableList<User>) {
        if (newStudentList.size >= 0) {
            studentList.clear()
            studentList.addAll(newStudentList)
            this.notifyDataSetChanged()
        }
    }

    //----------------------------------- View Holder ----------------------------------------------

    inner class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView(user: User) {
            with(itemView) {
                studentName.text = user.firstName + " " + user.lastName
            }
        }
    }
}