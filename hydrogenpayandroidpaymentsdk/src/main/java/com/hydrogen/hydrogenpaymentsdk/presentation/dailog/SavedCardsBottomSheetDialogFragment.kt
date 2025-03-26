package com.hydrogen.hydrogenpaymentsdk.presentation.dailog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
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

class SavedCardsBottomSheetDialogFragment : Fragment() {
    private lateinit var binding: FragmentSavedCardsBottomSheetDailogListDialogBinding
    private lateinit var closeIcon: ImageView
    private lateinit var button: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private val appViewModel: AppViewModel by activityViewModels {
        AppViewModelProviderFactory(HydrogenPayDiModule)
    }
    private lateinit var savedCardsAdapter: SavedCardsRecyclerViewAdapter

    private val savedCardsSelectListener: SavedCardsSelectListener = object : SavedCardsSelectListener{
        override fun onCardSelected(card: SavedCard) {
            appViewModel.selectASavedCard(card)
        }

        override fun onDeleteCard(card: SavedCard) {
            appViewModel.deleteCard(card)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
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

        savedCardsAdapter = SavedCardsRecyclerViewAdapter(savedCardsSelectListener)

        binding =
            FragmentSavedCardsBottomSheetDailogListDialogBinding.inflate(inflater, container, false)
        binding.apply {
            appViewModel = this@SavedCardsBottomSheetDialogFragment.appViewModel
            lifecycleOwner = viewLifecycleOwner
            executePendingBindings()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews()
        recyclerView.adapter = savedCardsAdapter
        closeIcon.setOnClickListener {
            findNavController().popBackStack()
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
            findNavController().popBackStack()
        }
    }

    private fun initViews() {
        with(binding) {
            closeIcon = imageView20
            progressBar = progressBar2
            this@SavedCardsBottomSheetDialogFragment.button = button
            recyclerView = list
        }
    }

    override fun onResume() {
        super.onResume()
        appViewModel.savedCard.observe(this) {
            it?.let { data ->
                if (data.status == Status.SUCCESS && !(data.content.isNullOrEmpty())) {
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    savedCardsAdapter.updateData(it.content!!)
                }
            }
        }
    }
}