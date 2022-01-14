package org.eclipsesoundscapes.ui.about

import android.text.util.Linkify
import android.view.LayoutInflater
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
class PhotoCreditAdapter(private val photoCredits: ArrayList<PhotoCredit>) :
    RecyclerView.Adapter<PhotoCreditAdapter.PhotoCreditViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoCreditViewHolder =
        PhotoCreditViewHolder.from(parent)

    override fun onBindViewHolder(holder: PhotoCreditViewHolder, position: Int) =
        holder.bind(photoCredits[position])

    override fun getItemCount(): Int = photoCredits.size

    class PhotoCreditViewHolder private constructor(private val binding: ItemPhotoCreditBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(photoCredit: PhotoCredit) {
            binding.photoCredit = photoCredit
            Linkify.addLinks(binding.link, Linkify.ALL)

            Glide.with(binding.photo.context)
                .load(photoCredit.eclipse.imageResource())
                .apply(RequestOptions().transforms(CenterCrop(), RoundedCorners(8)))
                .into(binding.photo)
        }

        companion object {
            fun from(parent: ViewGroup): PhotoCreditViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemPhotoCreditBinding.inflate(layoutInflater, parent, false)
                return PhotoCreditViewHolder(binding)
            }
        }
    }
}