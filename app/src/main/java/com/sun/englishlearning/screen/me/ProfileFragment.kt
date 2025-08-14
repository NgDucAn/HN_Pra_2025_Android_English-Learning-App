package com.sun.englishlearning.screen.me

import android.view.LayoutInflater
import android.view.ViewGroup
import com.sun.englishlearning.databinding.FragmentProfileBinding
import com.sun.englishlearning.utils.base.BaseFragment

class ProfileFragment : BaseFragment<FragmentProfileBinding>() {
    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentProfileBinding = FragmentProfileBinding.inflate(layoutInflater, container, false)

    override fun initData() {

    }

    override fun initView() {

    }

}
