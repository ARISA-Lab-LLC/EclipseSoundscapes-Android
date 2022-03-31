package org.eclipsesoundscapes

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

@BindingAdapter("eclipseCenterText")
fun setEclipseCenterText(textView: TextView, text: String) {
    val context = textView.context
    textView.text = context.getString(R.string.eclipse_center_title_format, text)
}

@BindingAdapter("stringId")
fun setTextFromId(textView: TextView, stringId: Int) {
    val context = textView.context
    textView.text = context.getString(stringId)
}

@BindingAdapter("roundImage")
fun setRoundImageFromId(imageView: ImageView, drawableResId: Int) {
    Glide.with(imageView.context)
        .load(drawableResId)
        .apply(RequestOptions().transforms(CenterCrop(), RoundedCorners(10)))
        .into(imageView)
}

@BindingAdapter("imageId")
fun setImageFromId(imageView: ImageView, drawableResId: Int) {
    Glide.with(imageView.context)
        .load(drawableResId)
        .into(imageView)
}

