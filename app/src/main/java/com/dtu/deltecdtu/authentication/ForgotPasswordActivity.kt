package com.dtu.deltecdtu.authentication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.dtu.deltecdtu.R
import com.dtu.deltecdtu.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth


class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        window.statusBarColor = ContextCompat.getColor(this, R.color.color_one_light_two)

        binding.tvResetLogin.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btResetPassword.setOnClickListener {
            val email = binding.etResetEmail.text.toString()
            if (email.isNotEmpty()) {
                auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            applicationContext,
                            "Reset Link has been sent on registered mail",
                            Toast.LENGTH_LONG
                        ).show()

                        finish()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            task.exception?.localizedMessage, Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
            }
        }
    }
}