package com.example.uas_c14190099

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.internal.artificialFrame

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [transactionFr.newInstance] factory method to
 * create an instance of this fragment.
 */
class transactionFr : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var _rvTransactions: RecyclerView
    private var arTransaction = arrayListOf<transaction>()
    lateinit var db: FirebaseFirestore

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transaction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()
        _rvTransactions = view.findViewById(R.id.rvTransactions)

        DisplayTransactions()
    }

    private fun DisplayTransactions() {
        _rvTransactions.layoutManager = LinearLayoutManager(activity)
        val adapterT = adapterTransaction(arTransaction)
        _rvTransactions.adapter = adapterT

        db.collection("transaction_table")
            .get()
            .addOnSuccessListener { result ->
                arTransaction.clear()
                for(document in result) {
                    val data = transaction(document.id,
                        document.data.get("type").toString(),
                        document.data.get("amount").toString(),
                        document.data.get("category").toString(),
                        document.data.get("note").toString(),
                        document.data.get("day").toString(),
                        document.data.get("month").toString(),
                        document.data.get("year").toString())
                    arTransaction.add(data)
                }
                adapterT.notifyDataSetChanged()
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment transactionFr.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            transactionFr().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}