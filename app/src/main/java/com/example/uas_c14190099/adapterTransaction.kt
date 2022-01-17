package com.example.uas_c14190099

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class adapterTransaction (
    private val listTransaction: ArrayList<transaction>): RecyclerView.Adapter<adapterTransaction.ListViewHolder>() {
        inner class ListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            var _cvTransaction: CardView = itemView.findViewById(R.id.cvTransaction)
            var _ivType: ImageView = itemView.findViewById(R.id.ivType)
            var _tvDate: TextView = itemView.findViewById(R.id.tvDate)
            var _tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
            var _tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
            var _tvNote: TextView = itemView.findViewById(R.id.tvNote)
        }

    private lateinit var onItemClickCallback: OnItemClickCallback

    interface OnItemClickCallback {
        fun onItemClicked(data: transaction)
    }

    fun setOnItemClickCallbcak(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        var data = listTransaction[position]

        holder._cvTransaction.setOnClickListener {
            onItemClickCallback.onItemClicked(data)
        }
        holder._tvCategory.text = data.category
        if(data.note != "null") {
            holder._tvNote.text = data.note
        } else {
            holder._tvNote.visibility = View.INVISIBLE
        }

        var monthStr = "0"
        when(data.month) {
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
        holder._tvDate.text = ("${monthStr} ${data.day}, ${data.year}")

        if(data.type == "income") {
            holder._tvAmount.text = "+Rp${data.amount}"
            holder._tvAmount.setTextColor(Color.parseColor("#1a8f03"))

            when(data.category) {

            }
        } else if(data.type == "expense") {
            holder._tvAmount.text = "-Rp${data.amount}"
            holder._tvAmount.setTextColor(Color.parseColor("#c70000"))
        }


    }

    override fun getItemCount(): Int {
        return listTransaction.count()
    }
}