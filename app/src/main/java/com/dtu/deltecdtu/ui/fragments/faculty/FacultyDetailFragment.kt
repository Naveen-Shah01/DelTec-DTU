package com.dtu.deltecdtu.ui.fragments.faculty

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color.WHITE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.dtu.deltecdtu.R
import com.dtu.deltecdtu.databinding.FragmentFacultyDetailBinding
import com.dtu.deltecdtu.model.ModelFaculty
import com.dtu.deltecdtu.ui.activities.FullScreenImageActivity
import com.dtu.deltecdtu.util.Utility.getGlideOptions
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


private const val NO_INFO = "No info"
class FacultyDetailFragment : Fragment() {
    private var _binding: FragmentFacultyDetailBinding? = null
    private val binding get() = _binding!!
    private val args: FacultyDetailFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFacultyDetailBinding.inflate(inflater, container, false)
        initToolBar()
        val facultyModel = args.faculty
        setValues(facultyModel)

        binding.cdQualification.setOnClickListener {
            toggleQualification()
        }

        binding.cdSpecialisation.setOnClickListener {
            toggleSpecialisation()
        }
        binding.tvProfessorName.setOnLongClickListener {
            copyTextToClipboard("name", binding.tvProfessorName)
            true
        }
        binding.tvEmail.setOnLongClickListener {
            copyTextToClipboard("email", binding.tvEmail)
            true
        }
        binding.tvAlternateEmail.setOnLongClickListener {
            copyTextToClipboard("alternateEmail", binding.tvAlternateEmail)
            true
        }
        binding.tvAlternateMobileNumber.setOnLongClickListener {
            copyTextToClipboard("alternatePhone", binding.tvAlternateMobileNumber)
            true
        }
        binding.tvMobileNumber.setOnLongClickListener {
            copyTextToClipboard("phone", binding.tvMobileNumber)
            true
        }

        binding.sivProfessorImage.setOnClickListener {
            if (facultyModel.profImageUrl != null) {
                val intent = Intent(requireContext(), FullScreenImageActivity::class.java)
                intent.putExtra("image", facultyModel.profImageUrl)
                startActivity(intent)
            }
        }
        return binding.root
    }


    private fun setValues(facultyModel: ModelFaculty) {
        binding.apply {
            with(facultyModel) {
                tvProfessorName.text = name
                tvProfessorDesignation.text = designation
                tvProfessorHierarchy.text = hierarchy
                tvDepartmentName.text = department
                tvProfessorQualification.text = qualification ?: "No information available"
                tvProfessorSpecialisation.text = specialization ?: "No information available"
                tvAlternateMobileNumber.text = alternatePhone.toString()
                tvAlternateEmail.text = alternateEmail
                tvEmail.text = email
                tvMobileNumber.text = phone.toString()
                if (alternatePhone == null || alternatePhone.toString().isEmpty()) {
                    llAlternatePhone.visibility = View.GONE
                }
                if (alternateEmail.isNullOrEmpty()) {
                    llAlternateEmail.visibility = View.GONE
                }
                if (hierarchy != designation) View.VISIBLE else View.GONE
                if (email.isNullOrEmpty()) {
                    tvEmail.text = NO_INFO
                    tvEmail.isEnabled = false
                }
                if (phone == null || phone.toString()
                        .isEmpty() || phone.toString() == "9876543210"
                ) {
                    tvMobileNumber.text = NO_INFO
                    tvMobileNumber.isEnabled = false
                }
                tvProfessorHierarchy.visibility =
                    if (hierarchy != designation) View.VISIBLE else View.GONE
                val options = getGlideOptions()
                Glide.with(requireContext()).load(profImageUrl).apply(options)
                    .into(sivProfessorImage)
            }
        }
    }

    private fun initToolBar() {
        binding.tbToolbarFacultyDetail.setNavigationIcon(R.drawable.back_icon)
        binding.tbToolbarFacultyDetail.setNavigationIconTint(WHITE)
        binding.tbToolbarFacultyDetail.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun toggleSpecialisation() {
        if (binding.llSpecialisationHiddenView.visibility == View.VISIBLE) {
            binding.apply {
                llSpecialisationHiddenView.visibility = View.GONE
                clSpecialisationExtensionIcon.animate().rotationBy(-90f).setDuration(240)
                    .setInterpolator(LinearInterpolator())
                    .start()
            }
        } else {
            binding.llSpecialisationHiddenView.visibility = View.VISIBLE
            binding.clSpecialisationExtensionIcon.apply {
                animate().rotationBy(90f).setDuration(240).setInterpolator(LinearInterpolator())
                    .start()
            }
        }
    }


    private fun toggleQualification() {
        if (binding.llQualificationHiddenView.visibility == View.VISIBLE) {
            binding.llQualificationHiddenView.visibility = View.GONE
            binding.clQualificationExtensionIcon.animate().rotationBy(-90f).setDuration(150)
                .setInterpolator(LinearInterpolator())
                .start()

        } else {
            binding.llQualificationHiddenView.visibility = View.VISIBLE
            binding.clQualificationExtensionIcon.animate().rotationBy(90f).setDuration(150)
                .setInterpolator(LinearInterpolator())
                .start()
        }
    }

    private fun copyTextToClipboard(label: String = "Text", textView: TextView) {
        val text = textView.text.toString()
        val originalColor = textView.currentTextColor
        val changedColor = ContextCompat.getColor(requireContext(), R.color.color_one)
        textView.setTextColor(changedColor)
        val clipboard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Snackbar.make(requireView(), "copied", Snackbar.LENGTH_SHORT).show()
        lifecycleScope.launch {
            delay(700)
            withContext(Dispatchers.Main) {
                textView.setTextColor(originalColor)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}