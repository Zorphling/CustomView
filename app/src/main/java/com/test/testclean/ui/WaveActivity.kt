package com.test.testclean.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.test.testclean.R
import kotlinx.android.synthetic.main.activity_wave.*

class WaveActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wave)
        wave_progress.setProgressNum(80f, 3000)

    }
}