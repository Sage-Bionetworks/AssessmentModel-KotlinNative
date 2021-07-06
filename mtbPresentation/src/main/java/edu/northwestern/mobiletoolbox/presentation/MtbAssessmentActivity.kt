package edu.northwestern.mobiletoolbox.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.sagebionetworks.assessmentmodel.presentation.AssessmentFragment

class MtbAssessmentActivity: AppCompatActivity() {

    companion object {

        const val ARG_THEME = "arg_theme"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (intent.hasExtra(ARG_THEME)) {
            setTheme(intent.getIntExtra(ARG_THEME, R.style.BlueberryTheme))
        }

        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            val fragment = MtbAssessmentFragment.newFragmentInstance(
                intent.getStringExtra(AssessmentFragment.ARG_ASSESSMENT_ID_KEY),
                intent.getStringExtra(AssessmentFragment.ARG_RESOURCE_NAME),
                intent.getStringExtra(AssessmentFragment.ARG_PACKAGE_NAME))

            supportFragmentManager.beginTransaction().add(android.R.id.content, fragment).commit()
        }
    }

}