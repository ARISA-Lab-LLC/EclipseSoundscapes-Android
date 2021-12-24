package org.eclipsesoundscapes

import android.widget.TextView
import androidx.databinding.BindingAdapter


@BindingAdapter("eclipseCenterText")
fun setEclipseCenterText(textView: TextView, text: String) {
    val context = textView.context
    textView.text = context.getString(R.string.eclipse_center_title_format, text)
}