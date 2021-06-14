package org.sagebionetworks.assessmentmodel.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.datetime.LocalDate
import org.sagebionetworks.assessmentmodel.presentation.databinding.FragmentDateTimeInputBinding
import org.sagebionetworks.assessmentmodel.survey.QuestionState
import org.sagebionetworks.assessmentmodel.survey.SimpleQuestion

/**
 * A DateTimeInputFragment class
 * Allows a user to input a month, day and year into three text fields.
 * Validates the user input and verifies it is a correctly formatted date.
 */
class DateTimeInputFragment : StepFragment() {
    private var _binding: FragmentDateTimeInputBinding? = null
    val binding get() = _binding!!

    lateinit var questionStep: SimpleQuestion
    lateinit var questionState: QuestionState
    private var error = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        questionState = nodeState as QuestionState
        questionStep = questionState.node as SimpleQuestion
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentDateTimeInputBinding.inflate(layoutInflater, container, false)
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
        binding.questionHeader.questionTitle.text = questionStep.title
        binding.questionHeader.questionSubtitle.text = questionStep.subtitle
        binding.navBar.setBackwardOnClickListener { assessmentViewModel.goBackward() }
        binding.navBar.setSkipOnClickListener { assessmentViewModel.goForward() }
    }

    fun validateDateTimePeriod(year: Int, month: Int, day: Int) : LocalDate? {
        if (validateMonth(month, day) && validateYear(year, month, day) && validateDay(month, day)) {
            val validUserDate = LocalDate(year, month, day)
            return validUserDate
        } else {
            return null
        }
    }

    fun validateMonth(month: Int, day: Int) : Boolean {
        if (month < 1 || month > 12) {
            error = "Not a valid month. Try entering a month between 1-12"
            return false;
        }
        if (month == 2) {
            if (day == 31 || day == 30) {
                error = "Invalid day for Feb"
                return false;
            }
        }
        return true;
    }

    fun validateYear(year: Int, month: Int, day: Int) : Boolean {
        if (year > 2021) {
            error = "This $year is too large. Try entering something more recent."
            return false
        }
        if (year < 1950) {
            error = "This $year is too small. Try entering something more recent."
            return false
        }
        var isLeapYear = false
        if (year % 4 == 0) {
            isLeapYear = true;
        }
        if (isLeapYear) {
            if (month == 2) { // Feb has 29 days in leap year
                if (day > 29) {
                    error = "Invalid day for Feb leap year"
                    return false;
                }
            }
        } else { // Feb can't exceed 28 in normal years
            if (month == 2) {
                if (day > 28) {
                    error = "Invalid day for Feb"
                    return false;
                }
            }
        }
        return true;
    }

    fun validateDay(month: Int, day: Int) : Boolean {
        if (day > 31 || day < 1) {
            error = "The day is too large"
            return false;
        }
        // April, Sep, and Nov have 30 days
        if (month == 4 || month == 6 || month == 9 || month == 11) {
            if (day > 30) {
                error = "Invalid day for $month please try again"
                return false;
            }
        }
        // Jan, March, May, July, Aug, Oct, Dec have 31 days
        if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
            if (day > 31) {
                error = "Invalid day for $month please try again"
                return false
            }
        }
        // Feb sanity check
        if (month == 2 && day > 29) {
            return false
        }
        return true;
    }
}