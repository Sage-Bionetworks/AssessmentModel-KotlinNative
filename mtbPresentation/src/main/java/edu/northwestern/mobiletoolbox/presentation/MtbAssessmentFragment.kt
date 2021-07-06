package edu.northwestern.mobiletoolbox.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.sagebionetworks.assessmentmodel.FormStep
import org.sagebionetworks.assessmentmodel.Step
import org.sagebionetworks.assessmentmodel.northwestern.MtbSerialization
import org.sagebionetworks.assessmentmodel.northwestern.MtbStep
import org.sagebionetworks.assessmentmodel.presentation.AssessmentFragment
import org.sagebionetworks.assessmentmodel.presentation.DebugStepFragment


open class MtbAssessmentFragment : AssessmentFragment() {

    companion object {

        @JvmStatic
        fun newFragmentInstance(assessmentId: String, resourceName: String, packageName: String) =
            MtbAssessmentFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_ASSESSMENT_ID_KEY, assessmentId)
                    putString(ARG_RESOURCE_NAME, resourceName)
                    putString(ARG_PACKAGE_NAME, packageName)
                }
            }
    }

    override fun getJsonLoader() : Json {
        //Can change the serialization context here to load different serializer modules
        return Json(context = MtbSerialization.SerializersModule.default, configuration = JsonConfiguration.Stable.copy(ignoreUnknownKeys = true, isLenient = true))
    }

    override fun getFragmentForStep(step: Step): Fragment {
        //Determine which fragment to laod for the given step
        when(step) {
            is FormStep -> return DebugStepFragment()
            else -> return super.getFragmentForStep(step)
        }
    }

}
