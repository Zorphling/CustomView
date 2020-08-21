package com.test.testclean.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.test.testclean.R
import kotlinx.android.synthetic.main.activity_scan.*

class ScanActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)
        scanner_view.postDelayed({
            scanner_view.startScan()
        },1000)
    }

    override fun onDestroy() {
        super.onDestroy()
        scanner_view.release()
    }
}