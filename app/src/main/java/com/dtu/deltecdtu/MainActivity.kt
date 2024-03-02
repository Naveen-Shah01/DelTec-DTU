package com.dtu.deltecdtu

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
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
import com.dtu.deltecdtu.databinding.ActivityMainBinding
import com.dtu.deltecdtu.model.UsersEntity
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
import timber.log.Timber


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
                R.id.holidayMainFragment, R.id.holidayDetailFragment -> {
                    toolbar.visibility = View.GONE
                    lockDrawer()
                }

                else -> {
                    toolbar.visibility = View.VISIBLE
                    lockDrawer()
                }
            }
        }

        retrieveData()
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
            if (task.isSuccessful) {
                Timber.tag("MainActivity").e("Subscribed you will receive notifications")
            } else {
                subscribeToTopic()
            }
        }
    }


    private fun retrieveData() {
        val userId = auth.currentUser!!.uid
        userEmail = auth.currentUser!!.email.toString()

        Timber.tag("MainActivity").e("retrieving..........")

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