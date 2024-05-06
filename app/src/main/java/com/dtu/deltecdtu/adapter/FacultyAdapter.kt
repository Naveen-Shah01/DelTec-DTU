package com.dtu.deltecdtu.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dtu.deltecdtu.FacultyItemClickListener
import com.dtu.deltecdtu.databinding.ListItemFacultyBinding
import com.dtu.deltecdtu.filter.FilterFaculty
import com.dtu.deltecdtu.model.ModelFaculty
import com.dtu.deltecdtu.util.Utility.getGlideOptions



class FacultyAdapter(
    private val onFacultyItemClickListener: FacultyItemClickListener
) : RecyclerView.Adapter<FacultyAdapter.FacultyViewHolder>(), Filterable {

    private var filter: FilterFaculty? = null

    inner class FacultyViewHolder(adapterBinding: ListItemFacultyBinding) :
        RecyclerView.ViewHolder(adapterBinding.root) {
        val tvProfName = adapterBinding.tvProfName
        val tvProfDetail = adapterBinding.tvProfDetail
        val tvProfessorDesignation = adapterBinding.tvProfessorDesignation
        val profImage = adapterBinding.sivProfImage
        private val cdFacultyEmail = adapterBinding.cdFacultyEmail
        val item = adapterBinding.root
        init {
            item.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onFacultyItemClickListener.onFacultyClicked(facultyList[position])
                }
            }
            cdFacultyEmail.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onFacultyItemClickListener.onFacultyEmailPopDialogClicked(  facultyList[position].email,
                        facultyList[position].alternateEmail)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FacultyViewHolder {
        return FacultyViewHolder(
            ListItemFacultyBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    private val differCallback = object : DiffUtil.ItemCallback<ModelFaculty>() {
        override fun areItemsTheSame(
            oldItem: ModelFaculty, newItem: ModelFaculty
        ): Boolean {
            return oldItem.facultyId == newItem.facultyId
        }

        override fun areContentsTheSame(
            oldItem: ModelFaculty, newItem: ModelFaculty
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
    var facultyList: List<ModelFaculty>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }

    var filterFacultyList: List<ModelFaculty> = emptyList()

    override fun getItemCount(): Int {
        return facultyList.size
    }

    override fun onBindViewHolder(holder: FacultyViewHolder, position: Int) {
        val currentFaculty = facultyList[position]
        val profName = currentFaculty.name
        val detail = currentFaculty.specialization
        val designation = currentFaculty.designation
        val profImageUrl = currentFaculty.profImageUrl
        holder.tvProfName.text = profName
        holder.tvProfDetail.text = detail ?:"No information available"
        holder.tvProfessorDesignation.text = designation
        //setting transition name for images for shared element transition
        holder.profImage.transitionName = "profImage${currentFaculty.facultyId}"
        holder.tvProfName.transitionName="profName${currentFaculty.facultyId}"
        val options = getGlideOptions()
        Glide.with(holder.profImage.context).load(profImageUrl).apply(options).into(holder.profImage)
    }

    override fun getFilter(): Filter {
        if (filter == null) {
            filter = FilterFaculty(filterFacultyList, this)
        }
        return filter as FilterFaculty
    }

}