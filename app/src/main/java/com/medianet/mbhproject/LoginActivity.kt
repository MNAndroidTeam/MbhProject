package com.medianet.mbhproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.medianet.tools.form.FormResultListener
import com.medianet.tools.form.FormTextInputLayout
import com.medianet.tools.form.Login
import kotlinx.android.synthetic.main.activity_main.*

class LoginActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        formulaire.formContainerVerification(this,formResultListener = object  :
            FormResultListener<Login> {


            override fun formResultSuccessListener(result: Login) {
              startActivity(Intent(this@LoginActivity , InscriptionActivity::class.java))
            }

            override fun formResultErrorListener(msg: String, id: Int) {
                Toast.makeText(this@LoginActivity , "Error" , Toast.LENGTH_LONG).show()
                findViewById<FormTextInputLayout>(id).error = msg
            }


        })


    }
}