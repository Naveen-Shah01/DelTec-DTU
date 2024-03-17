package com.dtu.deltecdtu.fragments

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.dtu.deltecdtu.R
import com.dtu.deltecdtu.authentication.LoginActivity
import com.dtu.deltecdtu.databinding.DeleteDialogBinding
import com.dtu.deltecdtu.databinding.DeleteDialogConfirmGoogleBinding
import com.dtu.deltecdtu.databinding.DeleteDialogPasswordBinding
import com.dtu.deltecdtu.databinding.FragmentProfileBinding
import com.dtu.deltecdtu.databinding.LogoutDialogBinding
import com.dtu.deltecdtu.model.UsersEntity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID

//6.
class ProfileFragment : Fragment() {


    private lateinit var galleryActivityResultLauncher: ActivityResultLauncher<Intent>

    private lateinit var googleSignInClient: GoogleSignInClient


    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val myReference: DatabaseReference = database.reference.child("Users")
    private val fireBaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
    private val storageReference: StorageReference = fireBaseStorage.reference
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()


    private var imageName = ""
    private lateinit var userId: String
    private var userEmail: String = ""
    private var profileImageUrl: String = ""
    private var imageUri: Uri? = null
    private var userName: String = ""
    private var joined: String = ""

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private var isDataChanged: Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        initToolbar()
        isDataChanged = false

        registerActivityForResult()
        makeElementsInvisible()
        retrieveData()

        binding.iBtnChooseImage.setOnClickListener {
            chooseImageFromGallery()
        }

        binding.iBtnLogoutProfile.setOnClickListener {
            customDialogForLogout()
        }

        binding.btUpdate.setOnClickListener {
            userName = binding.etProfileUserName.text.toString()
            updateData()
        }

        binding.btDeleteProfile.setOnClickListener {
            val user = auth.currentUser
            if (user != null) {
                val providerData = user.providerData
                for (profile in providerData) {
                    if (profile.providerId == GoogleAuthProvider.PROVIDER_ID) {
                        customDialogForDeleteAccountByGoogle(user)

                    } else if (profile.providerId == EmailAuthProvider.PROVIDER_ID) {
                        customDialogForDeleteAccount(user)
                    }
                }
            }
        }
        return binding.root
    }

    private fun callForDeleteAccountByEmail(password: String, user: FirebaseUser) {

        progressVisibleButtonNotClickable()
        if (password.isNotEmpty() && user.email != null) {
            val email = user.email
            val credential = EmailAuthProvider.getCredential(email!!, password)
            user.reauthenticate(credential).addOnCompleteListener { reAuthTask ->
                if (reAuthTask.isSuccessful) {
                    user.delete().addOnCompleteListener { deleteTask ->
                        if (deleteTask.isSuccessful) {
                            logout(true)
                            progressVisibleButtonNotClickable()
                            startLoginActivity()
                        } else {
                            progressInvisibleButtonClickable()
                            Toast.makeText(
                                requireContext(),
                                "Deletion failed, please try again after some time",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } else {
                    progressInvisibleButtonClickable()
                    Toast.makeText(
                        requireContext(), "Please try again....", Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun makeElementsInvisible() {
        binding.cdProfileView.visibility = View.GONE
        binding.btDeleteProfile.visibility = View.GONE
    }

    private fun makeElementsVisible() {
        binding.cdProfileView.visibility = View.VISIBLE
        binding.btDeleteProfile.visibility = View.VISIBLE
    }

    private fun customDialogForLogout() {
        val customDialog = Dialog(requireContext())
        val dialogBinding = LogoutDialogBinding.inflate(layoutInflater)
        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(true)
        dialogBinding.btLogOutConfirm.setOnClickListener {
            logout(false)
            customDialog.dismiss()
        }
        dialogBinding.btLogOutCancel.setOnClickListener {
            customDialog.dismiss()
        }
        customDialog.show()
    }

    private fun customDialogForDeleteAccountByGoogle(user: FirebaseUser) {
        showWarningDialog(user)
    }

    private fun showWarningDialog(user: FirebaseUser) {
        val customDialog = Dialog(requireContext())
        val dialogBinding = DeleteDialogConfirmGoogleBinding.inflate(layoutInflater)
        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(true)
        dialogBinding.btLogOutConfirm.setOnClickListener {
            progressVisibleButtonNotClickable()
            user.delete().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    progressInvisibleButtonClickable()
                    logout(true)
                    startLoginActivity()
                } else {
                    progressInvisibleButtonClickable()
                    val exception = task.exception
                    if (exception is FirebaseAuthRecentLoginRequiredException) {
                        showDialog()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Please try again after some time....",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
            customDialog.dismiss()
        }
        dialogBinding.btLogOutCancel.setOnClickListener {
            customDialog.dismiss()
        }
        customDialog.show()
    }

    private fun showDialog() {
        val customDialog = Dialog(requireContext())
        val dialogBinding = DeleteDialogBinding.inflate(layoutInflater)
        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(true)
        dialogBinding.btLogoutConfirm.setOnClickListener {
            logout(false)
            customDialog.dismiss()
        }
        dialogBinding.btLogoutCancel.setOnClickListener {
            customDialog.dismiss()
        }
        customDialog.show()
    }

    private fun customDialogForDeleteAccount(user: FirebaseUser) {
        val customDialog = Dialog(requireContext())
        val dialogBinding = DeleteDialogPasswordBinding.inflate(layoutInflater)
        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(true)
        dialogBinding.btDeleteConfirm.setOnClickListener {
            val password = dialogBinding.etPassword.text.toString()
            callForDeleteAccountByEmail(password, user)
            customDialog.dismiss()
        }
        dialogBinding.btDeleteCancel.setOnClickListener {
            customDialog.dismiss()
        }
        customDialog.show()
    }

    private fun logout(accountDeleted: Boolean) {
        googleSignInClient =
            GoogleSignIn.getClient(requireActivity(), GoogleSignInOptions.DEFAULT_SIGN_IN)
        googleSignInClient.signOut().addOnCompleteListener { task ->

            if (task.isSuccessful) {
                auth.signOut()
                if (!accountDeleted) {
                    Toast.makeText(requireContext(), "Logged Out", Toast.LENGTH_SHORT).show()
                    startLoginActivity()
                }
                else {
                    Toast.makeText(requireContext(), "Account Deleted", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun retrieveData() {
        userId = auth.currentUser!!.uid
        userEmail = auth.currentUser!!.email.toString()

        //7.
        myReference.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!isDataChanged) {
                    val user = snapshot.getValue(UsersEntity::class.java)
                    makeElementsVisible()
                    if (user != null) {
                        userName = user.userName
                        binding.etProfileUserName.setText(userName)
                        imageName = user.imageName
                        profileImageUrl = user.profileImageUrl
                        joined = user.joined
                        if (user.profileImageUrl != "") {
                            if(isAdded){
                                Glide.with(this@ProfileFragment).load(user.profileImageUrl)
                                    .into(binding.iBtnChooseImage)
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
                makeElementsInvisible()
                binding.tvErrorText.visibility = View.VISIBLE
            }
        })
    }

    private fun startLoginActivity() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }


    private fun addUserToDatabase() {
        val user = UsersEntity(userId, userName, userEmail, profileImageUrl, imageName, joined)

        isDataChanged = true
        myReference.child(userId).setValue(user).addOnSuccessListener {
            Toast.makeText(
                requireContext(), "Updated successfully...", Toast.LENGTH_SHORT
            ).show()
            progressInvisibleButtonClickable()
        }.addOnFailureListener { e ->
            Toast.makeText(requireContext(), "${e.message}", Toast.LENGTH_LONG).show()
            progressInvisibleButtonClickable()
        }
    }


    private fun updateData() {
        if (imageUri == null && userName.isEmpty() || userName.isEmpty()) {
            return
        }
        progressVisibleButtonNotClickable()

        if (userName.isNotEmpty() && imageUri == null) {
            addUserToDatabase()
        }
        if (imageUri != null && userName.isNotEmpty()) {

            Glide.with(this@ProfileFragment).load(imageUri).into(binding.iBtnChooseImage)

            imageName = UUID.randomUUID().toString()

            val imageReference = storageReference.child("UserImages").child(imageName)
            imageReference.putFile(imageUri!!).addOnSuccessListener {
                imageReference.downloadUrl.addOnSuccessListener { url ->
                    profileImageUrl = url.toString()
                    addUserToDatabase()
                }
            }.addOnFailureListener {
                Toast.makeText(
                    requireContext(), "Error please upload again", Toast.LENGTH_SHORT
                ).show()
                progressInvisibleButtonClickable()
            }
        }
    }


    private fun chooseImageFromGallery() {

        val readImagePermission =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_MEDIA_IMAGES
            else Manifest.permission.READ_EXTERNAL_STORAGE

        if (ContextCompat.checkSelfPermission(
                requireContext(), readImagePermission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            callIntentForImage()
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            val title = "Permission Required"
            val message = "App needs Media Permission to set profile image"

            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            builder.setTitle(title).setMessage(message).setCancelable(false)
                .setPositiveButton("OK") { dialog, _ ->
                    requestGalleryPermissionLauncher.launch(readImagePermission)
                    dialog.dismiss()
                }.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            builder.create().show()

        } else {
            requestGalleryPermissionLauncher.launch(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    private fun callIntentForImage() {
        val getIntent = Intent(Intent.ACTION_GET_CONTENT)
        getIntent.type = "image/*"
        val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        val chooserIntent = Intent.createChooser(getIntent, "Select Image from...")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))
        galleryActivityResultLauncher.launch(chooserIntent)
    }

    private val requestGalleryPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            callIntentForImage()
        } else if (!ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            val title = "Permission required"
            val message =
                "Please allow media permission for images from settings to set profile picture."
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            builder.setTitle(title).setMessage(message).setCancelable(false)
                .setPositiveButton("Change Settings") { _, _ ->
                    try {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", requireActivity().packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        e.printStackTrace()
                    }
                }.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            builder.create().show()
        } else {
            chooseImageFromGallery()
        }
    }


    private fun registerActivityForResult() {
        galleryActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback { result ->
                val resultCode = result.resultCode
                val imageData = result.data
                if (resultCode == RESULT_OK && imageData != null) {
                    imageUri = imageData.data
                    imageUri?.let {
                        Glide.with(this@ProfileFragment).load(it).into(binding.iBtnChooseImage)
                    }
                }
            })
    }

    private fun initToolbar() {
        binding.tbToolbarProfile.setNavigationIcon(R.drawable.back_icon)
        binding.tbToolbarProfile.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun progressVisibleButtonNotClickable() {
        binding.pbProgressBar.visibility = View.VISIBLE
        binding.iBtnLogoutProfile.isClickable = false
        binding.clRoot.setAllEnabled(false)
    }

    private fun progressInvisibleButtonClickable() {
        binding.pbProgressBar.visibility = View.INVISIBLE
        binding.iBtnLogoutProfile.isClickable = true
        binding.clRoot.setAllEnabled(true)
    }

    private fun View.setAllEnabled(enabled: Boolean) {
        isEnabled = enabled
        if (this is ViewGroup) children.forEach { child -> child.setAllEnabled(enabled) }
    }

    override fun onStop() {
        super.onStop()
        isDataChanged = false
    }

    override fun onDestroy() {
        super.onDestroy()
        isDataChanged = false
    }
}