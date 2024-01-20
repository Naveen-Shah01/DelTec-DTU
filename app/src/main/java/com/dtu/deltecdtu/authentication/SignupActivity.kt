package com.dtu.deltecdtu.authentication

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.dtu.deltecdtu.MainActivity
import com.dtu.deltecdtu.R
import com.dtu.deltecdtu.databinding.ActivitySignupBinding
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
import java.util.regex.Pattern



//TODO handle the case when user deletes the email so resend the verification email or give option to resend verification link on mail
//TODO add user to database when sig-up by google
class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var googleSignInClient: GoogleSignInClient
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val myReference: DatabaseReference = database.reference.child("Users")

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        window.statusBarColor = ContextCompat.getColor(this, R.color.color_one_light_two)

        //register the launcher
        registerActivityForGoogleSignIn()

        binding.tvLoginFromSignUp.setOnClickListener {
            // it will automatically destroy activity.
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btSignUp.setOnClickListener {
            signUpStart()
        }

        binding.btSgnUpGoogle.setOnClickListener {
            signUpWithGoogle()
        }

        binding.etPasswordSignUp.addTextChangedListener {
            binding.tilPassword.error = null
        }
    }

    private fun signUpWithGoogle() {

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("20746531457-bm6edfhg2dd99hv8qa8en37o209rqcov.apps.googleusercontent.com")
            .requestEmail().build()


        googleSignInClient = GoogleSignIn.getClient(this, gso)
        signUp()
    }


    private fun signUp() {
        val signInIntent: Intent = googleSignInClient.signInIntent
        activityResultLauncher.launch(signInIntent)

    }

    private fun registerActivityForGoogleSignIn() {
        activityResultLauncher =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult(),
                ActivityResultCallback { result ->
                    val resultCode = result.resultCode
                    val data = result.data
                    if (resultCode == RESULT_OK && data != null) {
                        val task: Task<GoogleSignInAccount> =
                            GoogleSignIn.getSignedInAccountFromIntent(data)
                        fireBaseSignInWithGoogle(task)
                    }
                })
    }

    private fun fireBaseSignInWithGoogle(task: Task<GoogleSignInAccount>) {
        try {
            val googleAccount: GoogleSignInAccount = task.getResult(ApiException::class.java)
            Toast.makeText(applicationContext, "Google sign up successful", Toast.LENGTH_LONG)
                .show()
            fireBaseGoogleAccount(googleAccount)
        } catch (e: ApiException) {
            Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }

    private fun fireBaseGoogleAccount(googleAccount: GoogleSignInAccount) {
        val authCredential = GoogleAuthProvider.getCredential(googleAccount.idToken, null)
        auth.signInWithCredential(authCredential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
//                val user = auth.currentUser

                startActivity(
                    Intent(
                        this@SignupActivity, MainActivity::class.java
                    ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
                finish()
            } else {
                Toast.makeText(applicationContext, "Authentication Failed", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun signUpStart() {
        val signUpEmail: String = binding.etEmailSignUp.text.toString().trim()
        val signUpName: String = binding.etNameSignUp.text.toString().trim()
        val signUpPassword: String = binding.etPasswordSignUp.text.toString().trim()

        if (isDetailsNotEmpty(signUpEmail, signUpName, signUpPassword) && isPassWordValid(
                signUpPassword
            )
        ) {
            signUpWithFireBase(signUpEmail, signUpPassword, signUpName)
        }
    }

    private fun isPassWordValid(password: String): Boolean {
        val digitPattern = Pattern.compile(".*\\d.*")
        val letterPattern = Pattern.compile(".*[a-zA-Z].*")
        var valid = false
        if (password.length < 8 || password.length > 20) {
            binding.tilPassword.error = "Password must be 8 to 20 characters!"
        } else if (!(digitPattern.matcher(password).matches() && letterPattern.matcher(password)
                .matches())
        ) {
            binding.tilPassword.error = "Password must contain number and alphabet"
        } else {
            binding.tilPassword.error = null
            valid = true
        }
        return valid
    }

    private fun isDetailsNotEmpty(
        signUpEmail: String,
        signUpName: String,
        signUpPassword: String
    ): Boolean {
        var valid = true
        if (TextUtils.isEmpty(signUpEmail)) {
            binding.etEmailSignUp.error = "Email cannot be empty"
            valid = false
        }

        if (TextUtils.isEmpty(signUpName)) {
            binding.etNameSignUp.error = "Name cannot be empty"
            valid = false
        }

        if (TextUtils.isEmpty(signUpPassword)) {
            binding.etPasswordSignUp.error = "Password cannot be empty"
            valid = false
        }

        return valid
    }

    private fun signUpWithFireBase(userEmail: String, userPassword: String, userName: String) {

        binding.pbProgressSignup.visibility = View.VISIBLE
        binding.btSignUp.isClickable = false
        auth.createUserWithEmailAndPassword(userEmail, userPassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    sendVerificationEmail(userEmail, userName)
                } else {
                    Toast.makeText(
                        applicationContext,
                        task.exception?.localizedMessage, Toast.LENGTH_SHORT,
                    ).show()
                    binding.pbProgressSignup.visibility = View.GONE
                    binding.btSignUp.isClickable = true
                }
            }
    }

    private fun addUserToDatabase(userId: String, userName: String, userEmail: String) {
        val user = UsersEntity(userId, userName, userEmail)
        myReference.child(userId).setValue(user)
        auth.signOut()
        finish()
    }

    private fun sendVerificationEmail(userEmail: String, userName: String) {
        val currentUser = auth.currentUser
        currentUser!!.sendEmailVerification()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        applicationContext,
                        "Registered please verify email",
                        Toast.LENGTH_LONG
                    ).show()
                    val userId = currentUser.uid
                    addUserToDatabase(userId, userName, userEmail)

                } else {
                    Toast.makeText(this, "Error, Please register again", Toast.LENGTH_LONG).show()
                }
            }
    }
}