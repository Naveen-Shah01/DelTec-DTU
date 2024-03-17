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
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dtu.deltecdtu.R
import com.dtu.deltecdtu.SubListNoticeClickListener
import com.dtu.deltecdtu.adapter.SubListAdapter
import com.dtu.deltecdtu.databinding.FragmentSubListBinding
import com.dtu.deltecdtu.model.SubListModel
import com.dtu.deltecdtu.util.AndroidDownloader
import com.dtu.deltecdtu.util.Utility
import com.example.deltecdtu.Util.Constants
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class SubListFragment : Fragment(), SubListNoticeClickListener {
    private var _binding: FragmentSubListBinding? = null
    private val binding get() = _binding!!
    private val sublistArrayLiveData = MutableLiveData<List<SubListModel>>()
    private lateinit var subListAdapter: SubListAdapter

   val args: SubListFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSubListBinding.inflate(inflater, container, false)
        sublistArrayLiveData.value = emptyList()

        initToolbar()

        setupRecyclerView()
        val noticeModel = args.subList.notice
        binding.tvMainHeading.text = noticeModel!!.name
        val list = noticeModel.subList



        if (list != null) {
            val newList = list.map { items ->
                val name = items.name
                val url = items.link
                val type = Utility.getFileTypeFromUrl(url!!)
                val isDownloaded = Utility.isDownloaded(requireContext(), url)
                SubListModel(name = name, url = url, type = type, isDownloaded = isDownloaded)
            }
            sublistArrayLiveData.value = newList
        }

        sublistArrayLiveData.observe(viewLifecycleOwner, Observer { newList ->
            for (item in newList) {
                val url = item.url
                val type = item.type
                val isDownloaded = item.isDownloaded
                if (isDownloaded) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val bitmap = Utility.generateBitmap(requireContext(), url!!, type)
                        withContext(Dispatchers.Main) {
                            if (bitmap != null) {
                                item.bitmap = bitmap
                            }
                        }
                    }
                }
            }
            subListAdapter.differ.submitList(newList)
        })

        return binding.root
    }


    private fun setupRecyclerView() {

        subListAdapter = SubListAdapter(requireContext(), this)
        binding.rvSublistFragmentRecyclerView.adapter = subListAdapter

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
        val id = AndroidDownloader(requireContext()).downloadFromSubListAdapter(
            url,
            position,
            subListAdapter
        )
        Snackbar.make(requireView(), "Downloading $url", Snackbar.LENGTH_LONG).show()
    }

    private fun initToolbar() {
        binding.tbToolbarSubListFragment.setNavigationIcon(R.drawable.back_icon)
        binding.tbToolbarSubListFragment.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }
}