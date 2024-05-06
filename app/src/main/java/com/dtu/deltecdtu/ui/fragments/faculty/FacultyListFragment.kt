package com.dtu.deltecdtu.ui.fragments.faculty

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.dtu.deltecdtu.FacultyItemClickListener
import com.dtu.deltecdtu.R
import com.dtu.deltecdtu.adapter.FacultyAdapter
import com.dtu.deltecdtu.databinding.FragmentFacultyListBinding
import com.dtu.deltecdtu.model.ModelFaculty
import com.dtu.deltecdtu.util.Response
import com.dtu.deltecdtu.viewmodel.FacultyViewModel


class FacultyListFragment : Fragment(), FacultyItemClickListener {
    //TODO click on email icon to show email dialog mail and set mail and send email to this dialog

    private var _binding: FragmentFacultyListBinding? = null
    private val binding get() = _binding!!
    private lateinit var facultyAdapter: FacultyAdapter
    private val facultyViewModel: FacultyViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFacultyListBinding.inflate(inflater, container, false)
        initToolbar()
        setupRecyclerView()
        monitorFacultyData()
        return binding.root
    }

    private fun initToolbar() {
        binding.tbFacultyToolbar.setNavigationIcon(R.drawable.back_icon)
        binding.tbFacultyToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.tbFacultyToolbar.inflateMenu(R.menu.faculty_menu)
        binding.tbFacultyToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.faculty_search -> {
                    val searchView = it.actionView as SearchView
                    searchView.queryHint = "Search Faculty...."
                    startSearching(searchView)
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
                facultyAdapter.filter.filter(newText)
                return false
            }
        })
    }

    private fun monitorFacultyData() {
        facultyViewModel.facultyLiveData.observe(viewLifecycleOwner, Observer { Response ->
            when (Response) {
                is Response.Success -> handleSuccess(Response.data)
                is Response.Error -> handleError(Response.message)
                is Response.Loading -> showProgressBar()
            }
        })
    }

    private fun handleSuccess(data: List<ModelFaculty>?) {
        hideProgressBar()
        data?.let { facultyResponse ->
            facultyAdapter.differ.submitList(facultyResponse)
            facultyAdapter.filterFacultyList = facultyResponse
        }
    }

    private fun handleError(message: String?) {
        hideProgressBar()
        message?.let {
            Toast.makeText(activity, "An Error Occurred: $message", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        facultyAdapter = FacultyAdapter(this)
        binding.apply {
            rvRecyclerviewFaculty.adapter = facultyAdapter
        }
    }

    override fun onFacultyClicked(
        modelFaculty: ModelFaculty
    ) {
        val directions =
            FacultyListFragmentDirections.actionFacultyListFragmentToFacultyDetailFragment(modelFaculty)
        findNavController().navigate(directions)
        closeSearchView()
    }

    private fun closeSearchView() {
        binding.tbFacultyToolbar.collapseActionView()
        binding.tbFacultyToolbar.clearFocus()
    }

    override fun onFacultyEmailPopDialogClicked(email: String?, alternateEmail: String?) {
        showCustomDialog(email,alternateEmail)
    }

    private fun showCustomDialog(email: String?, alternateEmail: String?) {
        val dialogFragment = FacultyEmailDialogFragment()
        val bundle = Bundle().apply {
            putString("email", email)
            putString("alternateEmail", alternateEmail)
        }
        dialogFragment.arguments = bundle

        dialogFragment.show(parentFragmentManager, "EmailDialogFragment")
    }

    private fun hideProgressBar() {
        binding.pbProgressBar.visibility = View.GONE
    }

    private fun showProgressBar() {
        binding.pbProgressBar.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}