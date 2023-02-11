package com.example.aankh.ui.fragments.application

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.aankh.R
import com.example.aankh.FaceVerification
import java.io.IOException
import java.util.*


class FirFragment : Fragment(){

    private val STORAGE_READ_PERMISSION_REQUEST_CODE = 130103
    private val STORAGE_WRITE_PERMISSION_REQUEST_CODE = 130104
    private val REQUEST_IMAGE_CAPTURE = 130105
    private val PICK_IMAGE = 1
    private var proofImageUri: Uri? = null
    private var fragView: View? = null
    private val handler = Handler()

    //Hooks
    private var proofImage: ImageView? = null

    //User inputs
    private var reportDate: String? = null
    private var reportTime: String? = null
    private var reportName: String? = null
    private var reportResidence: String? = null
    private var reportDescription: String? = null



    //Get - Set View
    private fun setView(view: View){
        fragView = view;
    }
    fun getView(a: Int): View? {
        return fragView;
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_fir, container, false);
        setView(view)  //So we can use anywhere here

        var openDatePicker = view.findViewById<TextView>(R.id.showDate);
        var openTimePicker = view.findViewById<TextView>(R.id.showTime);
        proofImage = view.findViewById<ImageView>(R.id.showImageProof);
        openDatePicker.setOnClickListener { openDatePicker(view);}
        openTimePicker.setOnClickListener { openTimePicker(view);}


        view.findViewById<EditText>(R.id.complainDescription).setSelection(0)


        var submitButton = view.findViewById<CardView>(R.id.submitButtonFragment)
        var progressBar = view.findViewById<ProgressBar>(R.id.progressBarFirFragment)
        submitButton.setOnClickListener {
            submitButton.visibility = View.INVISIBLE
            progressBar.visibility = View.VISIBLE

            reportDate = view.findViewById<TextView>(R.id.showDate).text.toString()
            reportTime = view.findViewById<TextView>(R.id.showTime).text.toString()
            reportName = view.findViewById<EditText>(R.id.complainantName).text.toString()
            reportResidence = view.findViewById<EditText>(R.id.complainantResidence).text.toString()
            reportDescription = view.findViewById<EditText>(R.id.complainDescription).text.toString()

            val mydata =
                "Date: $reportDate\nReport Time: $reportTime\nName: $reportName\nResidence: $reportResidence\nDescription$reportDescription";
            Log.d("Userdata", mydata)

            handler.postDelayed(runnable, 2000)
        }


        //Select image file
//        var imageInputBtn = view.findViewById<TextView>(R.id.openGallery);
//        imageInputBtn.setOnClickListener {
//            if (!checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
//                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_READ_PERMISSION_REQUEST_CODE)
//            } else {
//                if(!checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
//                    requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_WRITE_PERMISSION_REQUEST_CODE)
//                }
//                else{
//                    val intent = Intent(Intent.ACTION_PICK)
//                    intent.type = "image/*"
//                    startActivityForResult(intent, PICK_IMAGE)
//                }
//            }
//        }
        return view;
    }

    private val runnable = Runnable {
        var view = getView(1)
        if (view != null) {
            view.findViewById<CardView>(R.id.submitButtonFragment).visibility = View.VISIBLE
            view.findViewById<ProgressBar>(R.id.progressBarFirFragment).visibility = View.INVISIBLE
        };
        Toast.makeText(requireContext(), "Complaint registered successfully!", Toast.LENGTH_LONG).show()
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(runnable)
    }

    private fun openTimePicker(view: View) {
        var showTime = view.findViewById<TextView>(R.id.showTime)
        val calendar = Calendar.getInstance()
        var hour = calendar.get(Calendar.HOUR_OF_DAY)
        var minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(requireContext(), TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute1 ->
            hour = hourOfDay;
            minute = minute1;
            var date = "$hour:$minute";
            showTime.setText(date)
        }, hour, minute, DateFormat.is24HourFormat(requireContext()))

        timePickerDialog.show()
    }

    private fun openDatePicker(view: View) {
        val calendar = Calendar.getInstance()
        var year = calendar.get(Calendar.YEAR)
        var month = calendar.get(Calendar.MONTH)
        var day = calendar.get(Calendar.DAY_OF_MONTH)
        var showDate = view.findViewById<TextView>(R.id.showDate)
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->
                year = selectedYear
                month = selectedMonth
                day = selectedDay

                var date = "$day/$month/$year";
                showDate.setText(date)
//                Toast.makeText(activity, "Date: " + selectedDay + ":" + selectedMonth + ":" + selectedYear, Toast.LENGTH_SHORT).show()
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun checkPermission(permission: String): Boolean {
        val permissionCheck = ContextCompat.checkSelfPermission(requireContext(), permission)
        return permissionCheck == PackageManager.PERMISSION_GRANTED
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            data?.data?.let { imageUri ->
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, imageUri)
                    proofImage?.setImageBitmap(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

}




