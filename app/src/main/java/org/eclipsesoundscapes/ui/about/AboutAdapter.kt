package org.eclipsesoundscapes.ui.about

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.eclipsesoundscapes.R
import org.eclipsesoundscapes.databinding.ItemAboutBinding

/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see [http://www.gnu.org/licenses/].
  * */
/**
 * @author Joel Goncalves
 *
 * A [RecyclerView.Adapter] that populates a list with [AboutItem]
 */
class AboutAdapter internal constructor(
    private val items: ArrayList<AboutItem>,
    private val clickListener: AboutClickListener
) : RecyclerView.Adapter<AboutAdapter.AboutViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AboutViewHolder = AboutViewHolder.from(parent)

    override fun onBindViewHolder(holder: AboutViewHolder, position: Int) {
        holder.bind(items[position], clickListener)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class AboutViewHolder private constructor(val binding: ItemAboutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AboutItem, clickListener: AboutClickListener) {
            val context = itemView.context
            when (item) {
                AboutItem.RUMBLE_MAP -> {
                    binding.optionText.text = context.getString(R.string.rumble_map_how)
                    binding.listAvatar.setImageResource(R.drawable.ic_nav_elcipse_features)
                }
                AboutItem.TEAM -> {
                    binding.optionText.text = context.getString(R.string.our_team)
                    binding.listAvatar.setImageResource(R.drawable.ic_team)
                }
                AboutItem.PARTNERS -> {
                    binding.optionText.text = context.getString(R.string.our_partners)
                    binding.listAvatar.setImageResource(R.drawable.ic_partners)
                }
                AboutItem.PHOTO_CREDITS -> {
                    binding.optionText.text = context.getString(R.string.photo_credits)
                    binding.listAvatar.setImageResource(R.drawable.ic_photo_credits)
                }
                AboutItem.FUTURE_ECLIPSES -> {
                    binding.optionText.text = context.getString(R.string.supported_eclipse)
                    binding.listAvatar.setImageResource(R.drawable.ic_nav_eclipse_center)
                }
                AboutItem.WALKTHROUGH -> {
                    binding.optionText.text = context.getString(R.string.walkthrough)
                    binding.listAvatar.setImageResource(R.drawable.ic_instructions)
                }
                AboutItem.FEEDBACK -> {
                    binding.optionText.text = context.getString(R.string.feedback)
                    binding.listAvatar.setImageResource(R.drawable.ic_feedback)
                }
                AboutItem.LANGUAGE -> {
                    binding.optionText.text = context.getString(R.string.language)
                    binding.listAvatar.setImageResource(R.drawable.ic_language)
                }
                AboutItem.PERMISSIONS -> {
                    binding.optionText.text = context.getString(R.string.permissions)
                    binding.listAvatar.setImageResource(R.drawable.ic_settings)
                }
                AboutItem.LEGAL -> {
                    binding.optionText.text = context.getString(R.string.legal)
                    binding.listAvatar.setImageResource(R.drawable.ic_legal)
                }
            }

            binding.item = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): AboutViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemAboutBinding.inflate(layoutInflater, parent, false)
                return AboutViewHolder(binding)
            }
        }
    }

    class AboutClickListener(val aboutItemClickListener: (item: AboutItem) -> Unit) {
        fun onOptionClicked(item: AboutItem) = aboutItemClickListener(item)
    }
}