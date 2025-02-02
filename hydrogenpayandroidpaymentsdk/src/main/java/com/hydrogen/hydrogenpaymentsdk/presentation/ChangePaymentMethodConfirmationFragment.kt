package com.hydrogen.hydrogenpaymentsdk.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hydrogen.hydrogenpayandroidpaymentsdk.R
import com.hydrogen.hydrogenpayandroidpaymentsdk.databinding.FragmentChangePaymentMethodConfirmationBinding
import com.hydrogen.hydrogenpayandroidpaymentsdk.databinding.FragmentSelectPaymentMethodBinding

class ChangePaymentMethodConfirmationFragment : Fragment() {
    private lateinit var binding: FragmentChangePaymentMethodConfirmationBinding
    private lateinit var noButton: Button
    private lateinit var yesButton: Button
    private lateinit var closeIcon: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_change_payment_method_confirmation,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        noButton.setOnClickListener {
            findNavController().popBackStack()
        }
        closeIcon.setOnClickListener {
            findNavController().popBackStack()
        }
        yesButton.setOnClickListener {
            val action = ChangePaymentMethodConfirmationFragmentDirections.actionChangePaymentMethodConfirmationFragmentToSelectPaymentMethodFragment()
            findNavController().navigate(action)
        }
    }

    private fun initViews() {
        with(binding) {
            noButton = button2
            yesButton = button3
            closeIcon = imageView9
        }
    }
}