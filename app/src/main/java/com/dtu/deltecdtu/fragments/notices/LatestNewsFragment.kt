package com.dtu.deltecdtu.fragments.notices

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dtu.deltecdtu.NoticeClickListener
import com.dtu.deltecdtu.R
import com.dtu.deltecdtu.util.Response
import com.dtu.deltecdtu.util.Utility
import com.dtu.deltecdtu.adapter.DTUNoticeAdapter
import com.dtu.deltecdtu.databinding.FragmentLatestNewsBinding
import com.dtu.deltecdtu.model.ExtendedNoticeModel
import com.dtu.deltecdtu.model.NoticeModel
import com.dtu.deltecdtu.viewmodel.SearchViewModel
import com.example.deltecdtu.Util.Constants
import com.dtu.deltecdtu.viewmodel.LatestNewsViewModel
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class LatestNewsFragment : Fragment(), NoticeClickListener {

    lateinit var viewModel: LatestNewsViewModel
    private val searchViewModel: SearchViewModel by activityViewModels()

    private lateinit var dtuNoticeAdapter: DTUNoticeAdapter

    private var _binding: FragmentLatestNewsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLatestNewsBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(this)[LatestNewsViewModel::class.java]

        setupRecyclerView()

        viewModel.fetchLatestNews()
        viewModel.latestNewsLiveData.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Response.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        setBitMapUtilFunction(newsResponse)
                        dtuNoticeAdapter.differ.submitList(newsResponse)
                        dtuNoticeAdapter.filterNoticeList = newsResponse
                    }
                }

                is Response.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Toast.makeText(activity, "An Error Occurred : $message", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                is Response.Loading -> {
                    showProgressBar()
                }
            }
        })
        return binding.root
    }

    private fun setBitMapUtilFunction(newsResponse: List<ExtendedNoticeModel>) {
        for (item in newsResponse) {
            val url = item.noticeModel.notice?.link ?: ""
            var isDownloaded = false
            if (url.isNotEmpty() && url != "") {
                isDownloaded = Utility.isDownloaded(requireContext(), url)
            }

            val type = item.type
            item.isDownloaded = isDownloaded
            if (isDownloaded) {
                CoroutineScope(Dispatchers.IO).launch {
                    val bitmap = Utility.generateBitmap(requireContext(), url, type)
                    withContext(Dispatchers.Main) {
                        if (bitmap != null) {
                            item.bitmap = bitmap
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        searchViewModel.query.observe(viewLifecycleOwner, Observer { query ->
            dtuNoticeAdapter.filter.filter(query)
        })
    }

    override fun onPause() {
        super.onPause()
        searchViewModel.query.removeObservers(viewLifecycleOwner)
    }

    private fun hideProgressBar() {
        binding.pbProgressBar.visibility = View.GONE
    }

    private fun showProgressBar() {
        binding.pbProgressBar.visibility = View.VISIBLE
    }

    private fun setupRecyclerView() {
        dtuNoticeAdapter = DTUNoticeAdapter(requireContext(), this)
        binding.rvLatestFragmentRecyclerView.adapter = dtuNoticeAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onReadMoreClick(position: Int, model: NoticeModel) {
        val bundle = Bundle().apply {
            putSerializable("subList", model)
        }
        findNavController().navigate(R.id.action_mainFragment_to_subListFragment, bundle)
    }

    override fun onThumbnailItemClick(url: String) {

        val directory = requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        val filename = Utility.getFileNameFromUrl(url)
        val filePath = File(directory, filename).path
        val mimeType = Utility.getMimeType(url)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val file = File(filePath)
            val uri = FileProvider.getUriForFile(
                requireContext(),
                requireContext().applicationContext.packageName + ".provider",
                file
            )
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, mimeType)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            try {
                requireContext().startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(context, "No App viewer installed", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            var intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(Uri.parse(filePath), mimeType)
            intent = Intent.createChooser(intent, "Select App")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            requireContext().startActivity(intent)
        }
    }

    override fun linkLongClick(url: String, tvLink: TextView) {
        val originalColor = tvLink.currentTextColor
        val changedColor = ContextCompat.getColor(requireContext(), R.color.color_one_dark)
        tvLink.setTextColor(changedColor)
        val clipboard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Link", url)
        clipboard.setPrimaryClip(clip)
        Snackbar.make(requireView(), "Link copied to clipboard", Snackbar.LENGTH_SHORT).show()
        lifecycleScope.launch {
            delay(Constants.TIME_GAP_FOR_COPY_LINK)
            withContext(Dispatchers.Main) {
                tvLink.setTextColor(originalColor)
            }
        }

    }

    override fun linkClick(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    override fun onDownloadClick(position: Int, url: String, cdDownload: MaterialCardView) {
        val id = Utility.downloadFile(requireContext(), url, position, dtuNoticeAdapter)
        Snackbar.make(requireView(), "Downloading $url", Snackbar.LENGTH_LONG).show()
    }
}






