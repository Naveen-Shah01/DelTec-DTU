package com.dtu.deltecdtu.adapter

import android.content.Context

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dtu.deltecdtu.SubListNoticeClickListener
import com.dtu.deltecdtu.util.Utility
import com.dtu.deltecdtu.databinding.ItemNoticeBinding
import com.dtu.deltecdtu.model.SubListModel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SubListAdapter(
    private val context: Context, private val noticeClickListener: SubListNoticeClickListener
) : RecyclerView.Adapter<SubListAdapter.NoticeViewHolder>() {

    inner class NoticeViewHolder(private val adapterBinding: ItemNoticeBinding) :
        RecyclerView.ViewHolder(adapterBinding.root) {
        val tvTitle = adapterBinding.tvNoticeTitle
        val tvLink = adapterBinding.tvNoticeLink
        val btReadMore = adapterBinding.btReadMore
        val sivThumbnail = adapterBinding.sivThumbnail
        val cdThumbnail = adapterBinding.cdThumbnail
        val cdDownload = adapterBinding.cdDownload
        val tvFileName = adapterBinding.tvFileName

        init {
            tvLink.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val url = noticesList[position].url
                    noticeClickListener.linkLongClick(url!!, tvLink)
                    true
                } else {
                    false
                }
            }

            // Set click listener for cdThumbnail
            cdThumbnail.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val url = noticesList[position].url
                    noticeClickListener.onThumbnailItemClick(url!!)
                }
            }

            tvLink.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val url = noticesList[position].url
                    noticeClickListener.linkClick(url!!)
                }
            }

            cdDownload.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val url = noticesList[position].url
                    noticeClickListener.onDownloadClick(position, url!!, cdDownload)
                }
            }

        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<SubListModel>() {
        override fun areItemsTheSame(
            oldItem: SubListModel, newItem: SubListModel
        ): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(
            oldItem: SubListModel, newItem: SubListModel
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
    var noticesList: List<SubListModel>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        return NoticeViewHolder(
            ItemNoticeBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return noticesList.size
    }


    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        holder.btReadMore.visibility = View.GONE
        val currentNotice = noticesList[position]

        val newsName = currentNotice.name
        val newsLink = currentNotice.url
        val isDownloaded = currentNotice.isDownloaded
        val type = currentNotice.type
        var bitmap = currentNotice.bitmap

        holder.tvTitle.text = newsName


        if (newsLink != null && newsLink.isNotEmpty()) {
            holder.tvLink.text = newsLink
            holder.tvLink.visibility = View.VISIBLE
            if (isDownloaded) {
                val filename = Utility.setFilename(newsLink)
                holder.tvFileName.text = filename
                currentNotice.fileName = filename
                holder.cdDownload.visibility = View.GONE
                holder.cdThumbnail.visibility = View.VISIBLE
                if (bitmap != null) {
                    Glide.with(context).load(bitmap).into(holder.sivThumbnail)
                } else {
                    CoroutineScope(Dispatchers.IO).launch {
                        bitmap = Utility.generateBitmap(context, newsLink, type)
                        withContext(Dispatchers.Main) {
                            if (bitmap != null) {
                                currentNotice.bitmap = bitmap
                                Glide.with(context).load(bitmap).into(holder.sivThumbnail)
                            }
                        }
                    }
                }
            } else if (type == "pdf" || type == "image") {
                holder.cdDownload.visibility = View.VISIBLE
                holder.cdThumbnail.visibility = View.GONE
            } else {
                holder.cdDownload.visibility = View.GONE
                holder.cdThumbnail.visibility = View.GONE
            }


        } else if (newsLink == null || newsLink.isEmpty()) {
            holder.tvLink.visibility = View.GONE
            holder.cdThumbnail.visibility = View.GONE
            holder.cdDownload.visibility = View.GONE
        }
    }
}











