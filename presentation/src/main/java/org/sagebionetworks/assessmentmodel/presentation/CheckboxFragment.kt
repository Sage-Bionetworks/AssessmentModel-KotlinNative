package org.sagebionetworks.assessmentmodel.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.sagebionetworks.assessmentmodel.Step
import org.sagebionetworks.assessmentmodel.presentation.databinding.CheckboxFragmentBinding
import org.sagebionetworks.assessmentmodel.survey.*

/**
 * A simple [Fragment] subclass.
 * Use the [CheckboxFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CheckboxFragment : StepFragment() {
    private var _binding: CheckboxFragmentBinding? = null
    val binding get() = _binding!!

    lateinit var questionStep: SimpleQuestion
    lateinit var questionState: QuestionState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        questionState = nodeState as QuestionState
        questionStep = questionState.node as SimpleQuestion
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = CheckboxFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.navBar.setForwardOnClickListener {
            assessmentViewModel.goForward()
        }
        binding.checkboxInputView.updateResult(questionStep.inputItem as CheckboxInputItem)
        binding.navBar.setBackwardOnClickListener { assessmentViewModel.goBackward() }
        binding.navBar.setSkipOnClickListener { assessmentViewModel.goForward() }
        binding.navBar.setup(questionStep as Step)
        binding.questionHeader.questionTitle.text = questionStep.title
        binding.questionHeader.questionSubtitle.text = questionStep.subtitle
        binding.textviewid.text = questionStep.detail
        binding.checkboxInputView.placeholder = questionStep.inputItem.fieldLabel.toString()
        binding.checkboxInputView.setup(questionStep.inputItem as CheckboxInputItem)
        binding.questionHeader.closeBtn.setOnClickListener{ assessmentViewModel.cancel() }
    }
}