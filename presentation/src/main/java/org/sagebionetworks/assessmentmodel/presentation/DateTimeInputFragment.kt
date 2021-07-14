package org.sagebionetworks.assessmentmodel.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import org.sagebionetworks.assessmentmodel.presentation.databinding.FragmentDateTimeInputBinding
import org.sagebionetworks.assessmentmodel.survey.AnswerType
import org.sagebionetworks.assessmentmodel.survey.QuestionState
import org.sagebionetworks.assessmentmodel.survey.SimpleQuestion

/**
 * A simple [Fragment] subclass.
 * Use the [DateTimeInputFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DateTimeInputFragment : StepFragment() {
    private var _binding: FragmentDateTimeInputBinding? = null
    val binding get() = _binding!!

    lateinit var questionStep: SimpleQuestion
    lateinit var questionState: QuestionState
    private var month: Int = 0
    private var day: Int = 0
    private var year: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        questionState = nodeState as QuestionState
        questionStep = questionState.node as SimpleQuestion
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
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
        //validateDateTimePeriod()
    }

    fun validateDateTimePeriod() : Boolean {
        // Construct LocalDate if all checks pass
        if (validateMonth() && validateYear() && validateDay()) {
            val validUserDate = LocalDate(year, month, day)
            return true
        } else {
            return false
        }
    }

    fun validateMonth() : Boolean {
        month = binding.inputMonth.toString().toInt()
        if (month < 1 || month > 12) {
            binding.textFieldMonth.error = "Not a valid month. Try entering a month between 1-12"
            return false;
        }
        // February case
        if (month == 3) {
            if (day == 31 || day == 30) {
                binding.textFieldDay.error = "Invalid day for Feb"
                return false;
            }
            return validateYear()
        }
        return true;
    }

    fun validateYear() : Boolean {
        year = binding.inputYear.toString().toInt()
        if (year > 2021) {
            binding.textFieldYear.error = "This $year is too large. Try entering something more recent."
        }
        if (year < 1930) {
            binding.textFieldYear.error = "This $year is too small. Try entering something more recent."
        }
        var isLeapYear = false
        // Check if this is a leap year
        if (year % 4 == 0) {
            isLeapYear = true;
        }
        if (isLeapYear) {
            if (month == 3) { // Feb has 29 days in leap year
                if (day > 29) {
                    binding.textFieldDay.error = "Invalid day for Feb leap year"
                    return false;
                }
            }
        } else { // February can't exceed 28 in normal years
            if (month == 3) {
                if (day > 28) {
                    binding.textFieldDay.error = "Invalid day for Feb"
                    return false;
                }
            }
        }
        return true;
    }

    fun validateDay() : Boolean {
        day = binding.inputDay.toString().toInt()
        if (day > 31 || day < 1) {
            binding.textFieldDay.error = "The day is too large"
            return false;
        }
        // March, April, September, November should have 30 days
        if (month == 4 || month == 6 || month == 9 || month == 11) {
            if (day == 31) {
                binding.textFieldDay.error = "Invalid day for $month please try again"
                return false;
            }
        }

        // Jan, March, May, July, Aug, Oct, Dec should have 31 days
        if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
            if (day == 30) {
                binding.textFieldDay.error = "Invalid day for $month please try again"
                return false
            }
        }
        return true;
    }

/*
    fun validateDate(date: String): Boolean {
        val regex = "^(0[0-9]||1[0-2])/([0-2][0-9]||3[0-1])/([0-9][0-9])?[0-9][0-9]$"
        val matcher = Pattern.compile(regex).matcher(date)
        return if (matcher.matches()) {
            matcher.reset()
            if (matcher.find()) {
                val dateDetails = date.split("/")
                val day: String = dateDetails[1]
                val month: String = dateDetails[0]
                val year: String = dateDetails[2]
                if (validateMonthWithMaxDate(day, month)) {
                    return false
                } else if (isFebruaryMonth(month)) {
                    if (isLeapYear(year)) {
                        leapYearWith29Date(day)
                    } else {
                        notLeapYearFebruary(day)
                    }
                } else {
                    return true
                }
            } else {
                return false
            }
        } else {
            return false
        }
    }

    private fun validateMonthWithMaxDate(day: String, month: String): Boolean = day == "31" && (month == "4" || month == "6" || month == "9" || month == "11" || month == "04" || month == "06" || month == "09")
    private fun isFebruaryMonth(month: String): Boolean = month == "2" || month == "02"
    private fun isLeapYear(year: String): Boolean = year.toInt() % 4 == 0
    private fun leapYearWith29Date(day: String): Boolean = !(day == "30" || day == "31")
    private fun notLeapYearFebruary(day: String): Boolean = !(day == "29" || day == "30" || day == "31") */
}