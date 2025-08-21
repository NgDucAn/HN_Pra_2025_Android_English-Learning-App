package com.sun.englishlearning.screen.me

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sun.englishlearning.MainActivity
import com.sun.englishlearning.R
import com.sun.englishlearning.data.model.Language
import com.sun.englishlearning.databinding.FragmentProfileBinding
import com.sun.englishlearning.screen.me.adapter.LanguageSpinnerAdapter
import com.sun.englishlearning.utils.DialogUtils
import com.sun.englishlearning.utils.LocaleHelper
import com.sun.englishlearning.utils.base.BaseFragment
import kotlinx.coroutines.launch

class ProfileFragment : BaseFragment<FragmentProfileBinding>() {
    private lateinit var auth: FirebaseAuth
    private lateinit var credentialManager: CredentialManager
    private lateinit var languageSpinnerAdapter: LanguageSpinnerAdapter
    private var selectedLanguage: Language? = null

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentProfileBinding = FragmentProfileBinding.inflate(layoutInflater, container, false)

    override fun initData() {
        auth = Firebase.auth
        credentialManager = CredentialManager.create(requireContext())
    }

    override fun initView() {
        setupLanguageSpinner()
        viewBinding.llLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        viewBinding.btnBack.setOnClickListener {
            // Navigate back to previous screen
            findNavController().navigateUp()
        }

        viewBinding.llNotifications.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_notificationsFragment)
        }

    }

    override fun onStart() {
        super.onStart()

        updateUI(auth.currentUser)
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user!= null) {
            viewBinding.tvProfileName.text = user.displayName
            viewBinding.tvProfileEmail.text = user.email
        }
    }

    private fun setupLanguageSpinner() {
        // Create language list
        val languages = listOf(
            Language("en", getString(R.string.language_english), "en", R.drawable.ic_us),
            Language("vi", getString(R.string.language_vietnamese), "vi", R.drawable.ic_vietnam)
        )

        // LocaleHelper
        val currentLang = LocaleHelper.getLanguage(requireContext())
        val defaultIndex = languages.indexOfFirst { it.code == currentLang }.takeIf { it >= 0 } ?: 0

        // Create and set adapter
        languageSpinnerAdapter = LanguageSpinnerAdapter(requireContext(), languages)
        viewBinding.spinnerLanguage.adapter = languageSpinnerAdapter

        // Set default selection
        viewBinding.spinnerLanguage.setSelection(defaultIndex)
        selectedLanguage = languages[defaultIndex]

        // Handle language selection
        viewBinding.spinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val lang = languages[position]
                if (lang.code != LocaleHelper.getLanguage(requireContext())) {
                    LocaleHelper.setLocale(requireContext().applicationContext, lang.code)
                    // Reload activity
                    activity?.recreate()
                }
                selectedLanguage = lang
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

    private fun showLogoutConfirmationDialog() {
        DialogUtils.showConfirmationDialog(
            context = requireContext(),
            title = getString(R.string.logout_confirmation_title),
            message = getString(R.string.logout_confirmation_message),
            positiveButtonText = getString(R.string.logout_confirm),
            negativeButtonText = getString(R.string.logout_cancel),
            onPositiveClick = { signOut() }
        )
    }

    private fun signOut() {
        lifecycleScope.launch {
            auth.signOut()

            try {
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
            } catch (e: Exception) {
            }
            (activity as? MainActivity)?.navigateToLogin()
        }
    }
}
