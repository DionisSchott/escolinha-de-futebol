package com.dionis.escolinhajdb.presentation.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dionis.escolinhajdb.R
import com.dionis.escolinhajdb.States
import com.dionis.escolinhajdb.UiState
import com.dionis.escolinhajdb.data.model.Player
import com.dionis.escolinhajdb.databinding.FragmentRegisterPlayerBinding
import com.dionis.escolinhajdb.util.Extensions.toast
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterPlayerFragment : Fragment() {

    private lateinit var binding: FragmentRegisterPlayerBinding
    private val viewModel: PlayerViewModel by viewModels()
    var objPlayer: Player? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentRegisterPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUp()
    }

    private fun setUp() {
        validateFields()
        setObservers()

    }

    private fun validateFields() {
        binding.btnDone.setOnClickListener {
            val playerName = binding.edtPlayerName.text.toString()
            val responsibleName = binding.edtresponsibleName.text.toString()
            val playersBirth = binding.edtPlayersBirth.text.toString()

            viewModel.validateFields(playerName, responsibleName, playersBirth)
        }
    }

    private fun setObservers() {

        viewModel.validateFields.observe(viewLifecycleOwner) {
            when (it) {
                is States.ValidateRegisterPlayer.PlayerNameEmpty -> {
                    showObligatoryField(binding.edtPlayerNameLayout, R.string.obligatory_field)
                }
                is States.ValidateRegisterPlayer.ResponsibleNameEmpty -> {
                    showObligatoryField(binding.edtResponsibleLayout, R.string.obligatory_field)
                }
                is States.ValidateRegisterPlayer.PlayersBirthEmpty -> {
                    showObligatoryField(binding.edtPlayersBirthLayout, R.string.obligatory_field)
                }
                is States.ValidateRegisterPlayer.FieldsDone -> {
                    registerPlayer()
                }
            }
        }

        viewModel.playerRegister.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {

                }
                is UiState.Failure -> {
                    toast(it.error)
                }
                is UiState.Success -> {
                    objPlayer = it.data.first
                    findNavController().popBackStack()
                }
            }
        }

    }

    private fun getPlayerObj(): Player {
        return Player(
            id = "",
            playerName =  binding.edtPlayerName.text.toString(),
            responsibleName = binding.edtresponsibleName.text.toString(),
            playersBirth = binding.edtPlayersBirth.text.toString(),
        )
    }

    private fun registerPlayer() {
        viewModel.registerPlayer(
            player = getPlayerObj()
        )
    }

    private fun showObligatoryField(edt: TextInputLayout, message: Int) {
        edt.error = getString(message)
    }

}