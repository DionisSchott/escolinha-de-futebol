package com.dionis.escolinhajdb.presentation.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.dionis.escolinhajdb.R
import com.dionis.escolinhajdb.databinding.FragmentRecoveryPasswordBinding


class RecoveryPasswordFragment : Fragment() {

    private val viewModel: ViewModel by activityViewModels()
    private var binding: FragmentRecoveryPasswordBinding? = null
    private lateinit var email: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        email = arguments?.getString(EMAIL)!!

        return FragmentRecoveryPasswordBinding.inflate(inflater, container, false).apply {
            binding = this
        }.root
    }



    companion object{
        const val EMAIL = "email"
    }


}