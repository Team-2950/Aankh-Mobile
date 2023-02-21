package com.example.aankh.ui.fragments.application

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.SharedPreferences
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
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.aankh.R
import com.example.aankh.dataModels.ReportComplaintDataModel
import com.example.aankh.databinding.FragmentFirBinding
import com.example.aankh.viewModels.uiViewModels.ReportComplaintViewModel
import java.io.IOException
import java.util.*


class FirFragment : Fragment() {


    private val viewModel: ReportComplaintViewModel by activityViewModels()
    private lateinit var preferences: SharedPreferences
    private lateinit var id: String
    private var _binding: FragmentFirBinding? = null
    private val binding get() = _binding!!


    //User inputs
    private var date: String? = null
    private var time: String? = null
    private var officerName: String? = null
    private var complaintantName: String? = null
    private var description: String? = null


    private var fragView: View? = null

    //Get - Set View
    private fun setView(view: View) {
        fragView = view;
    }

    fun getView(a: Int): View? {
        return fragView;
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFirBinding.inflate(inflater, container, false)
        var view = inflater.inflate(R.layout.fragment_fir, container, false);
        setView(view)  //So we can use anywhere here

        var openDatePicker = view.findViewById<TextView>(R.id.showDate);
        var openTimePicker = view.findViewById<TextView>(R.id.showTime);
        openDatePicker.setOnClickListener { openDatePicker(view); }
        openTimePicker.setOnClickListener { openTimePicker(view); }


        view.findViewById<EditText>(R.id.complainDescription).setSelection(0)


        var submitButton = view.findViewById<CardView>(R.id.submitButtonFragment)
        var progressBar = view.findViewById<ProgressBar>(R.id.progressBarFirFragment)

        try {
            preferences =
                activity?.getSharedPreferences("PREFERENCE", AppCompatActivity.MODE_PRIVATE)!!
            id = preferences?.getString("id", "").toString()
        } catch (e: Exception) {

        }

        submitButton.visibility = View.VISIBLE
        progressBar.visibility = View.GONE

        submitButton.setOnClickListener {
            submitButton.visibility = View.INVISIBLE
            progressBar.visibility = View.VISIBLE

//            val date = binding.showDate.text.trim().toString()
//            val time = binding.showTime.text.trim().toString()
//            val officerName = binding.officerName.text.trim().toString()
//            val complaintantName = binding.complainantResidence.text.trim().toString()
//            val description = binding.complainDescription.text.trim().toString()

            date = view.findViewById<TextView>(R.id.showDate).text.toString()
            time = view.findViewById<TextView>(R.id.showTime).text.toString()
            officerName = view.findViewById<EditText>(R.id.officerName).text.toString()
            complaintantName =
                view.findViewById<EditText>(R.id.complainantResidence).text.toString()
            description =
                view.findViewById<EditText>(R.id.complainDescription).text.toString()


            viewModel.getReportStatus().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                Toast.makeText(requireContext(), "Report has been submitted!!", Toast.LENGTH_LONG)
                    .show()
//                binding.submitButtonFragment.visibility = View.VISIBLE
//                binding.progressBarFirFragment.visibility = View.GONE
                submitButton.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
            })



            if (date!!.isEmpty() || time!!.isEmpty() || officerName!!.isEmpty() || complaintantName!!.isEmpty() || description!!.isEmpty()) {
                Toast.makeText(requireContext(), "Each filed is mandatory", Toast.LENGTH_SHORT)
                    .show()
//                binding.submitButtonFragment.visibility = View.VISIBLE
//                binding.progressBarFirFragment.visibility = View.GONE
                submitButton.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
                val mydata =
                    "Date: $date\nReport Time: $time\nName: $officerName\nResidence: $complaintantName\nDescription$description";
                Log.d("Userdata", mydata)
            } else {
                try {
//
//                    binding.submitButtonFragment.visibility = View.INVISIBLE
//                    binding.progressBarFirFragment.visibility = View.VISIBLE
                    submitButton.visibility = View.INVISIBLE
                    progressBar.visibility = View.VISIBLE
                    viewModel.reportComplaint(
                        id,
                        ReportComplaintDataModel(
                            date!!,
                            time!!,
                            officerName!!,
                            complaintantName!!,
                            description!!
                        )
                    )
                    val mydata =
                        "Date: $date\nReport Time: $time\nName: $officerName\nResidence: $complaintantName\nDescription$description";
                    Log.d("Userdata", mydata)
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Got NUll ERROR!!", Toast.LENGTH_SHORT)
                        .show()

                    findNavController().navigate(R.id.action_firFragment_to_mainDashboard)
                }

            }

        }


        return view;
    }


    private fun openTimePicker(view: View) {
        var showTime = view.findViewById<TextView>(R.id.showTime)
        val calendar = Calendar.getInstance()
        var hour = calendar.get(Calendar.HOUR_OF_DAY)
        var minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute1 ->
                hour = hourOfDay;
                minute = minute1;
                var date = "$hour:$minute";
                showTime.setText(date)
            },
            hour,
            minute,
            DateFormat.is24HourFormat(requireContext())
        )

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


}