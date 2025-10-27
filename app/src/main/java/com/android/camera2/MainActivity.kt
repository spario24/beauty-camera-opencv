package com.android.camera2

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.camera.activity.MainActivity
import com.android.camera2.fragment.Camera2M1

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

//        addFragment()
        gotoCameraBeauty()
    }

    fun addFragment(){
        val frag = Camera2M1()

        supportFragmentManager.beginTransaction()
            .add(R.id.main, frag)
            .commit()
    }

    fun gotoCameraBeauty(){
        startActivity(Intent(this, MainActivity::class.java))
    }

}