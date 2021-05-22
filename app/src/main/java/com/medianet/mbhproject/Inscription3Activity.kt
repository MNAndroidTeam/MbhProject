package com.medianet.mbhproject

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.NonNull
import com.medianet.tools.form.FormContainerLayout
import com.medianet.tools.form.FormResultListener
import com.medianet.tools.form.stringToBitMap
import kotlinx.android.synthetic.main.activity_inscription2.*
import kotlinx.android.synthetic.main.activity_inscription2.formulaire
import kotlinx.android.synthetic.main.activity_inscription2.image
import kotlinx.android.synthetic.main.activity_inscription3.*
import java.text.SimpleDateFormat
import java.util.*

class Inscription3Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inscription3)

        dddd.formContainerVerification(
            activity = this,
            model = User(),
            formatter = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        @NonNull permissions: Array<String>,
        @NonNull grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == FormContainerLayout.PERMISSIONS_REQUEST_CODE) {
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
        dddd.onActivityResult(requestCode , resultCode, data)
    }



}