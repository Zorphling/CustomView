package com.test.testclean.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.test.testclean.R
import kotlinx.android.synthetic.main.activity_wave_ok.*

class WaveOkActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wave_ok)
        wave_view.setProgressNum(60f,3000)
    }
}