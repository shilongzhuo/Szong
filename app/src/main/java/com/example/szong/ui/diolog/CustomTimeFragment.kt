package com.example.szong.ui.diolog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.szong.App
import com.example.szong.R
import com.example.szong.service.media.music.SimpleWorker
import com.example.szong.widget.pickerview.MyData
import com.example.szong.widget.pickerview.adapter.ScrollPickerAdapter
import com.example.szong.widget.pickerview.provider.MyViewProvider
import com.example.szong.widget.pickerview.view.ScrollPickerView
import java.util.ArrayList
import java.util.concurrent.TimeUnit

class CustomTimeFragment : DialogFragment() {
    private lateinit var hourView : ScrollPickerView
    private lateinit var minuteView : ScrollPickerView
    private lateinit var cancelView : TextView
    private lateinit var confirmView : TextView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_customtime,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
    }

    private fun initView(){
        hourView = view?.findViewById(R.id.hour)!!
        minuteView = view?.findViewById(R.id.minute)!!
        cancelView = view?.findViewById(R.id.cancelView)!!
        confirmView = view?.findViewById(R.id.confirmView)!!

        hourView?.layoutManager = LinearLayoutManager(context)
        minuteView?.layoutManager = LinearLayoutManager(context)

        hourView.apply {
            setHasFixedSize(true)
            setItemViewCacheSize(24)
            drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
        }
        minuteView.apply {
            setHasFixedSize(true)
            setItemViewCacheSize(60)
            drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
        }

        cancelView.setOnClickListener {
            dismiss()
        }
        confirmView.setOnClickListener {
            val hour = hourView.currentItem
            val minute = minuteView.currentItem
            App.musicController.value?.setCurrentCustom(hour * 60 + minute)

            val request = OneTimeWorkRequest.Builder(SimpleWorker::class.java).setInitialDelay((hour * 60 + minute).toLong(), TimeUnit.MINUTES)
                .addTag("lbccc").build()
            WorkManager.getInstance(requireContext()).enqueue(request)

            if (hour == 0){
                Toast.makeText(context, "?????????????????????$minute ??????????????? ", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context, "?????????????????????$hour ?????? $minute ??????????????? ", Toast.LENGTH_SHORT).show()
            }
            dismiss()
        }
    }
    private fun initData() {
        val hourList: MutableList<MyData> = ArrayList()
        val minuteList: MutableList<MyData> = ArrayList()

        for (i in 0..23) {
            val myData = MyData()
            myData.id = i
            myData.text = "   $i   "
            hourList.add(myData)
        }
        for (i in 0..59) {
            val myData = MyData()
            myData.id = i
            myData.text = "   $i   "
            minuteList.add(myData)
        }
        val hourBuilder: ScrollPickerAdapter.ScrollPickerAdapterBuilder<MyData> = ScrollPickerAdapter.ScrollPickerAdapterBuilder<MyData>(context)
            .setDataList(hourList)
            .selectedItemOffset(1)
            .visibleItemNumber(3)
            .setItemViewProvider(MyViewProvider())


        val mHourScrollPickerAdapter = hourBuilder.build()
        hourView.adapter = mHourScrollPickerAdapter

        val minuteBuilder: ScrollPickerAdapter.ScrollPickerAdapterBuilder<MyData> = ScrollPickerAdapter.ScrollPickerAdapterBuilder<MyData>(context)
            .setDataList(minuteList)
            .selectedItemOffset(1)
            .visibleItemNumber(3)
            .setItemViewProvider(MyViewProvider())


        val mMinuteScrollPickerAdapter = minuteBuilder.build()
        minuteView.adapter = mMinuteScrollPickerAdapter
    }
}