package org.eclipsesoundscapes.ui.about

import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import org.eclipsesoundscapes.databinding.ItemPhotoCreditBinding
import org.eclipsesoundscapes.model.PhotoCredit


/**
 * Adapter that populates a list of [PhotoCredit]
 */
class PhotoCreditAdapter(private val photoCredits: ArrayList<PhotoCredit>,
                         private val clickListener: CreditsClickListener
) :
    RecyclerView.Adapter<PhotoCreditAdapter.PhotoCreditViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoCreditViewHolder =
        PhotoCreditViewHolder.from(parent)

    override fun onBindViewHolder(holder: PhotoCreditViewHolder, position: Int) =
        holder.bind(clickListener, photoCredits[position])

    override fun getItemCount(): Int = photoCredits.size

    class PhotoCreditViewHolder private constructor(private val binding: ItemPhotoCreditBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(clickListener: CreditsClickListener, photoCredit: PhotoCredit) {
            binding.photoCredit = photoCredit
            binding.clickListener = clickListener
            binding.executePendingBindings()

            showLink(photoCredit)

            Glide.with(binding.photo.context)
                .load(photoCredit.imageResource)
                .apply(RequestOptions().transforms(CenterCrop(), RoundedCorners(8)))
                .into(binding.photo)
        }

        private fun showLink(photoCredit: PhotoCredit) {
            val spannableString = SpannableString(photoCredit.link)

            val clickableSpan = object: ClickableSpan() {
                override fun onClick(p0: View) {
                    binding.clickListener?.onPhotoCreditClicked(photoCredit)
                }
            }

            spannableString.setSpan(
                clickableSpan,
                0,
                spannableString.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            spannableString.setSpan(UnderlineSpan(), 0, spannableString.length, 0)
            binding.link.text = spannableString
            binding.link.movementMethod = LinkMovementMethod.getInstance();
        }

        companion object {
            fun from(parent: ViewGroup): PhotoCreditViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemPhotoCreditBinding.inflate(layoutInflater, parent, false)
                return PhotoCreditViewHolder(binding)
            }
        }
    }

    class CreditsClickListener(val creditClickListener: (credit: PhotoCredit) -> Unit) {
        fun onPhotoCreditClicked(credit: PhotoCredit) = creditClickListener(credit)
    }
}