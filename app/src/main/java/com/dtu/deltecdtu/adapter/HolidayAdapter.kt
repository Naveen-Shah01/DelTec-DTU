package com.dtu.deltecdtu.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dtu.deltecdtu.filter.FilterHolidays
import com.dtu.deltecdtu.HolidayItemClickListener
import com.dtu.deltecdtu.databinding.ListItemHolidaysBinding
import com.dtu.deltecdtu.model.ModelHoliday
import com.dtu.deltecdtu.util.Utility

class HolidayAdapter(
    private val onHolidayItemClickListener: HolidayItemClickListener
) : RecyclerView.Adapter<HolidayAdapter.HolidayViewHolder>(), Filterable {

    private var filter: FilterHolidays? = null

    inner class HolidayViewHolder(adapterBinding: ListItemHolidaysBinding) :
        RecyclerView.ViewHolder(adapterBinding.root) {
        val tvMonth = adapterBinding.tvMonth
        val tvYear = adapterBinding.tvYear
        val tvDate = adapterBinding.tvDate
        val tvDay = adapterBinding.tvDay
        val tvHoliday = adapterBinding.tvHoliday
        val item = adapterBinding.root

        init {
            item.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onHolidayItemClickListener.onHolidayClicked(holidayList[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolidayViewHolder {
        return HolidayViewHolder(
            ListItemHolidaysBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    private val differCallback = object : DiffUtil.ItemCallback<ModelHoliday>() {
        override fun areItemsTheSame(
            oldItem: ModelHoliday, newItem: ModelHoliday
        ): Boolean {
            return oldItem.dateId == newItem.dateId
        }

        override fun areContentsTheSame(
            oldItem: ModelHoliday, newItem: ModelHoliday
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
    var holidayList: List<ModelHoliday>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }

    var filterHolidayList: List<ModelHoliday> = emptyList()

    override fun getItemCount(): Int {
        return holidayList.size
    }

    override fun onBindViewHolder(holder: HolidayViewHolder, position: Int) {
        val currentHoliday = holidayList[position]
        val holidayName = currentHoliday.holiday
        val date = Utility.getSuitableDate(currentHoliday.date!!)
        val month = currentHoliday.month
        val day = currentHoliday.day
        val year = currentHoliday.year
        holder.tvMonth.text = month
        holder.tvYear.text = year.toString()
        holder.tvDate.text = date
        holder.tvDay.text = day
        holder.tvHoliday.text = holidayName
    }

    override fun getFilter(): Filter {
        if (filter == null) {
            filter = FilterHolidays(filterHolidayList, this)
        }
        return filter as FilterHolidays
    }

}