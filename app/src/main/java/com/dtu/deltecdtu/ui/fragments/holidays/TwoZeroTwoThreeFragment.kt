package com.dtu.deltecdtu.ui.fragments.holidays

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.dtu.deltecdtu.HolidayItemClickListener
import com.dtu.deltecdtu.R
import com.dtu.deltecdtu.adapter.HolidayAdapter
import com.dtu.deltecdtu.databinding.FragmentTwoZeroTwoThreeBinding
import com.dtu.deltecdtu.model.ModelHoliday
import com.dtu.deltecdtu.util.Response
import com.dtu.deltecdtu.viewmodel.HolidayViewModel

class TwoZeroTwoThreeFragment : Fragment(), HolidayItemClickListener {
    private var _binding: FragmentTwoZeroTwoThreeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HolidayViewModel by activityViewModels()
    private lateinit var holidayAdapter: HolidayAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTwoZeroTwoThreeBinding.inflate(inflater, container, false)

        setupRecyclerView()
        monitorHolidayData()

        return binding.root
    }


    private fun setupRecyclerView() {
        holidayAdapter = HolidayAdapter(this)
        binding.rvRecyclerviewListOfHolidays2023.adapter = holidayAdapter
    }

    override fun onResume() {
        super.onResume()
        viewModel.query.observe(viewLifecycleOwner, Observer { query ->
                holidayAdapter.filter.filter(query)
        })
    }

    override fun onPause() {
        super.onPause()
        viewModel.query.removeObservers(viewLifecycleOwner)
    }

    private fun monitorHolidayData() {
        viewModel.holidaysLiveData2023.observe(viewLifecycleOwner, Observer { Resource ->
            when (Resource) {
                is Response.Success -> handleSuccess(Resource.data)
                is Response.Error -> handleError(Resource.message)
                is Response.Loading -> showProgressBar()
            }
        })
    }

    private fun handleSuccess(data: List<ModelHoliday>?) {
        hideProgressBar()
        data?.let { holidayResponse ->
            holidayAdapter.differ.submitList(holidayResponse)
            holidayAdapter.filterHolidayList = holidayResponse
        }
    }

    private fun handleError(message: String?) {
        hideProgressBar()
        message?.let {
            Toast.makeText(activity, "An Error Occurred: $message", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onHolidayClicked(modelHoliday: ModelHoliday) {
        val bundle = Bundle().apply {
            putParcelable("holiday", modelHoliday)
        }
        findNavController().navigate(R.id.action_holidayMainFragment3_to_holidayDetailFragment, bundle)
    }


    private fun hideProgressBar() {
        binding.pbProgressBar.visibility = View.GONE
    }

    private fun showProgressBar() {
        binding.pbProgressBar.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}









