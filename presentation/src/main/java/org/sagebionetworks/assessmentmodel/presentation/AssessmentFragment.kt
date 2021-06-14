package org.sagebionetworks.assessmentmodel.presentation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import org.sagebionetworks.assessmentmodel.BranchNode
import org.sagebionetworks.assessmentmodel.InstructionStep
import org.sagebionetworks.assessmentmodel.Step
import org.sagebionetworks.assessmentmodel.navigation.BranchNodeState
import org.sagebionetworks.assessmentmodel.navigation.NavigationPoint
import org.sagebionetworks.assessmentmodel.survey.*
import org.w3c.dom.Text

open class AssessmentFragment : Fragment() {

    companion object {

        @JvmStatic
        fun newFragmentInstance() = AssessmentFragment()

        const val ASSESSMENT_RESULT = "AsssessmentResult"
    }

    lateinit var viewModel: AssessmentViewModel
    var assessmentFragmentProvider: AssessmentFragmentProvider? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        assessmentFragmentProvider = (activity as? AssessmentActivity)?.assessmentFragmentProvider

        var nodeState: BranchNodeState? = null
        if (parentFragment is AssessmentFragment) {
            (parentFragment as AssessmentFragment).let {
                nodeState = it.viewModel.currentNodeStateLiveData.value!!.nodeState as? BranchNodeState
            }
        } else if (activity is AssessmentActivity) {
            (activity as? AssessmentActivity)?.let {
                nodeState = it.viewModel.assessmentNodeState
            }
        }


        viewModel = initViewModel(nodeState!!)
        super.onCreate(savedInstanceState) //Needs to be called after viewModel is initialized
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.assessment_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.currentNodeStateLiveData
            .observe(this.viewLifecycleOwner, Observer<AssessmentViewModel.ShowNodeState>
            { showNodeState ->
                // If the event has already been handled, then fragment is being restored from a
                // configuration change and the step fragment will be automatically restored for us.
                if (!showNodeState.hasBeenHandled) {
                    this.showStep(showNodeState)
                }
            })
        start()
    }

    /**
     * Starts the Assessment by calling start on the [viewModel]. This is called in onActivityCreated.
     */
    open fun start() {
        viewModel.start()
    }

    open fun initViewModel(branchNodeState: BranchNodeState) =
        ViewModelProvider(
            this, AssessmentViewModelFactory()
                .create(branchNodeState)
        ).get(AssessmentViewModel::class.java)

    open fun showStep(showNodeState: AssessmentViewModel.ShowNodeState) {
        showNodeState.hasBeenHandled = true
        if (NavigationPoint.Direction.Exit == showNodeState.direction) {
            val resultIntent = Intent()
            resultIntent.putExtra(
                ASSESSMENT_RESULT,
                showNodeState.nodeState.currentResult.toString()
            )
            activity?.setResult(Activity.RESULT_CANCELED, resultIntent)
            activity?.finish()
            return
        }

        //If this is an assessment node, load the necessary assessmentFragment
        val curNode = showNodeState.nodeState.node
        (curNode as? BranchNode)?.let {
            assessmentFragmentProvider?.fragmentFor(it)?.let { fragment ->
                val transaction = childFragmentManager.beginTransaction()
                transaction
                    .replace(R.id.step_fragment_container, fragment)
                    .commit()
                childFragmentManager.executePendingTransactions()
                return
            }
        }

        val stepFragment = this.getFragmentForStep(showNodeState.nodeState.node as Step)
        val transaction = childFragmentManager.beginTransaction()
        transaction
            .replace(R.id.step_fragment_container, stepFragment)
            .commit()
        childFragmentManager.executePendingTransactions()
    }

    open fun getFragmentForStep(step: Step): Fragment {
        //TODO: need factory for loading step fragments -nbrown 02/13/2020
        when (step) {
            is SimpleQuestion -> {
                if (step.inputItem is CheckboxInputItem) {
                    return CheckboxFragment()
                } else if (step.inputItem is DateTimeInputItem) {
                    return DateTimeInputFragment()
                } else if (step.inputItem is KeyboardTextInputItem<*>) {
                    return TextQuestionStepFragment()
                }
                TODO("Not yet supported inputItem type: ${step.inputItem.javaClass.simpleName}")
            }
            is ChoiceQuestion -> return ChoiceQuestionStepFragment()
            is InstructionStep -> return InstructionStepFragment()
            is MultipleInputQuestion -> return MultipleInputQuestionStepFragment()
            else -> return DebugStepFragment()
        }
    }
}
