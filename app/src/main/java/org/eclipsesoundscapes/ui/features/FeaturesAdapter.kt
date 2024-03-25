package org.eclipsesoundscapes.ui.features

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.eclipsesoundscapes.R
import org.eclipsesoundscapes.databinding.ItemFeatureBinding
import org.eclipsesoundscapes.databinding.ItemSectionBinding
import org.eclipsesoundscapes.model.Eclipse
import org.eclipsesoundscapes.model.Section
import java.util.*

internal class FeaturesAdapter internal constructor(
    private val eclipseList: ArrayList<Any>,
    private val clickListener: FeaturesClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        val eclipseItem = eclipseList[position]
        return if (eclipseItem is Section) {
            VIEW_SECTION
        } else {
            VIEW_ECLIPSE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_SECTION) {
            return SectionViewHolder.from(parent)
        }

        return FeaturesViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val eclipseItem = eclipseList[position]
        (holder as? SectionViewHolder)?.let {
            (eclipseItem as? Section)?.let { section ->
                it.bind(section)
            }
        }

        (holder as? FeaturesViewHolder)?.let {
            (eclipseItem as? Eclipse)?.let { media ->
                it.bind(media, clickListener)
            }
        }
    }

    override fun getItemCount(): Int = eclipseList.size

    internal class SectionViewHolder private constructor(val binding: ItemSectionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(section: Section) {
            binding.title.text = section.title
        }

        companion object {
            fun from(parent: ViewGroup): SectionViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemSectionBinding.inflate(layoutInflater, parent, false)
                return SectionViewHolder(binding)
            }
        }
    }

    internal class FeaturesViewHolder private constructor(val binding: ItemFeatureBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Eclipse, clickListener: FeaturesClickListener) {
            binding.eclipse = item
            binding.clickListener = clickListener
            binding.executePendingBindings()

            binding.root.context?.let {
                val title = it.getString(item.title())
                binding.descriptionButton.text = it.getString(R.string.open_description, title)
            }
        }

        companion object {
            fun from(parent: ViewGroup): FeaturesViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemFeatureBinding.inflate(layoutInflater, parent, false)
                return FeaturesViewHolder(binding)
            }
        }
    }

    class FeaturesClickListener(val featuresClickListener: (eclipse: Eclipse, rumbleMap: Boolean) -> Unit) {
        fun onEclipseSelected(eclipse: Eclipse, rumbleMap: Boolean = true) = featuresClickListener(eclipse, rumbleMap)
    }

    companion object {
        private const val VIEW_SECTION = 0
        private const val VIEW_ECLIPSE = 1
    }
}