package org.eclipsesoundscapes.ui.media

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.eclipsesoundscapes.R
import org.eclipsesoundscapes.databinding.ItemMediaBinding
import org.eclipsesoundscapes.databinding.ItemSectionBinding
import org.eclipsesoundscapes.model.MediaItem
import org.eclipsesoundscapes.model.Section
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
    private val mediaList: ArrayList<Any>,
    private val clickListener: MediaClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        val mediaItem = mediaList[position]
        return if (mediaItem is Section) {
            VIEW_SECTION
        } else {
            VIEW_MEDIA
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_SECTION) {
            return MediaSectionViewHolder.from(parent)
        }

        return MediaViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val mediaItem = mediaList[position]
        (holder as? MediaSectionViewHolder)?.let {
            (mediaItem as? Section)?.let { section ->
                it.bind(section)
            }
        }

        (holder as? MediaViewHolder)?.let {
            (mediaItem as? MediaItem)?.let { media ->
                it.bind(media, clickListener)
            }
        }
    }

    override fun getItemCount(): Int = mediaList.size

    internal class MediaSectionViewHolder private constructor(val binding: ItemSectionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(section: Section) {
            binding.title.text = section.title
        }

        companion object {
            fun from(parent: ViewGroup): MediaSectionViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemSectionBinding.inflate(layoutInflater, parent, false)
                return MediaSectionViewHolder(binding)
            }
        }
    }

    internal class MediaViewHolder private constructor(val binding: ItemMediaBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MediaItem, clickListener: MediaClickListener) {
            binding.media = item
            binding.clickListener = clickListener
            binding.executePendingBindings()

            binding.root.context?.let {
                val title = it.getString(item.titleResId)
                binding.listMediaImg.contentDescription = it.getString(R.string.eclipse_item_img_desc, title)
            }
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

    companion object {
        private const val VIEW_SECTION = 0
        private const val VIEW_MEDIA = 1
    }
}