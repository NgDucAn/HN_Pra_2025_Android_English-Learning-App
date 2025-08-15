package com.sun.englishlearning.screen.me

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sun.englishlearning.MainActivity
import com.sun.englishlearning.databinding.FragmentMeBinding
import com.sun.englishlearning.utils.base.BaseFragment
import kotlinx.coroutines.launch

class MeFragment : BaseFragment<FragmentMeBinding>() {

    private lateinit var auth: FirebaseAuth
    private lateinit var credentialManager: CredentialManager

    override fun inflateViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMeBinding {
        return FragmentMeBinding.inflate(inflater, container, false)
    }

    override fun initData() {
        auth = Firebase.auth
        credentialManager = CredentialManager.create(requireContext())
    }

    override fun initView() {
        // Settings -> navigate to profile screen
        viewBinding.icSettings.setOnClickListener {
            findNavController().navigate(
                com.sun.englishlearning.R.id.action_navigation_me_to_navigation_profile
            )
        }
    }

    override fun onStart() {
        super.onStart()
        // Check user state and update UI when the fragment is displayed
        updateUI(auth.currentUser)
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user!= null) {
            // User is logged in: display personal information
            viewBinding.layoutUserProfile.visibility = View.VISIBLE
            viewBinding.tvUser.text = user.displayName
        } else {
            // User is not logged in (rare case on this screen): hide information
            viewBinding.layoutUserProfile.visibility = View.GONE
        }
    }
}
