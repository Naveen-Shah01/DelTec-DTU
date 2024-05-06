package com.dtu.deltecdtu.ui.fragments.holidays

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.dtu.deltecdtu.R
import com.dtu.deltecdtu.databinding.FragmentHolidayDetailBinding
import com.dtu.deltecdtu.model.ModelHoliday
import com.dtu.deltecdtu.util.Utility
import com.google.android.material.imageview.ShapeableImageView

//4.
class HolidayDetailFragment : Fragment() {
    private var _binding: FragmentHolidayDetailBinding? = null
    private val binding get() = _binding!!
    private val args: HolidayDetailFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHolidayDetailBinding.inflate(inflater, container, false)
        initToolbar()
        val holidayModel = args.holiday
        setValues(holidayModel)

//        binding.sivHolidayImage.setOnClickListener {
//            Toast.makeText(requireContext(), "Image will be enlarged", Toast.LENGTH_SHORT).show()
//        }
        return binding.root
    }

    private fun setValues(holidayModel: ModelHoliday) {
        binding.tvDate.text = Utility.getSuitableDate(holidayModel.date!!)
        val dayMonthYear =
            "${holidayModel.day}, ${holidayModel.month}, ${holidayModel.year.toString()}"
        binding.tvDayMonthYear.text = dayMonthYear
        binding.tvHoliday.text = holidayModel.holiday
        binding.tvHolidayDescription.text = holidayModel.holidayDescription
        binding.tvSakaDate.text = Utility.getSuitableDate(holidayModel.sakaDate!!)
        binding.tvSakaMonth.text = holidayModel.sakaMonth
        val imageUrl = holidayModel.holidayImageLink
        setImageViewBackgroundColor(binding.sivHolidayImage, holidayModel.backgroundColor)

        binding.tvHolidayDescription.movementMethod = ScrollingMovementMethod()
        Glide.with(this).load(imageUrl).apply(
            RequestOptions().placeholder(R.drawable.holiday_image_placeholder)
                .error(R.drawable.broken_image_icon)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
        ).into(binding.sivHolidayImage)
    }

    private fun initToolbar() {
        binding.tbToolbarDetailHoliday.setNavigationIcon(R.drawable.back_icon_black)
        binding.tbToolbarDetailHoliday.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setImageViewBackgroundColor(imageView: ShapeableImageView, colorString: String) {
        if (colorString.isNotEmpty()) {
            val color = Color.parseColor(colorString)
            val colorDrawable = ColorDrawable(color)
            imageView.background = colorDrawable
        }
    }
}