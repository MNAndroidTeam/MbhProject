package com.medianet.mbhproject

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.medianet.tools.form.FormResultListener
import kotlinx.android.synthetic.main.activity_inscription.*
import kotlinx.android.synthetic.main.activity_inscription2.*
import kotlinx.android.synthetic.main.activity_main.btnSave
import kotlinx.android.synthetic.main.activity_main.formulaire
import java.io.IOException

class InscriptionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inscription)

        formulaire.formContainerVerification(
            this,
            model =User(
            email = "benhamouda@gmail.com",
            name = "mohamed",
            address = Address(name = "address"),
            gender = 1
        ).also { it.numberPhone = "22332423" })
        btnSave.setOnClickListener {

            formulaire.checkForm(

                formResultListener =  object  : FormResultListener<User> {

                    override fun formResultSuccessListener(result: User) {

                        Log.e("uuuuuu","$result")
                        startActivity(Intent(this@InscriptionActivity , Inscription2Activity::class.java).putExtra("user",result))
                    }

                    override fun formResultErrorListener(msg: String, id: Int) {
                        Toast.makeText(this@InscriptionActivity , "Error" , Toast.LENGTH_LONG).show()
                    }


                }
            )

        }
    }
}