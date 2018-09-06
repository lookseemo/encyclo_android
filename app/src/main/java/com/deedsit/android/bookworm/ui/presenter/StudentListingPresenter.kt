package com.deedsit.android.bookworm.ui.presenter

import com.deedsit.android.bookworm.arch.AppGraph
import com.deedsit.android.bookworm.services.DataRepo
import com.deedsit.android.bookworm.services.impl.DataRepoImpl
import com.deedsit.android.bookworm.ui.base.FragmentRecyclerView
import com.deedsit.android.bookworm.ui.base.FragmentView
import com.deedsit.android.bookworm.ui.interfacelisteners.OnFragmentStateChangeListener
import java.lang.Exception
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class StudentListingPresenter(appGraph: AppGraph, fragmentView: FragmentView): BasePresenter
(fragmentView, appGraph), RecyclerViewPresenter, OnFragmentStateChangeListener {

    @Inject
    lateinit var dataRepo: DataRepo

    private val fragmentView: FragmentRecyclerView = (fragmentView as FragmentRecyclerView)
    private var registeredServiceCallback = false

    companion object {
        @JvmStatic
        val TAG = "STUDENT_LISTING_PRESENTER"
    }

    init {
        appGraph.inject(this)
    }

    override fun getPresenterTag(): String {
        return TAG
    }

    override fun onDataSuccessfullyAdded(data: Any?) {
        if (data is ArrayList<*>) {
            fragmentView.displayData(data)
        }
    }

    override fun onDataSuccessfullyChanged(data: Any?) {
        if(data is ArrayList<*>){
            fragmentView.displayData(data)
        }
    }

    override fun registerCallBackListener() {

        if (!registeredServiceCallback) {
            dataRepo.registerCallBackEvent(DataRepoImpl.queryPath.StudentListing, this, fragmentView.fragmentTag)
            registeredServiceCallback = true
        }
        dataRepo.queryDatabase(DataRepoImpl.queryPath.StudentListing, null, fragmentView
                .fragmentTag, null)
    }

    override fun onFragmentResume() {
        registerCallBackListener()
    }

    override fun onFragmentPause() {
        if (registeredServiceCallback) {
            dataRepo.unregisterCallBack(fragmentView.fragmentTag)
            registeredServiceCallback = false
        }
    }

    //************************** METHODS DO NOT NEED TO BE IMPLEMENTED ************************

    override fun processDataChanged() {
        // blank
    }


    override fun onSuccess(data: Any?) {
        //Blank
    }

    override fun onRowAddedListener(data: Any?) {
        //Blank
    }

    override fun OnRecyclerViewRowClicked(`object`: Any?) {
        // Blank
    }

    override fun onFailure(e: Exception?) {
        //Blank
    }

    override fun onDataSuccessfullyRemoved(data: Any?) {
        //blank
    }
}