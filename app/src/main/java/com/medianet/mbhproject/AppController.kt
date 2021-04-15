package com.medianet.mbhproject

import android.app.Application
import java.text.SimpleDateFormat
import java.util.*

class AppController : Application() {

    var dateTimeFormatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    var dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    var timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
}