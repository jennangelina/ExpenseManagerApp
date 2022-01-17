package com.example.uas_c14190099

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [reportFr.newInstance] factory method to
 * create an instance of this fragment.
 */
class reportFr : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var _rvReport: RecyclerView
    private var arReport = arrayListOf<transaction>()
    lateinit var db: FirebaseFirestore
    lateinit var _mbReportDate: MaterialButton
    var day = 0
    var month = 0
    var year = 0

    var savedDay = 0
    var savedMonth = 0
    var savedYear = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()
        _rvReport = view.findViewById(R.id.rvReport)

        val _showResultBtn = view.findViewById<Button>(R.id.showResultBtn)
        val _clTotalIncome = view.findViewById<ConstraintLayout>(R.id.clTotalIncome)
        val _clTotalExpense = view.findViewById<ConstraintLayout>(R.id.clTotalExpense)
        val _tvTotalIncome = view.findViewById<TextView>(R.id.tvTotalIncome)
        val _tvTotalExpense = view.findViewById<TextView>(R.id.tvTotalExpense)
        _mbReportDate = view.findViewById(R.id.mbReportDate)

        _clTotalIncome.visibility = View.INVISIBLE
        _clTotalExpense.visibility = View.INVISIBLE

        var calendar = Calendar.getInstance()
        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH)
        day = calendar.get(Calendar.DAY_OF_MONTH)

        savedDay = day
        savedMonth = month+1
        savedYear = year

        var monthStr = "-"
        when(savedMonth) {
            1 -> monthStr = "Jan"
            2 -> monthStr = "Feb"
            3 -> monthStr = "Mar"
            4 -> monthStr = "Apr"
            5 -> monthStr = "May"
            6 -> monthStr = "Jun"
            7 -> monthStr = "Jul"
            8 -> monthStr = "Aug"
            9 -> monthStr = "Sep"
            10 -> monthStr = "Oct"
            11 -> monthStr = "Nov"
            12 -> monthStr = "Dec"
        }

        _mbReportDate.setText("$savedDay $monthStr $savedYear")

        _mbReportDate.setOnClickListener {
            var datePickerDialog = DatePickerDialog(requireActivity(),
                DatePickerDialog.OnDateSetListener{ view, yearD, monthD, dayD ->
                    val months = monthD+1
                    var monthName = "-"
                    when(months) {
                        1 -> monthName = "Jan"
                        2 -> monthName = "Feb"
                        3 -> monthName = "Mar"
                        4 -> monthName = "Apr"
                        5 -> monthName = "May"
                        6 -> monthName = "Jun"
                        7 -> monthName = "Jul"
                        8 -> monthName = "Aug"
                        9 -> monthName = "Sep"
                        10 -> monthName = "Oct"
                        11 -> monthName = "Nov"
                        12 -> monthName = "Dec"
                    }
                    _mbReportDate.setText("$dayD $monthName $yearD")
                    savedDay = dayD
                    savedMonth = months
                    savedYear = yearD
                }, year, month, day)
            datePickerDialog.show()
        }

        _showResultBtn.setOnClickListener {
            _rvReport.layoutManager = LinearLayoutManager(activity)
            val adapterT = adapterTransaction(arReport)
            _rvReport.adapter = adapterT

            db.collection("transaction_table")
                .whereEqualTo("day", savedDay.toString())
                .whereEqualTo("month", savedMonth.toString())
                .whereEqualTo("year", savedYear.toString())
                .get()
                .addOnSuccessListener { result ->
                    arReport.clear()
                    for(document in result) {
                        val data = transaction(document.id,
                            document.data.get("type").toString(),
                            document.data.get("amount").toString(),
                            document.data.get("category").toString(),
                            document.data.get("note").toString(),
                            document.data.get("day").toString(),
                            document.data.get("month").toString(),
                            document.data.get("year").toString())
                        arReport.add(data)
                    }
                    adapterT.notifyDataSetChanged()

                    if(arReport.count() > 0) {
                        var tempTotalIncome = 0
                        var tempTotalExpense = 0
                        for(item in arReport) {
                            if(item.type == "income") {
                                tempTotalIncome = tempTotalIncome + item.amount.toInt()
                            }
                            else if(item.type == "expense") {
                                tempTotalExpense = tempTotalExpense + item.amount.toInt()
                            }
                        }
                        _clTotalIncome.visibility = View.VISIBLE
                        _clTotalExpense.visibility = View.VISIBLE
                        _tvTotalIncome.setText("Rp${tempTotalIncome}")
                        _tvTotalExpense.setText("Rp${tempTotalExpense}")
                    }
                }
                .addOnFailureListener{
                    Log.d("Firebase", it.message.toString())
                }

            adapterT.setOnItemClickCallbcak(object: adapterTransaction.OnItemClickCallback {
                override fun onItemClicked(data: transaction) {
                    Intent(activity, detailTransactionAct::class.java).also {
                        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        it.putExtra("transactionData", data)
                        startActivity(it)
                    }
                }
            })
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment reportFr.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            reportFr().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}