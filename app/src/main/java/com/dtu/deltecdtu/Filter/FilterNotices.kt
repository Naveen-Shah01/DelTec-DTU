package com.dtu.deltecdtu.Filter

import android.widget.Filter
import com.dtu.deltecdtu.adapter.DTUNoticeAdapter
import com.dtu.deltecdtu.model.ExtendedNoticeModel


class FilterNotices(
    private var filterList: List<ExtendedNoticeModel>,
    private var noticeAdapter: DTUNoticeAdapter
) :
    Filter() {
    override fun performFiltering(words: CharSequence?): FilterResults {
        var givenWord = words
        val results = FilterResults()

        if (givenWord != null && givenWord.isNotEmpty()) {
            givenWord = givenWord.toString().uppercase()
            val filterModels = mutableListOf<ExtendedNoticeModel>()
            for (i in 0 until filterList.size) {
                if (filterList[i].noticeModel.notice!!.name!!.uppercase().contains(givenWord)) {
                    filterModels.add(filterList[i])
                }
            }
            results.count = filterModels.size
            results.values = filterModels
        } else {
            results.count = filterList.size
            results.values = filterList
        }
        return results
    }
    override fun publishResults(words: CharSequence?, results: FilterResults?) {
        noticeAdapter.noticesList = results?.values as List<ExtendedNoticeModel>
    }
}