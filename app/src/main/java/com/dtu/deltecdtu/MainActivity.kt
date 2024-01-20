package com.dtu.deltecdtu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import com.dtu.deltecdtu.databinding.ActivityMainBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.color_one_light_two)
        subscribeToTopic()
    }

    private fun subscribeToTopic() {
        Firebase.messaging.subscribeToTopic("News")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
//                    Log.e("Firebase Notification", "Subscribed you will receive notifications")
                } else {
                    subscribeToTopic()
                }
            }
    }
}