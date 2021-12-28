package org.eclipsesoundscapes.ui.media

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.eclipsesoundscapes.databinding.ItemMediaBinding
import org.eclipsesoundscapes.model.MediaItem
import java.util.*

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
 * A [RecyclerView.Adapter] that populates a list with [MediaItem]
 */
internal class MediaAdapter internal constructor(
    private val mediaList: ArrayList<MediaItem>,
    private val clickListener: MediaClickListener
) : RecyclerView.Adapter<MediaAdapter.MediaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder = MediaViewHolder.from(parent)

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        holder.bind(mediaList[position], clickListener)
    }

    override fun getItemCount(): Int = mediaList.size

    internal class MediaViewHolder private constructor(val binding: ItemMediaBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MediaItem, clickListener: MediaClickListener) {
            binding.media = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): MediaViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemMediaBinding.inflate(layoutInflater, parent, false)
                return MediaViewHolder(binding)
            }
        }
    }

    class MediaClickListener(val mediaClickListener: (mediaItem: MediaItem) -> Unit) {
        fun onMediaClicked(mediaItem: MediaItem) = mediaClickListener(mediaItem)
    }
}