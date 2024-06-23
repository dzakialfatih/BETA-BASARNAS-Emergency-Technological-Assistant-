package com.example.beta.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.beta.DetailActivity
import com.example.beta.R
import com.example.beta.response.RefugeeResponse

class RefugeeAdapter(private val context: Context, private val refugees: List<RefugeeResponse>) : BaseAdapter() {
    override fun getCount(): Int {
        return refugees.size
    }

    override fun getItem(position: Int): Any {
        return refugees[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_user, parent, false)

        val refugee = refugees[position]
        val nameTextView: TextView = view.findViewById(R.id.tv_user_name)
        val poskoTextView: TextView = view.findViewById(R.id.posko)

        nameTextView.text = refugee.name
        poskoTextView.text = refugee.posko

        view.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("REFUGEE_ID", refugee.id)
            context.startActivity(intent)
        }

        return view
    }
}