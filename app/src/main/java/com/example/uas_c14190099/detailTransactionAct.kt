package com.example.uas_c14190099

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore

class detailTransactionAct : AppCompatActivity() {

    lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_transaction)

        db = FirebaseFirestore.getInstance()

        val _transactionData = intent.getParcelableExtra<transaction>("transactionData")
        val _ivBackground = findViewById<ImageView>(R.id.ivBackground)
        val _ivCategoryDet = findViewById<ImageView>(R.id.ivCategoryDet)
        val _tvCategoryDet = findViewById<TextView>(R.id.tvCategoryDet)
        val _tvAmountDet = findViewById<TextView>(R.id.tvAmountDet)
        val _tvDateDet = findViewById<TextView>(R.id.tvDateDet)
        val _ivNoteDet = findViewById<ImageView>(R.id.ivNoteDet)
        val _tvNoteDet = findViewById<TextView>(R.id.tvNoteDet)
        val _editDetBtn = findViewById<Button>(R.id.editDetBtn)
        val _deleteDetBtn = findViewById<Button>(R.id.deleteDetBtn)

        _tvCategoryDet.setText(_transactionData!!.category)

        _tvAmountDet.setText(_transactionData!!.amount)

        var monthStr = "-"
        when(_transactionData.month) {
            "1" -> monthStr = "January"
            "2" -> monthStr = "February"
            "3" -> monthStr = "March"
            "4" -> monthStr = "April"
            "5" -> monthStr = "May"
            "6" -> monthStr = "June"
            "7" -> monthStr = "July"
            "8" -> monthStr = "August"
            "9" -> monthStr = "September"
            "10" -> monthStr = "October"
            "11" -> monthStr = "November"
            "12" -> monthStr = "December"
        }
        _tvDateDet.setText("${monthStr} ${_transactionData.day}, ${_transactionData.year}")

        if(_transactionData.note == "null") {
            _ivNoteDet.visibility = View.INVISIBLE
            _tvNoteDet.visibility = View.INVISIBLE
        } else {
            _tvNoteDet.setText(_transactionData!!.note)
        }

        _editDetBtn.setOnClickListener {
            val intent = Intent(this@detailTransactionAct, editTransactionAct::class.java)
            intent.putExtra("transactionData", _transactionData)
            startActivity(intent)
        }

        _deleteDetBtn.setOnClickListener {
            db.collection("transaction_table").document(_transactionData!!.id)
                .delete()
                .addOnSuccessListener {
                    Log.d("Firebase", "Transaction was successfully deleted")
                }
                .addOnFailureListener {
                    Log.d("Firebase", it.message.toString())
                }
            val intent = Intent(this@detailTransactionAct, MainActivity::class.java)
            startActivity(intent)
        }
    }
}