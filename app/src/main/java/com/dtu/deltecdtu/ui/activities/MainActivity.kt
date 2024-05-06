package com.dtu.deltecdtu.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.Glide
import com.dtu.deltecdtu.OpenDrawer
import com.dtu.deltecdtu.R
import com.dtu.deltecdtu.databinding.ActivityMainBinding
import com.dtu.deltecdtu.model.UsersEntity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging


class MainActivity : AppCompatActivity(), OpenDrawer {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    private var appBarConfiguration: AppBarConfiguration? = null
    private lateinit var toolbar: Toolbar
    private lateinit var navController: NavController
    private var joined: String = ""
    private lateinit var userEmail: String

    private val myReference: DatabaseReference =
        FirebaseDatabase.getInstance().reference.child("Users")
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()


    private lateinit var binding: ActivityMainBinding
    private var hasNotificationPermissionGranted = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        subscribeToTopic()

        window.statusBarColor = ContextCompat.getColor(this, R.color.color_one_light_two)

        drawerLayout = binding.drawerLayout
        navView = binding.navView

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_host) as NavHostFragment
        navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)

        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)

        NavigationUI.setupWithNavController(binding.navView, navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.mainFragment -> {
                    unlockDrawer()
                    toolbar.visibility = View.GONE

                }

                R.id.subListFragment, R.id.profileFragment, R.id.privacyPolicyFragment, R.id.aboutFragment,
                R.id.holidayMainFragment, R.id.holidayDetailFragment,R.id.facultyListFragment,R.id.facultyDetailFragment -> {
                    toolbar.visibility = View.GONE
                    lockDrawer()
                }
                else -> {
                    toolbar.visibility = View.VISIBLE
                    lockDrawer()
                }
            }
        }
        //asking permission for android 13 and higher
        if (Build.VERSION.SDK_INT >= 33) {
            notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        } else {
            hasNotificationPermissionGranted = true
        }
        retrieveData()
    }



    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            hasNotificationPermissionGranted = isGranted
            if (!isGranted) {
                if (Build.VERSION.SDK_INT >= 33) {
                    if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                        showNotificationPermissionRationale()
                    } else {
                        showSettingDialog()
                    }
                }
            } else { Log.e("Notification","Granted")
            }
        }

    private fun showSettingDialog() {
        MaterialAlertDialogBuilder(this, com.google.android.material.R.style.MaterialAlertDialog_Material3)
            .setTitle("Notification Permission")
            .setMessage("We will send notification of new update on DTU website, Please allow notification permission from settings")
            .setPositiveButton("Ok") { _, _ ->
                val intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showNotificationPermissionRationale() {

        MaterialAlertDialogBuilder(this, com.google.android.material.R.style.MaterialAlertDialog_Material3)
            .setTitle("Alert")
            .setMessage("Notification permission is required, to show notification")
            .setPositiveButton("Ok") { _, _ ->
                if (Build.VERSION.SDK_INT >= 33) {
                    notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }


    private fun lockDrawer() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    private fun unlockDrawer() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_host) as NavHostFragment
        val navController = navHostFragment.navController
        return NavigationUI.navigateUp(navController, drawerLayout)
    }


    private fun subscribeToTopic() {
        Firebase.messaging.subscribeToTopic("News").addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                subscribeToTopic()
            }
        }
    }


    private fun retrieveData() {
        val userId = auth.currentUser!!.uid
        userEmail = auth.currentUser!!.email.toString()
        myReference.child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userDetails = snapshot.getValue(UsersEntity::class.java)
                if (userDetails != null) {
                    val userName = userDetails.userName
                    val profileImageUrl = userDetails.profileImageUrl
                    joined = userDetails.joined
                    val headerView = navView.getHeaderView(0)
                    val sivDrawerProfileImage =
                        headerView.findViewById<ShapeableImageView>(R.id.sivDrawerProfileImage)
                    val tvJoined = headerView.findViewById<TextView>(R.id.tvJoined)
                    val drawerUsername = headerView.findViewById<TextView>(R.id.drawerUsername)
                    val drawerUserEmail = headerView.findViewById<TextView>(R.id.drawerUserEmail)
                    val tvJoinedTitle = headerView.findViewById<TextView>(R.id.tvJoinedTitle)
                    drawerUserEmail.text = userEmail
                    drawerUsername.text = userName
                    if (joined.isNotEmpty()) {
                        tvJoined.text = joined
                        tvJoined.visibility = View.VISIBLE
                        tvJoinedTitle.visibility = View.VISIBLE
                    }
                    if (profileImageUrl.isNotEmpty()) {
                        Glide.with(applicationContext).load(profileImageUrl)
                            .into(sivDrawerProfileImage)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    //9.
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun openDrawer() {
        drawerLayout.open()
    }
}