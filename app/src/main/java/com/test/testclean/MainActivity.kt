package com.test.testclean

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.test.testclean.constant.startA
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        random_draw_view.startA(this,RandomDrawActivity::class.java)
        scan.startA(this, ScanActivity::class.java)
        clean.startA(this, CleanActivity::class.java)
        start_home_view.setOnClickListener {
            ripple_circle_view.post { ripple_circle_view.start() }
        }
        stop_home_view.setOnClickListener {
            ripple_circle_view.release()
        }
    }

    override fun onResume() {
        super.onResume()
        ripple_circle_view.resume()
    }

    override fun onStop() {
        super.onStop()
        ripple_circle_view.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        ripple_circle_view.release()
    }
}