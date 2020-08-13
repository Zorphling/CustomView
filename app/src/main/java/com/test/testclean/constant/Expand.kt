package com.test.testclean.constant

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import java.util.logging.Logger

fun <T> View.startA(context: Context, clazz: Class<T>) {
    this.setOnClickListener {
        context.startActivity(Intent(context, clazz))
    }
}

const val TAG = "CONSTANT"
fun log(msg: Any) {
    Log.e("$TAG", "$msg")
}