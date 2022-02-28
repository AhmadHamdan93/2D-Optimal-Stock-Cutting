package com.example.cuttingsolver

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val t1=Handler()
        t1.postDelayed(Runnable {
            runOnUiThread {
                val i = Intent(this,WorkActivity::class.java)
                startActivity(i)
                finish()
            }
        },3000)

    }
}
