package com.deedsit.android.bookworm.ui.mainactivityfragments


import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.deedsit.android.bookworm.BookWormApplication
import com.deedsit.android.bookworm.R
import com.deedsit.android.bookworm.factory.AbstractPresenterFactory
import com.deedsit.android.bookworm.models.User
import com.deedsit.android.bookworm.ui.adapter.StudentListingRecycleViewAdapter
import com.deedsit.android.bookworm.ui.base.FragmentRecyclerView
import com.deedsit.android.bookworm.ui.interfacelisteners.OnFragmentStateChangeListener
import com.deedsit.android.bookworm.ui.interfacelisteners.OnRecyclerViewRowClickedListener
import com.deedsit.android.bookworm.ui.interfacelisteners.OnRowAddedListener
import kotlinx.android.synthetic.main.fragment_student_listing.*
import kotlinx.android.synthetic.main.fragment_student_listing.view.*
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [StudentListingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StudentListingFragment : Fragment(), FragmentRecyclerView {

    var masterFragment: RatingFragmentParent? = null
    var customRecyclerViewAdapter = StudentListingRecycleViewAdapter()
    var fragmentStateChangeListener: OnFragmentStateChangeListener? = null

    companion object {

        fun newInstance(): StudentListingFragment {
            return StudentListingFragment()
        }
        const val TAG = "STUDENT_LISTING_FRAGMENT"
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_student_listing, container, false)

        view.studentRecyclerList.layoutManager = LinearLayoutManager(fragmentActivity)
        view.studentRecyclerList.adapter = customRecyclerViewAdapter

        buildPresenter()
        return view
    }

    override fun onResume() {
        super.onResume()
        fragmentStateChangeListener?.onFragmentResume()
    }

    override fun onPause() {
        super.onPause()
        fragmentStateChangeListener?.onFragmentPause()
    }
    //----------------------------------- INTERFACE METHODS ----------------------------------------


    override fun getFragmentTag(): String {
        return TAG
    }

    override fun buildPresenter() {
        AbstractPresenterFactory.buildPresenterForFragment(this, (this.fragmentActivity
                .application as BookWormApplication).component, null)
    }

    override fun displayErrorMeesage(errorCode: Int) {
        //blank
    }

    override fun getFragmentActivity(): Activity {
        return masterFragment!!.fragmentActivity
    }

    override fun getParentFragmentManager(): FragmentManager {
        return masterFragment!!.parentFragmentManager
    }

    override fun displayData(adapterList: ArrayList<*>?) {
        updateStudentTotal(adapterList!!.size)
        val studentList = adapterList.toMutableList()
        customRecyclerViewAdapter.swapList((studentList as MutableList<User>))
    }

    override fun rowAdded(`object`: Any?) {
        //blank
    }

    override fun rowClicked(`object`: Any?) {
        //Blank
    }

    override fun clearDisplayData() {
        //blank
    }

    override fun setOnRowClickedListener(onRowClickedListener: OnRecyclerViewRowClickedListener?) {
        //Blank
    }

    override fun setOnFragmentStateChangeListener(onFragmentStateChangeListener: OnFragmentStateChangeListener?) {
       this.fragmentStateChangeListener = onFragmentStateChangeListener
    }

    override fun setOnRowAddedListener(onRowAddedListener: OnRowAddedListener?) {
        //blank
    }

    private fun updateStudentTotal(number: Int) {
        val studentText: String
        if (number == 1) {
            studentText = "$number student"
        } else {
            studentText = "$number students"
        }

        totalStudentTV.setText(studentText)
    }

}// Required empty public constructor
