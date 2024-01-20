package com.dtu.deltecdtu.fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.dtu.deltecdtu.R
import com.dtu.deltecdtu.adapter.ViewPagerAdapter
import com.dtu.deltecdtu.authentication.LoginActivity
import com.dtu.deltecdtu.databinding.FragmentMainBinding
import com.dtu.deltecdtu.databinding.LogoutDialogBinding
import com.dtu.deltecdtu.fragments.notices.EventsFragment
import com.dtu.deltecdtu.fragments.notices.FirstYearFragment
import com.dtu.deltecdtu.fragments.notices.JobsFragment
import com.dtu.deltecdtu.fragments.notices.LatestNewsFragment
import com.dtu.deltecdtu.fragments.notices.NoticesFragment
import com.dtu.deltecdtu.fragments.notices.TendersFragment
import com.dtu.deltecdtu.viewmodel.SearchViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth

class MainFragment : Fragment() {
    private val searchViewModel: SearchViewModel by activityViewModels()

    private val tabTitles = listOf("Latest", "Notices", "First Year", "Events", "Tenders", "Jobs")
    private val fragments =
        listOf(
            LatestNewsFragment(),
            NoticesFragment(),
            FirstYearFragment(),
            EventsFragment(),
            TendersFragment(),
            JobsFragment()
        )

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        initToolbar()
        initViewPager()
        initTabLayout()


        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                collapseSearchView()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })


        return binding.root
    }


    private fun collapseSearchView() {
        val menuItem = binding.noticesToolbar.menu.findItem(R.id.notice_search)
        menuItem.collapseActionView()
    }

    private fun initViewPager() {
        val pagerAdapter = ViewPagerAdapter(requireActivity(), fragments)
        binding.viewPager.adapter = pagerAdapter
    }

    private fun initTabLayout() {
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

    private fun initToolbar() {
        binding.noticesToolbar.inflateMenu(R.menu.notice_search_menu)
        binding.noticesToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.notice_search -> {
                    val searchView = it.actionView as SearchView
                    searchView.queryHint = "Filter notices...."
                    startSearching(searchView)
                    true
                }
                R.id.logOut->{
                    customDialogForLogout()
                    true
                }
                else -> false
            }
        }
    }

    private fun customDialogForLogout() {
        val customDialog = Dialog(requireContext())
        val dialogBinding = LogoutDialogBinding.inflate(layoutInflater)
        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(true)
        dialogBinding.btLogOutConfirm.setOnClickListener{
            Logout()
            customDialog.dismiss()
        }
        dialogBinding.btLogOutCancel.setOnClickListener{
            customDialog.dismiss()
        }
        customDialog.show()
    }

    private fun Logout() {

        // Initialize sign in client
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), GoogleSignInOptions.DEFAULT_SIGN_IN)
        // Sign out from google
        googleSignInClient.signOut().addOnCompleteListener { task ->
            // Check condition
            if (task.isSuccessful) {
                // When task is successful sign out from firebase
                FirebaseAuth.getInstance().signOut()
                // Display Toast
                Toast.makeText(requireContext(),"Logged Out",Toast.LENGTH_SHORT).show()
                startLoginActivity()
            }
        }


    }

    private fun startLoginActivity() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun startSearching(searchView: SearchView) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchViewModel.setQuery(newText)
                return false
            }
        })
    }


    override fun onStop() {
        super.onStop()
        val menuItem = binding.noticesToolbar.menu.findItem(R.id.notice_search)
        menuItem.collapseActionView()
    }
}