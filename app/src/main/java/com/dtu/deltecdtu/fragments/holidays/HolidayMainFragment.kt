package com.dtu.deltecdtu.fragments.holidays

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.dtu.deltecdtu.R
import com.dtu.deltecdtu.adapter.HolidayViewPagerAdapter
import com.dtu.deltecdtu.databinding.FragmentHolidayMainBinding
import com.dtu.deltecdtu.viewmodel.HolidayViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator




//TODO change the background of image based on image of holiday

class HolidayMainFragment : Fragment() {
    private val tabTitles = listOf("2024", "2023")
    private val fragments = listOf(TwoZeroTwoFourFragment(), TwoZeroTwoThreeFragment())


    private val searchViewModel: HolidayViewModel by activityViewModels()
    private var _binding: FragmentHolidayMainBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHolidayMainBinding.inflate(inflater, container, false)

        initToolbar()
        initViewPager()
        initTabLayout()

        //TO disable the swiping between pages
        binding.viewPagerHoliday.isUserInputEnabled = false

        binding.tabLayoutHoliday.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
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
        val menuItem = binding.noticesToolbar.menu.findItem(R.id.holiday_search)
        menuItem.collapseActionView()
    }


    private fun initToolbar() {
        binding.noticesToolbar.setNavigationIcon(R.drawable.back_icon_black)
        binding.noticesToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.noticesToolbar.inflateMenu(R.menu.holiday_menu)
        binding.noticesToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.holiday_search -> {
                    val searchView = it.actionView as SearchView
                    searchView.queryHint = "Search in holidays...."
                    startSearching(searchView)
                    true
                }
                R.id.downloadHolidayNotice -> {
                    //TODO download holiday notice structure
                    //TODO show dialog of two fee holiday notice 2023,2024
//                    downloadFreeStructure
                    true
                }
                else -> false
            }
        }
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

    private fun initViewPager() {
        val pagerAdapter = HolidayViewPagerAdapter(requireActivity(), fragments)
        binding.viewPagerHoliday.adapter = pagerAdapter
    }

    private fun initTabLayout() {
        TabLayoutMediator(binding.tabLayoutHoliday, binding.viewPagerHoliday) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

    override fun onStop() {
        super.onStop()
        val menuItem = binding.noticesToolbar.menu.findItem(R.id.holiday_search)
        menuItem.collapseActionView()
    }
}

