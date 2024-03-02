package com.dtu.deltecdtu.Filter

import android.widget.Filter
import com.dtu.deltecdtu.adapter.HolidayAdapter
import com.dtu.deltecdtu.model.ModelHoliday


class FilterHolidays(
    private var filterList: List<ModelHoliday>, private var holidayAdapter: HolidayAdapter
) : Filter() {
    override fun performFiltering(words: CharSequence?): FilterResults {
        var givenWord = words
        val results = FilterResults()

        if (!givenWord.isNullOrEmpty()) {
            givenWord = givenWord.toString().uppercase()
            val filterModels = mutableListOf<ModelHoliday>()
            for (i in filterList.indices) {

                if (isContains(filterList, i, givenWord)) {
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

    private fun isContains(filterList: List<ModelHoliday>, i: Int, givenWord: String): Boolean {
        if (filterList[i].holiday.uppercase().contains(givenWord)
            || filterList[i].holidayDescription.contains(givenWord)
            || filterList[i].day.uppercase().contains(givenWord)
            || filterList[i].sakaMonth.uppercase().contains(givenWord)
            || filterList[i].month.uppercase().contains(givenWord)
            || filterList[i].date.toString().uppercase().contains(givenWord)
        ) {
            return true
        }
        return false
    }
    override fun publishResults(words: CharSequence?, results: FilterResults?) {
        holidayAdapter.holidayList = results?.values as List<ModelHoliday>
    }
}