package com.hydrogen.hydrogenpaymentsdk.presentation.dailog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hydrogen.hydrogenpayandroidpaymentsdk.R
import com.hydrogen.hydrogenpayandroidpaymentsdk.databinding.FragmentSavedCardsBottomSheetDailogListDialogBinding
import com.hydrogen.hydrogenpaymentsdk.di.AppViewModelProviderFactory
import com.hydrogen.hydrogenpaymentsdk.di.HydrogenPayDiModule
import com.hydrogen.hydrogenpaymentsdk.di.HydrogenPayDiModule.providesGson
import com.hydrogen.hydrogenpaymentsdk.domain.models.SavedCard
import com.hydrogen.hydrogenpaymentsdk.presentation.adapters.SavedCardsRecyclerViewAdapter
import com.hydrogen.hydrogenpaymentsdk.presentation.interactors.SavedCardsSelectListener
import com.hydrogen.hydrogenpaymentsdk.presentation.viewModels.AppViewModel
import com.hydrogen.hydrogenpaymentsdk.presentation.viewStates.Status
import com.hydrogen.hydrogenpaymentsdk.utils.AppConstants.STRING_SET_SELECTED_CARD_BUNDLE_KEY
import com.hydrogen.hydrogenpaymentsdk.utils.AppConstants.STRING_SET_SELECTED_CARD_REQUEST_KEY

class SavedCardsBottomSheetDialogFragment : BottomSheetDialogFragment(), SavedCardsSelectListener {
    private lateinit var binding: FragmentSavedCardsBottomSheetDailogListDialogBinding
    private lateinit var closeIcon: ImageView
    private lateinit var button: Button
    private lateinit var recyclerView: RecyclerView
    private val appViewModel: AppViewModel by viewModels {
        AppViewModelProviderFactory(HydrogenPayDiModule)
    }
    private lateinit var savedCardsAdapter: SavedCardsRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                dismiss()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_saved_cards_bottom_sheet_dailog_list_dialog,
            container,
            false
        )

        savedCardsAdapter = SavedCardsRecyclerViewAdapter(this)

        binding =
            FragmentSavedCardsBottomSheetDailogListDialogBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            appViewModel = this@SavedCardsBottomSheetDialogFragment.appViewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews()
        recyclerView.adapter = savedCardsAdapter
        appViewModel.savedCard.observe(viewLifecycleOwner) {
            it?.let { data ->
                if (data.status == Status.SUCCESS && !data.content.isNullOrEmpty()) {
                    savedCardsAdapter.updateData(it.content!!)
                }
            }
        }
        closeIcon.setOnClickListener {
            dismiss()
        }
        button.setOnClickListener {
            setFragmentResult(
                STRING_SET_SELECTED_CARD_REQUEST_KEY,
                bundleOf(
                    STRING_SET_SELECTED_CARD_BUNDLE_KEY to appViewModel.getSelectedCard()
                        ?.let {
                            providesGson().toJson(it)
                        }
                )
            )
            dismiss()
        }
    }

    private fun initViews() {
        with(binding) {
            closeIcon = imageView20
            this@SavedCardsBottomSheetDialogFragment.button = button
            recyclerView = list
        }
    }

    override fun onCardSelected(card: SavedCard) {
        appViewModel.selectASavedCard(card)
    }

    override fun onDeleteCard(card: SavedCard) {
        appViewModel.deleteCard(card)
    }
}