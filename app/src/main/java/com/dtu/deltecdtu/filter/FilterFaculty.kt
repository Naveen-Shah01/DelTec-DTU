package com.dtu.deltecdtu.filter

import android.widget.Filter
import com.dtu.deltecdtu.adapter.FacultyAdapter
import com.dtu.deltecdtu.model.ModelFaculty

class FilterFaculty(
    private var filterList: List<ModelFaculty>, private var facultyAdapter: FacultyAdapter
) : Filter() {
    override fun performFiltering(words: CharSequence?): FilterResults {
        var givenWord = words
        val results = FilterResults()
        if (!givenWord.isNullOrEmpty()) {
            givenWord = givenWord.toString().uppercase()
            val filterModels = mutableListOf<ModelFaculty>()
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

    private fun isContains(filterList: List<ModelFaculty>, i: Int, givenWord: String): Boolean {
        val name = filterList[i].name
        val designation = filterList[i].designation
        val department = filterList[i].department
        if ((name != null && name.uppercase().contains(givenWord)) ||
            (designation != null && designation.uppercase().contains(givenWord))
            || (department != null && department.uppercase().contains(givenWord))
        ) {
            return true
        }
        return false
    }

    override fun publishResults(words: CharSequence?, results: FilterResults?) {
        facultyAdapter.facultyList = results?.values as List<ModelFaculty>
    }
}