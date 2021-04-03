package com.meteoro.jetsurvey.survey

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.Text
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.datepicker.MaterialDatePicker
import com.meteoro.jetsurvey.R
import com.meteoro.jetsurvey.ui.theme.JetSurveyTheme

class SurveyFragment : Fragment() {

    private val viewModel: SurveyViewModel by viewModels {
        SurveyViewModelFactory(PhotoUriManager(requireContext().applicationContext))
    }

    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { photoSaved ->
        if (photoSaved) {
            viewModel.onImageSaved()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ComposeView(requireContext()).apply {
            // In order for savedState to work, the same ID needs to be used for all instances.
            id = R.id.sign_in_fragment

            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setContent {
                JetSurveyTheme {
                    viewModel.uiState.observeAsState().value?.let { surveyState ->
                        when (surveyState) {
                            is SurveyState.Questions -> SurveyQuestionsScreen(
                                questions = surveyState,
                                onAction = { id, action -> handleSurveyAction(id, action) },
                                onDonePressed = { viewModel.computeResult(surveyState) },
                                onBackPressed = {
                                    activity?.onBackPressedDispatcher?.onBackPressed()
                                }
                            )
                            is SurveyState.Result -> Text(text = "Result")
                        }
                    }
                }
            }
        }
    }

    private fun handleSurveyAction(questionId: Int, actionType: SurveyActionType) {
        when (actionType) {
            SurveyActionType.PICK_DATE -> showDatePicker(questionId)
            SurveyActionType.TAKE_PHOTO -> takeAPhoto()
            SurveyActionType.SELECT_CONTACT -> selectContact(questionId)
        }
    }

    private fun showDatePicker(questionId: Int) {
        val picker = MaterialDatePicker.Builder.datePicker().build()
        activity?.let {
            picker.show(it.supportFragmentManager, picker.toString())
            picker.addOnPositiveButtonClickListener {
                viewModel.onDatePicked(questionId, picker.headerText)
            }
        }
    }

    private fun takeAPhoto() {
        takePicture.launch(viewModel.getUriToSaveImage())
    }

    @Suppress("UNUSED_PARAMETER")
    private fun selectContact(questionId: Int) {
        // TODO: unsupported for now
    }
}