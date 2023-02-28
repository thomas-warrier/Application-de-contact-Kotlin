package com.example.projectcour

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.PopupMenu
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*



class AddContactActivity : AppCompatActivity() {

    val myCalendar: Calendar = Calendar.getInstance()
    var naissanceString : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val firstNameInput = prenom
        firstNameInput.setText(intent.getStringExtra("firstName"))

        val SendButton = findViewById<Button>(R.id.buttonConnect)
        val naissance = naissance
        val date =
            DatePickerDialog.OnDateSetListener { view, year, month, day ->
                myCalendar[Calendar.YEAR] = year
                myCalendar[Calendar.MONTH] = month
                myCalendar[Calendar.DAY_OF_MONTH] = day
                updateLabel()
            }

        // set on-click listener
        naissance.setOnClickListener {
            DatePickerDialog(
                this,
                date,
                myCalendar[Calendar.YEAR],
                myCalendar[Calendar.MONTH],
                myCalendar[Calendar.DAY_OF_MONTH]
            ).show() }

        //choisir une photo
        var uriPhoto : Uri?=null
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                // Callback is invoked after the user selects a media item or closes the
                // photo picker.
                if (uri != null) {
                    Log.d("PhotoPicker", "Selected URI: $uri")
                    uriPhoto = uri

                    contactPictureButton.setImageURI(uri)

                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }
        val takePhoto = registerForActivityResult(ActivityResultContracts.TakePicture()){
            result-> if(result) contactPictureButton.setImageURI(uriPhoto)
        }

        contactPictureButton?.setOnClickListener {
            val menu = PopupMenu(this,contactPictureButton)
            menu.menuInflater.inflate(R.menu.drop_down_menu,menu.menu)
            menu.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.chooseGalery ->  pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    R.id.takePicture -> {
                        val fileDirectory = File(applicationContext.filesDir, "camera_images")
                        if (!fileDirectory.exists()) {
                            fileDirectory.mkdirs()
                        }
                        val file = File.createTempFile(
                            "IMG_", ".jpg", getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                        )
                        uriPhoto = FileProvider.getUriForFile(this, "com.example.projectcour.provider", file)

                        takePhoto.launch(uriPhoto)
                    }

                    }


                return@setOnMenuItemClickListener true
            }
            menu.show()


        }
        var favoris =false
        SendButton.setOnClickListener {
            if(checkbox.isChecked()){
                favoris = true
            }
            Toast.makeText(this@AddContactActivity, "Donnée envoyé.", Toast.LENGTH_SHORT).show()

            //gestion des genres
            val genreId = genre.checkedRadioButtonId
            val selectedRadioButton = findViewById<RadioButton>(genreId)
            var genre : Genre
            when (selectedRadioButton.text) {
                "Homme" -> {genre = Genre.HOMME}
                "Femme" -> {genre = Genre.FEMME}
                else -> {genre = Genre.AUTRE}
            }


            try {
                //vérifier si les champs sont bien remplis
                if (nom.text?.isBlank() == true || prenom.text?.isBlank() == true || email.text?.isBlank() == true
                    || phone.text?.isBlank() == true || checkbox.text?.isBlank() == true
                ) {
                    if (nom.text?.isBlank() == true) {
                        Snackbar.make(mainLayout, "Champs Manquant : nom", Snackbar.LENGTH_LONG).show()
                    } else if (prenom.text?.isBlank() == true) {
                        Snackbar.make(mainLayout, "Champs Manquant : prenom", Snackbar.LENGTH_LONG).show()
                    } else if (email.text?.isBlank() == true) {
                        Snackbar.make(mainLayout, "Champs Manquant : email", Snackbar.LENGTH_LONG).show()
                    } else if (phone.text?.isBlank() == true) {
                        Snackbar.make(mainLayout, "Champs Manquant : numéros de téléphone", Snackbar.LENGTH_LONG).show()
                    } else if (naissance.text?.isBlank() == true) {
                        Snackbar.make(mainLayout, "Champs Manquant : date de naissance", Snackbar.LENGTH_LONG).show()
                    }


                }else{

                    val contactData = ContactData(if(uriPhoto!=null)uriPhoto.toString()else{""},nom.text.toString(),prenom.text.toString(),email.text.toString(),
                        naissanceString,phone.text.toString(),genre,favoris)

                    val intent = Intent()
                    intent.putExtra("contactData",contactData)
                    setResult(Activity.RESULT_OK,intent)
                    finish()
                }
            }catch (_:Exception){}






            //show la dialog
            showDialog(
                genre.toString(),
                prenom.text.toString(),
                nom.text.toString(),
                email.text.toString(),
                phone.text.toString(),
                naissanceString,
                favoris
            )


        }
    }



    private fun updateLabel() {
        val myFormat = "dd/MM/yy"
        val dateFormat = SimpleDateFormat(myFormat, Locale.US)
        naissance?.setText(dateFormat.format(myCalendar.time))
        naissanceString = dateFormat.format(myCalendar.time)
    }

     fun showDialog(
         genre : String,
        firstname: String,
        name: String,
        email: String,
        phone: String,
        naissance : String,
        favoris : Boolean

    ) {

        AlertDialog.Builder(this)
            .setTitle("Info")
            .setMessage("genre : $genre \nprénom : $firstname\nnom : $name\nemail : $email\nphone : $phone\ndate naissance : $naissance\nfavoris : $favoris")
            .show()

    }
    override fun onResume() {
        super.onResume()
        Log.e("Resume","app is resuming")
    }




}