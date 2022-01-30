package org.eclipsesoundscapes.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import org.eclipsesoundscapes.R
import org.eclipsesoundscapes.databinding.LayoutCountdownBinding
import org.joda.time.Period

class CountdownView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var binding: LayoutCountdownBinding = LayoutCountdownBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        binding.yearSection.title.text = context.getString(R.string.years)
        binding.monthSection.title.text = context.getString(R.string.months)
        binding.daySection.title.text = context.getString(R.string.days)
        binding.hourSection.title.text = context.getString(R.string.hours)
        binding.minuteSection.title.text = context.getString(R.string.minutes)
        binding.secondSection.title.text = context.getString(R.string.seconds)
    }

    fun update(period: Period) {

        updateCountdownLabels(binding.yearSection.primaryTime, binding.yearSection.secondaryTime, period.years)

        updateCountdownLabels(binding.monthSection.primaryTime, binding.monthSection.secondaryTime, period.months)

        val weeks = period.weeks
        val totalDays = (weeks * 7) + period.days
        updateCountdownLabels(binding.daySection.primaryTime, binding.daySection.secondaryTime, totalDays)

        updateCountdownLabels(binding.hourSection.primaryTime, binding.hourSection.secondaryTime, period.hours)

        updateCountdownLabels(binding.minuteSection.primaryTime, binding.minuteSection.secondaryTime, period.minutes)

        updateCountdownLabels(binding.secondSection.primaryTime, binding.secondSection.secondaryTime, period.seconds)

        binding.root.contentDescription = context.getString(
            R.string.countdown_format,
            period.years.toString(),
            period.months.toString(),
            period.days.toString(),
            period.hours.toString(),
            period.minutes.toString(),
            period.seconds.toString()
        )
    }

    private fun updateCountdownLabels(
        primaryTextView: TextView,
        secondaryTextView: TextView,
        time: Int
    ) {
        if (time > 9) {
            val array = time.toString().split("").toTypedArray()
            primaryTextView.text = array[1]
            secondaryTextView.text = array[2]
        } else {
            primaryTextView.text = 0.toString()
            secondaryTextView.text = time.toString()
        }
    }
}
