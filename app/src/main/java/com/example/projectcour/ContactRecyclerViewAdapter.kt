package com.example.projectcour

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactRecyclerViewAdapter(var contactList :MutableList<ContactData>) : RecyclerView.Adapter<ContactRecyclerViewAdapter.ContactViewHolder>() {
    var fixedList = mutableListOf<ContactData>()

    override fun onCreateViewHolder(parent : ViewGroup, position : Int) : ContactViewHolder{

        val monItem = LayoutInflater.from(parent.context).inflate(R.layout.item_view_recycler,parent, false)

        return ContactViewHolder(monItem)

    }
    var onItemClick: ((ContactData) -> Unit)? = null
    var onItemLongClick: ((ContactData) -> Unit)? = null


    override fun onBindViewHolder(viewHolder: ContactViewHolder, position: Int) {
        val contactList = contactList[position]

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        if(contactList.photoUri!=null && contactList.photoUri!=""){
            viewHolder.photoView.setImageURI(Uri.parse(contactList.photoUri))
        }
        viewHolder.firstLine.text = contactList.prenom +" "+ contactList.nom
        viewHolder.secondLine.text = contactList.telephone
    }

    inner class ContactViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var photoView : ImageView
        var secondLine: TextView
        var firstLine: TextView
        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(contactList[adapterPosition])
            }
            itemView.setOnLongClickListener{
                onItemLongClick?.invoke(contactList[adapterPosition])
                return@setOnLongClickListener true
            }

            // Define click listener for the ViewHolder's View
            photoView = view.findViewById(R.id.photo_contact)
            firstLine = view.findViewById(R.id.firstLine)
            secondLine = view.findViewById(R.id.secondLine)
        }
    }
    override fun getItemCount() = contactList.size

}