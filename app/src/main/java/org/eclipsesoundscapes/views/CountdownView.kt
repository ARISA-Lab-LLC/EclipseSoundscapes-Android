package org.eclipsesoundscapes.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
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
        binding.stubYears.label.text = context.getString(R.string.years)
        binding.stubMonths.label.text = context.getString(R.string.months)
        binding.stubDays.label.text = context.getString(R.string.days)
        binding.stubHours.label.text = context.getString(R.string.hours)
        binding.stubMinutes.label.text = context.getString(R.string.minutes)

        binding.stubYears.value.text = "0"
        binding.stubMonths.value.text = "0"
        binding.stubDays.value.text = "0"
        binding.stubHours.value.text = "0"
        binding.stubMinutes.value.text = "0"
    }

    fun update(period: Period) {
        binding.stubYears.value.text = period.years.toString()
        binding.stubMonths.value.text = period.months.toString()
        binding.stubHours.value.text = period.hours.toString()
        binding.stubMinutes.value.text = period.minutes.toString()

        val weeks = period.weeks
        val totalDays = (weeks * 7) + period.days
        binding.stubDays.value.text = totalDays.toString()

        binding.countdownContainer.contentDescription = context.getString(
            R.string.countdown_format,
            period.years.toString(),
            period.months.toString(),
            period.days.toString(),
            period.hours.toString(),
            period.minutes.toString(),
            period.seconds.toString()
        )
    }
}
