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

class editTransactionAct : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    var day = 0
    var month = 0
    var year = 0

    var savedDay = 0
    var savedMonth = 0
    var savedYear = 0

    lateinit var _radioGroupEdit: RadioGroup
    lateinit var db: FirebaseFirestore
    lateinit var _tvTrasactionDateEdit: TextView
    var _type = "null"
    var _note = "null"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_transaction)

        db = FirebaseFirestore.getInstance()

        val _transactionData = intent.getParcelableExtra<transaction>("transactionData")
        _tvTrasactionDateEdit = findViewById(R.id.tvTransactionDateEdit)
        val _ivCalendarEdit = findViewById<ImageView>(R.id.ivCalendarEdit)
        _radioGroupEdit = findViewById(R.id.rgTypeEdit)
        val _rbIncomeEdit = findViewById<RadioButton>(R.id.rbIncomeEdit)
        val _rbExpenseEdit = findViewById<RadioButton>(R.id.rbExpenseEdit)
        val _etAmountEdit = findViewById<TextInputEditText>(R.id.etAmountEdit)
        val _dropdownCategoryItemEdit = findViewById<AutoCompleteTextView>(R.id.dropdownCategoryItemEdit)
        val _etNotesEdit = findViewById<TextInputEditText>(R.id.etNoteEdit)
        val _saveChangesBtn = findViewById<Button>(R.id.saveChangesBtn)
        val _deleteTransactionEditBtn = findViewById<Button>(R.id.deleteTransactionEditBtn)

        // set text transaction date
        day = _transactionData!!.day.toInt()
        month = _transactionData!!.month.toInt()
        year = _transactionData!!.year.toInt()

        savedDay = day
        savedMonth = month

        savedYear = year

        var monthStr = "-"
        when(_transactionData!!.month) {
            "1" -> monthStr = "Jan"
            "2" -> monthStr = "Feb"
            "3" -> monthStr = "Mar"
            "4" -> monthStr = "Apr"
            "5" -> monthStr = "May"
            "6" -> monthStr = "Jun"
            "7" -> monthStr = "Jul"
            "8" -> monthStr = "Aug"
            "9" -> monthStr = "Sep"
            "10" -> monthStr = "Oct"
            "11" -> monthStr = "Nov"
            "12" -> monthStr = "Dec"
        }
        _tvTrasactionDateEdit.setText("${_transactionData!!.day} ${monthStr} ${_transactionData!!.year}")
        _ivCalendarEdit.setOnClickListener {
            DatePickerDialog(this, this, year, month-1, day).show()
        }

        // set selected income / expense
        if(_transactionData.type == "income") {
            _radioGroupEdit.check(R.id.rbIncomeEdit)
            _type = "income"
            val _incomecategory = resources.getStringArray(R.array.incomecategory)
            val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, _incomecategory)
            _dropdownCategoryItemEdit.setAdapter(arrayAdapter)
        } else if(_transactionData.type == "expense") {
            _radioGroupEdit.check(R.id.rbExpenseEdit)
            _type = "expense"
            val _expensecategory = resources.getStringArray(R.array.expensecategory)
            val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, _expensecategory)
            _dropdownCategoryItemEdit.setAdapter(arrayAdapter)
        }

        _rbIncomeEdit.setOnClickListener {
            if(_rbIncomeEdit.isChecked) {
                _type = "income"
                val _incomecategory = resources.getStringArray(R.array.incomecategory)
                val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, _incomecategory)
                _dropdownCategoryItemEdit.setAdapter(arrayAdapter)

                if(_dropdownCategoryItemEdit.text.toString() == "Shopping" ||
                    _dropdownCategoryItemEdit.text.toString() == "Food" ||
                    _dropdownCategoryItemEdit.text.toString() == "Family and Friends" ||
                    _dropdownCategoryItemEdit.text.toString() == "Education" ||
                    _dropdownCategoryItemEdit.text.toString() == "Health" ||
                    _dropdownCategoryItemEdit.text.toString() == "Travel" ||
                    _dropdownCategoryItemEdit.text.toString() == "Entertainment" ||
                    _dropdownCategoryItemEdit.text.toString() == "Others") {
                    _dropdownCategoryItemEdit.setText("-- Not Selected --")
                }
            }
        }

        _rbExpenseEdit.setOnClickListener {
            if(_rbExpenseEdit.isChecked) {
                _type = "expense"
                val _expensecategory = resources.getStringArray(R.array.expensecategory)
                val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, _expensecategory)
                _dropdownCategoryItemEdit.setAdapter(arrayAdapter)

                if(_dropdownCategoryItemEdit.text.toString() == "Salary" ||
                    _dropdownCategoryItemEdit.text.toString() == "Gifts" ||
                    _dropdownCategoryItemEdit.text.toString() == "Selling" ||
                    _dropdownCategoryItemEdit.text.toString() == "Others") {
                    _dropdownCategoryItemEdit.setText("-- Not Selected --")
                }
            }
        }

        // set amount
        _etAmountEdit.setText(_transactionData.amount)

        // set category di dropdown
        _dropdownCategoryItemEdit.setText(_transactionData.category)

        // set notes
        if(_transactionData.note != "null") {
            _etNotesEdit.setText(_transactionData.note)
        }

        _saveChangesBtn.setOnClickListener {
            if(day == 0 || month == 0 || year == 0 || savedDay == 0 || savedMonth == 0 || savedYear == 0){
                Toast.makeText(this, "Please select transaction date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(_type == "null") {
                Toast.makeText(this, "Please select transaction type", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(_etAmountEdit.text.toString().isEmpty()) {
                Toast.makeText(this, "Please fill in the amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(_dropdownCategoryItemEdit.text.toString() == "-- Not Selected --") {
                Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(!_etNotesEdit.text.toString().isEmpty()) {
                _note = _etNotesEdit.text.toString()
            }

            val newdata = transaction(
                _transactionData.id,
                _type,
                _etAmountEdit.text.toString(),
                _dropdownCategoryItemEdit.text.toString(),
                _note,
                savedDay.toString(),
                savedMonth.toString(),
                savedYear.toString()
            )
            db.collection("transaction_table").document(_transactionData.id).set(newdata)
                .addOnSuccessListener {
                    Log.d("Firebase", "Transaction is successfully added")
                }
                .addOnFailureListener {
                    Log.d("Firebase", it.message.toString())
                }

            val intent = Intent(this@editTransactionAct, MainActivity::class.java)
            startActivity(intent)
        }

        _deleteTransactionEditBtn.setOnClickListener {
            db.collection("transaction_table").document(_transactionData!!.id)
                .delete()
                .addOnSuccessListener {
                    Log.d("Firebase", "Transaction was successfully deleted")
                }
                .addOnFailureListener {
                    Log.d("Firebase", it.message.toString())
                }

            val intent = Intent(this@editTransactionAct, MainActivity::class.java)
            startActivity(intent)
        }
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

        _tvTrasactionDateEdit.setText("$savedDay $monthStr $savedYear")
    }
}