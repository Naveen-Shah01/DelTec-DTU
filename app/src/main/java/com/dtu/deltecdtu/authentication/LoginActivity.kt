package com.dtu.deltecdtu.authentication


import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.children
import com.dtu.deltecdtu.MainActivity
import com.dtu.deltecdtu.R
import com.dtu.deltecdtu.util.Utility
import com.dtu.deltecdtu.databinding.ActivityLoginBinding
import com.dtu.deltecdtu.model.UsersEntity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val myReference: DatabaseReference = database.reference.child("Users")

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { false }
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        window.statusBarColor = ContextCompat.getColor(this, R.color.color_one_light_two)

        auth = FirebaseAuth.getInstance()
        registerActivityForGoogleSignIn()

        binding.tvSignUp.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        binding.tvForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }


        binding.btLogin.setOnClickListener {
            val loginEmail: String = binding.etLoginEmail.text.toString()
            val loginPassword: String = binding.etLoginPassword.text.toString()

            if (isDetailsNotEmpty(loginEmail, loginPassword)) {
                signInWithFireBase(loginEmail, loginPassword)
            }
        }
        binding.btSgnGoogle.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun isDetailsNotEmpty(loginEmail: String, loginPassword: String): Boolean {
        var valid = true
        if (TextUtils.isEmpty(loginEmail)) {
            binding.etLoginEmail.error = "Email cannot be empty"
            valid = false
        }

        if (TextUtils.isEmpty(loginPassword)) {
            binding.etLoginPassword.error = "Password cannot be empty"
            valid = false
        }
        return valid
    }

    private fun signInWithFireBase(userEmail: String, userPassword: String) {
        progressVisibleButtonNotClickable()
        auth.signInWithEmailAndPassword(userEmail, userPassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    if (auth.currentUser!!.isEmailVerified) {
                        progressInvisibleButtonClickable()
                        Toast.makeText(
                            applicationContext,
                            "Login Successful.", Toast.LENGTH_SHORT,
                        ).show()

                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {

                        Toast.makeText(
                            applicationContext,
                            "Email is not verified, Please verify", Toast.LENGTH_SHORT,
                        ).show()
                        progressInvisibleButtonClickable()
                    }

                } else {

                    Toast.makeText(
                        applicationContext,
                        task.exception?.localizedMessage, Toast.LENGTH_SHORT,
                    ).show()
                    binding.btLogin.isClickable = true
                    binding.pbProgressLogin.visibility = View.INVISIBLE
                }
            }
    }

    override fun onStart() {
        super.onStart()
        val user = auth.currentUser
        if (user != null && user.isEmailVerified) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun fireBaseSignInWithGoogle(task: Task<GoogleSignInAccount>) {
        try {
            val googleAccount: GoogleSignInAccount = task.getResult(ApiException::class.java)
            Toast.makeText(applicationContext, "Google sign in successful", Toast.LENGTH_LONG)
                .show()
            fireBaseGoogleAccount(googleAccount)
        } catch (e: ApiException) {
            progressInvisibleButtonClickable()
            Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }

    private fun fireBaseGoogleAccount(googleAccount: GoogleSignInAccount) {
        val authCredential = GoogleAuthProvider.getCredential(googleAccount.idToken, null)
        auth.signInWithCredential(authCredential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                val isNewUser = task.result.additionalUserInfo!!.isNewUser
                if (isNewUser) {
                    val userId = user!!.uid
                    var userName = user.displayName
                    var userEmail = user.email
                    if (userName.isNullOrEmpty()) {
                        userName = "User"
                    }
                    if (userEmail.isNullOrEmpty()) {
                        userEmail = "user@dce.com"
                    }
                    addToDatabase(userId, userName, userEmail)
                } else {
                    startActivity(
                        Intent(
                            this@LoginActivity, MainActivity::class.java
                        ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                    progressInvisibleButtonClickable()
                    finish()
                }
            } else {
                Toast.makeText(applicationContext, "Authentication Failed", Toast.LENGTH_LONG)
                    .show()
                progressInvisibleButtonClickable()
            }
        }
    }

    private fun addToDatabase(userId: String, userName: String, userEmail: String) {
        val joined = Utility.currentDate()
        val profileImageUrl = ""
        val imageName = ""
        val user = UsersEntity(userId, userName, userEmail, profileImageUrl, imageName, joined)
        myReference.child(userId).setValue(user).addOnSuccessListener {

            startActivity(
                Intent(
                    this@LoginActivity, MainActivity::class.java
                ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
            progressInvisibleButtonClickable()
            finish()
        }
            .addOnFailureListener {
                Toast.makeText(applicationContext, "Please try again", Toast.LENGTH_LONG)
                    .show()
                progressInvisibleButtonClickable()
            }
    }

    private fun signInWithGoogle() {
        progressVisibleButtonNotClickable()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("20746531457-bm6edfhg2dd99hv8qa8en37o209rqcov.apps.googleusercontent.com")
            .requestEmail().build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        signIn()
    }

    private fun signIn() {
        val signInIntent: Intent = googleSignInClient.signInIntent
        activityResultLauncher.launch(signInIntent)
    }

    private fun registerActivityForGoogleSignIn() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
                ActivityResultCallback { result ->
                    val resultCode = result.resultCode
                    val data = result.data
                    if (resultCode == RESULT_OK && data != null) {
                        val task: Task<GoogleSignInAccount> =
                            GoogleSignIn.getSignedInAccountFromIntent(data)
                        fireBaseSignInWithGoogle(task)
                    } else {
                        progressInvisibleButtonClickable()
                    }
                })
    }

    private fun progressVisibleButtonNotClickable() {
        binding.pbProgressLogin.visibility = View.VISIBLE
        binding.clRoot.setAllEnabled(false)
    }

    private fun progressInvisibleButtonClickable() {
        binding.pbProgressLogin.visibility = View.INVISIBLE
        binding.clRoot.setAllEnabled(true)
    }

    private fun View.setAllEnabled(enabled: Boolean) {
        isEnabled = enabled
        if (this is ViewGroup) children.forEach { child -> child.setAllEnabled(enabled) }
    }
}