package com.dtu.deltecdtu.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dtu.deltecdtu.databinding.ActivityFullScreenImageBinding

class FullScreenImageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFullScreenImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullScreenImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUrl = intent.getStringExtra("image")
        Glide.with(this).load(imageUrl).into(binding.tivFullImage)
    }
}