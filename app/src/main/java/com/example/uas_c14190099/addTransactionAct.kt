package com.example.uas_c14190099

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class addTransactionAct : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    var day = 0
    var month = 0
    var year = 0

    var savedDay = 0
    var savedMonth = 0
    var savedYear = 0

    lateinit var _radioGroup: RadioGroup
    lateinit var _tvTrasactionDate: TextView
    var _type = "null"
    var _note = "null"
    lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        db = FirebaseFirestore.getInstance()

        _tvTrasactionDate = findViewById<TextView>(R.id.tvTransactionDate)
        val _ivCalendar = findViewById<ImageView>(R.id.ivCalendar)
        _radioGroup = findViewById(R.id.rgType)
        val _rbIncome = findViewById<RadioButton>(R.id.rbIncome)
        val _rbExpense = findViewById<RadioButton>(R.id.rbExpense)
        val _etAmount = findViewById<TextInputEditText>(R.id.etAmount)
        val _dropdownCategoryItem = findViewById<AutoCompleteTextView>(R.id.dropdownCategoryItem)
        val _etNotes = findViewById<TextInputEditText>(R.id.etNotes)
        val _addTransactionBtn = findViewById<Button>(R.id.addTransactionBtn)
        val _cancelTransactionBtn = findViewById<Button>(R.id.cancelAddTransactionBtn)

        GetCurrentDate()
        var monthStr = "-"
        when(month+1) {
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
        _tvTrasactionDate.setText("${day} ${monthStr} ${year}")

        _ivCalendar.setOnClickListener {
            GetCurrentDate()
            DatePickerDialog(this, this, year, month, day).show()
        }

        _dropdownCategoryItem.setOnClickListener {
            if(!_rbIncome.isChecked && !_rbExpense.isChecked) {
                Toast.makeText(this, "Select transaction type first!", Toast.LENGTH_SHORT).show()
            }
        }

        _rbIncome.setOnClickListener {
            if(_rbIncome.isChecked) {
                _type = "income"
                val _incomecategory = resources.getStringArray(R.array.incomecategory)
                val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, _incomecategory)
                _dropdownCategoryItem.setAdapter(arrayAdapter)

                if(_dropdownCategoryItem.text.toString() == "Shopping" ||
                    _dropdownCategoryItem.text.toString() == "Food" ||
                    _dropdownCategoryItem.text.toString() == "Family and Friends" ||
                    _dropdownCategoryItem.text.toString() == "Education" ||
                    _dropdownCategoryItem.text.toString() == "Health" ||
                    _dropdownCategoryItem.text.toString() == "Travel" ||
                    _dropdownCategoryItem.text.toString() == "Entertainment" ||
                    _dropdownCategoryItem.text.toString() == "Others") {
                    _dropdownCategoryItem.setAdapter(arrayAdapter)
                    _dropdownCategoryItem.setText("-- Not Selected --")
                }
            }
        }

        _rbExpense.setOnClickListener {
            if(_rbExpense.isChecked) {
                _type = "expense"
                val _expensecategory = resources.getStringArray(R.array.expensecategory)
                val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, _expensecategory)
                _dropdownCategoryItem.setAdapter(arrayAdapter)

                if(_dropdownCategoryItem.text.toString() == "Salary" ||
                    _dropdownCategoryItem.text.toString() == "Gifts" ||
                    _dropdownCategoryItem.text.toString() == "Selling" ||
                    _dropdownCategoryItem.text.toString() == "Others") {
                    _dropdownCategoryItem.setAdapter(arrayAdapter)
                    _dropdownCategoryItem.setText("-- Not Selected --")
                }
            }
        }

        _addTransactionBtn.setOnClickListener {
            if(day == 0 || month == 0 || year == 0 || savedDay == 0 || savedMonth == 0 || savedYear == 0){
                Toast.makeText(this, "Please select transaction date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(_type == "null") {
                Toast.makeText(this, "Please select transaction type", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(_etAmount.text.toString().isEmpty()) {
                Toast.makeText(this, "Please fill in the amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(_dropdownCategoryItem.text.toString() == "-- Not Selected --") {
                Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(!_etNotes.text.toString().isEmpty()) {
                _note = _etNotes.text.toString()
            }

            // add to firebase
            val docRef = db.collection("transaction_table").document()
            val newdata = transaction(
                docRef.id,
                _type,
                _etAmount.text.toString(),
                _dropdownCategoryItem.text.toString(),
                _note,
                savedDay.toString(),
                savedMonth.toString(),
                savedYear.toString()
            )
            docRef.set(newdata)
                .addOnSuccessListener {
                    Log.d("Firebase", "Transaction is successfully added")
                }
                .addOnFailureListener {
                    Log.d("Firebase", it.message.toString())
                }

            val intent = Intent(this@addTransactionAct, MainActivity::class.java)
            startActivity(intent)
        }

        _cancelTransactionBtn.setOnClickListener {
            val intent = Intent(this@addTransactionAct, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun GetCurrentDate() {
        val cal: Calendar = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)

        savedDay = day
        savedMonth = month
        savedYear = year
    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        savedDay = dayOfMonth
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

        GetCurrentDate()
        _tvTrasactionDate.setText("$savedDay $monthStr $savedYear")
    }
}