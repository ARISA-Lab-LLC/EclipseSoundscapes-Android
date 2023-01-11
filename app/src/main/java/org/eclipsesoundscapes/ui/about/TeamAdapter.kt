package org.eclipsesoundscapes.ui.about

import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import org.eclipsesoundscapes.databinding.ItemPartnerBinding
import org.eclipsesoundscapes.ui.custom.CircleTransform

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
 * Adapter that populates a list of team members
 */
internal class TeamAdapter internal constructor(
    private val teamMembers: Array<String>,
    private val extra: Array<String>,
    private val descriptions: Array<String>,
    private val images: TypedArray?
) : RecyclerView.Adapter<TeamAdapter.TeamViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
        return TeamViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        holder.bind(teamMembers[position], extra[position], descriptions[position], images?.getDrawable(position))
    }

    override fun getItemCount(): Int {
        return teamMembers.size
    }

    internal class TeamViewHolder private constructor(val binding: ItemPartnerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(name: String, detail: String, description: String, photo: Drawable?) {
            binding.partnerName.text = name
            binding.extraDetail.text = detail
            binding.partnerDescription.text = description

            photo?.let {
                val options = RequestOptions()
                    .centerInside()
                    .transform(CircleTransform(itemView.context))

                Glide.with(itemView.context)
                    .load(it)
                    .apply(options)
                    .into(binding.partnerLogo)
            }

            Linkify.addLinks(binding.extraDetail, Linkify.ALL)
        }

        companion object {
            fun from(parent: ViewGroup): TeamViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemPartnerBinding.inflate(layoutInflater, parent, false)
                return TeamViewHolder(binding)
            }
        }
    }
}