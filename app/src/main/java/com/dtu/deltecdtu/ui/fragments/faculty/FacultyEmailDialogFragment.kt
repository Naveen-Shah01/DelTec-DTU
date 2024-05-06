package com.dtu.deltecdtu.ui.fragments.faculty


import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.dtu.deltecdtu.R
import com.dtu.deltecdtu.databinding.FragmentEmailDialogBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class FacultyEmailDialogFragment : DialogFragment() {

    private var _binding: FragmentEmailDialogBinding? = null
    private val binding get() = _binding!!
    private var email: String? = null
    private var alternateEmail: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.MyAlertDialogTheme)
        val bundle = arguments
        email = bundle?.getString("email")
        alternateEmail = bundle?.getString("alternateEmail")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEmailDialogBinding.inflate(inflater, container, false)
        binding.apply {
            tvEmail.text = email
            if (alternateEmail != null) {
                tvAlternateEmail.text = alternateEmail
                tvAlternateEmail.visibility = View.VISIBLE
            }
            tvEmail.setOnClickListener {
                copyTextToClipboard("email", tvEmail)
            }
            tvAlternateEmail.setOnClickListener {
                copyTextToClipboard("alternateEmail", tvAlternateEmail)
            }
        }
        return binding.root
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
        Toast.makeText(requireContext(), "copied to clipboard", Toast.LENGTH_SHORT).show()
        lifecycleScope.launch {
            delay(400)
            withContext(Dispatchers.Main) {
                textView.setTextColor(originalColor)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnOK.setOnClickListener {
            dismiss()
        }

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.attributes?.windowAnimations = R.style.AnimationDialog
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
