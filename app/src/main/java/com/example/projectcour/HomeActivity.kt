package com.example.projectcour

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_home.*
import java.lang.reflect.Type

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)



        val addContactButton = addContactButton
        var isCheckedBool  = false
        val contactAdapter = ContactRecyclerViewAdapter(loadContacts())
        contactAdapter.fixedList.addAll(loadContacts())
        contactAdapter.fixedList.forEach{
            setListenerOnItem(contactAdapter,isCheckedBool)
        }



        val checkBoxFavorisFilter = checkBoxFavoris //checkBoxFavoris est l'id de ma checkbox
        checkBoxFavorisFilter.setOnCheckedChangeListener { buttonView, isChecked ->
            Log.d(TAG, "onCheckedListener : ici ")
            updateList(isChecked,contactAdapter) //j'update ma liste en cas de filtrage sur les favoris
            isCheckedBool = isChecked
        }


        val registerActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            Log.d(TAG, "onRegister : if avant ")
            Log.d(TAG, "onCreate: ${result}")
            if(result != null && result.data != null && result.resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "onRegister : if entré ")
                if (Build.VERSION.SDK_INT >= 33) {
                    val contact = result.data!!.getSerializableExtra("contactData", ContactData::class.java)
                    contact?.let{
                        contactAdapter.fixedList.add(it)
                        updateList(isCheckedBool,contactAdapter)
                        contactAdapter.notifyItemInserted(contactAdapter.itemCount)
                        saveContact(contactAdapter.fixedList)
                        setListenerOnItem(contactAdapter,isCheckedBool)

                    }
                }
            }
        }

        addContactButton.setOnClickListener {
            val startAddContact = Intent(this,AddContactActivity::class.java)
            startAddContact.putExtra("firstName","TP3TestFirstname") //j'ai fait ca car c'était demandé dans le tp
            registerActivity.launch(startAddContact)
        }

        recyclerView.adapter = contactAdapter

        recyclerView.layoutManager = LinearLayoutManager(this)

    }

    fun setListenerOnItem(contactAdapter:ContactRecyclerViewAdapter,isCheckedBool: Boolean){
        //set un listener on item click
        contactAdapter.onItemClick = { contact ->
            Toast.makeText(this, contact.prenom +" "+contact.nom+" : "+contact.telephone+", "+contact.email+", "+contact.naissance+", "+contact.genre, Toast.LENGTH_LONG).show()
        }
        //le long press listener supprime l'item séléctionné
        contactAdapter.onItemLongClick = { contact ->
            contactAdapter.fixedList.remove(contact)
            updateList(isCheckedBool,contactAdapter)
            contactAdapter.notifyItemRemoved(contactAdapter.contactList.indexOf(contact))
        }
    }

    fun updateList(isChecked:Boolean,contactAdapter : ContactRecyclerViewAdapter){
        if (isChecked) {
            contactAdapter.contactList.clear()
            contactAdapter.fixedList.forEach{
                if(it.favoris){
                    contactAdapter.contactList.add(it)
                }
            }
            contactAdapter.notifyDataSetChanged()
        }
        else{
            contactAdapter.contactList.clear()
            contactAdapter.contactList.addAll(contactAdapter.fixedList)
            contactAdapter.notifyDataSetChanged()
        }
    }
    // Fonction pour charger les données
    fun loadContacts(): MutableList<ContactData> {
        val sharedPreferences = applicationContext.getSharedPreferences("contactlist", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("contactlist", null)
        val listType: Type = object : TypeToken<MutableList<ContactData>>(){}.type
        if (json == null){
            return mutableListOf()
        }else{
            return gson.fromJson(json, listType)
        }
    }
    // Fonction pour sauvegarder les données
    fun saveContact(data: MutableList<ContactData>) {
        val sharedPreferences = applicationContext.getSharedPreferences("contactlist", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(data)
        editor.putString("contactlist", json)
        editor.apply()
    }

}