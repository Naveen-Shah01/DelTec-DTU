package com.dtu.deltecdtu.fragments.holidays

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.dtu.deltecdtu.R
import com.dtu.deltecdtu.adapter.HolidayViewPagerAdapter
import com.dtu.deltecdtu.databinding.DialogHolidayNoticeBinding
import com.dtu.deltecdtu.databinding.FragmentHolidayMainBinding
import com.dtu.deltecdtu.databinding.LogoutDialogBinding
import com.dtu.deltecdtu.util.AndroidDownloader
import com.dtu.deltecdtu.viewmodel.HolidayViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class HolidayMainFragment : Fragment() {
    private val tabTitles = listOf("2024", "2023")
    private val fragments = listOf(TwoZeroTwoFourFragment(), TwoZeroTwoThreeFragment())

    //5.
    private val holiday2024Url = "http://dtu.ac.in/Web/notice/2023/dec/file1234.pdf"
    private val holiday2023Url = "http://dtu.ac.in/Web/notice/2022/dec/file1258.pdf"

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
                    showDownloadNotice()
                    true
                }

                else -> false
            }
        }
    }

    private fun showDownloadNotice() {
        val customDialog = Dialog(requireContext())
        val dialogBinding = DialogHolidayNoticeBinding.inflate(layoutInflater)
        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(true)
        dialogBinding.cl2024.setOnClickListener {
            AndroidDownloader(requireContext()).downloadPdf(
                holiday2024Url, "Holiday Notice 2024.pdf"
            )
            Snackbar.make(
                requireView(), "Downloading 2024 Holiday notice....", Snackbar.LENGTH_LONG
            ).show()
            customDialog.dismiss()
        }
        dialogBinding.cl2023.setOnClickListener {
            AndroidDownloader(requireContext()).downloadPdf(
                holiday2023Url, "Holiday Notice 2023.pdf"
            )
            Snackbar.make(
                requireView(), "Downloading 2023 Holiday notice....", Snackbar.LENGTH_LONG
            ).show()
            customDialog.dismiss()
        }
        customDialog.show()
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

