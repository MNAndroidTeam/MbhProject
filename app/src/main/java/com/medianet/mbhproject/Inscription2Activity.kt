package com.medianet.mbhproject

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.medianet.tools.form.FormContainerLayout
import com.medianet.tools.form.FormContainerLayout.Companion.PERMISSIONS_REQUEST_CODE
import com.medianet.tools.form.FormResultListener
import com.medianet.tools.form.stringToBitMap
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_inscription2.*
import java.text.SimpleDateFormat
import java.util.*

class Inscription2Activity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inscription2)

        val user = intent.getSerializableExtra("user") as User

        spinner.initData(listOf(
            AgeInterval("1","de 5 à 10"),
            AgeInterval("2","de 10 à 20"),
            AgeInterval("3","de 20 à 40"),
            AgeInterval("4","de 40 à 60")
        ))


        formulaire.formContainerVerification(
            activity = this,
            model = user.also { it.age = "de 20 à 40" } ,
            formatter = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()),
            formResultListener =  object  : FormResultListener<User> {

                override fun formResultSuccessListener(result: User) {

                    Log.e("uuuuuu","${result.image.length}")
                }

                override fun formResultErrorListener(msg: String, id: Int) {
                    if(id == R.id.checkBox) checkBox.error = msg
                    Toast.makeText(this@Inscription2Activity , "Error" , Toast.LENGTH_LONG).show()
                }


            }
        )


        btnCamera.setOnClickListener {
            formulaire.takePhotoFromCamera(image)
        }

        btnGallerie.setOnClickListener {
            formulaire.choosePhotoFromGallary(image)
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        @NonNull permissions: Array<String>,
        @NonNull grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            val permissionResults =
                HashMap<String, Int>()
            var denidedCount = 0
            for (i in grantResults.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    permissionResults[permissions[i]] = grantResults[i]
                    denidedCount++
                }
            }

            if (denidedCount==0){
                if (!formulaire.onRequestPermissionsResultGranted()){
                    // call ur costum popup here
                }

            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        formulaire.onActivityResult(requestCode , resultCode, data)
    }


}