package com.dtu.deltecdtu.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dtu.deltecdtu.NoticeClickListener
import com.dtu.deltecdtu.Filter.FilterNotices
import com.dtu.deltecdtu.Util.Utility
import com.dtu.deltecdtu.databinding.ItemNoticeBinding
import com.dtu.deltecdtu.model.ExtendedNoticeModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DTUNoticeAdapter(
    private val context: Context,
    private val noticeClickListener: NoticeClickListener
) :
    RecyclerView.Adapter<DTUNoticeAdapter.NoticeViewHolder>(), Filterable {

    private var filter: FilterNotices? = null

    inner class NoticeViewHolder(private val adapterBinding: ItemNoticeBinding) :
        RecyclerView.ViewHolder(adapterBinding.root) {
        val tvTitle = adapterBinding.tvNoticeTitle
        val tvLink = adapterBinding.tvNoticeLink
        val tvFileName = adapterBinding.tvFileName
        val btReadMore = adapterBinding.btReadMore
        val sivThumbnail = adapterBinding.sivThumbnail
        val cdThumbnail = adapterBinding.cdThumbnail
        val cdDownload = adapterBinding.cdDownload

        init {

            tvLink.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val url = noticesList[position].noticeModel.notice?.link ?: ""
                    noticeClickListener.linkLongClick(url, tvLink)
                    true
                } else {
                    false
                }
            }

            tvLink.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val url = noticesList[position].noticeModel.notice?.link ?: ""
                    noticeClickListener.linkClick(url)
                }
            }

            cdThumbnail.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val url = noticesList[position].noticeModel.notice?.link ?: ""
                    noticeClickListener.onThumbnailItemClick(url)
                }
            }

            btReadMore.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val model = noticesList[position]
                    noticeClickListener.onReadMoreClick(position, model.noticeModel)
                }
            }
            cdDownload.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val url = noticesList[position].noticeModel.notice?.link ?: ""
                    noticeClickListener.onDownloadClick(position, url,cdDownload)
                }
            }
        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<ExtendedNoticeModel>() {
        override fun areItemsTheSame(
            oldItem: ExtendedNoticeModel,
            newItem: ExtendedNoticeModel
        ): Boolean {
            return oldItem.noticeModel.id == newItem.noticeModel.id
        }

        override fun areContentsTheSame(
            oldItem: ExtendedNoticeModel,
            newItem: ExtendedNoticeModel
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
    var noticesList: List<ExtendedNoticeModel>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }
    var filterNoticeList: List<ExtendedNoticeModel> = emptyList()
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
        val currentNotice = noticesList[position]

        val newsName = currentNotice.noticeModel.notice?.name
        val newsLink = currentNotice.noticeModel.notice?.link ?: "None"
        val isDownloaded = currentNotice.isDownloaded
        val type = currentNotice.type
        var bitmap = currentNotice.bitmap
        holder.tvTitle.text = newsName
        if (newsLink.isNotEmpty() && newsLink != "None") {
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

        } else if (newsLink == "None" || newsLink.isEmpty()) {
            holder.tvLink.visibility = View.GONE
            holder.cdThumbnail.visibility = View.GONE
            holder.cdDownload.visibility = View.GONE
        }

        val sublist = currentNotice.noticeModel.notice?.subList
        if (sublist != null && sublist.isNotEmpty()) {
            holder.btReadMore.visibility = View.VISIBLE
        } else {
            holder.btReadMore.visibility = View.GONE
        }
    }

    override fun getFilter(): Filter {
        if (filter == null) {
            filter = FilterNotices(filterNoticeList, this)
        }
        return filter as FilterNotices
    }
}











