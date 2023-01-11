package org.eclipsesoundscapes.ui.about

import android.content.Context
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.eclipsesoundscapes.R
import org.eclipsesoundscapes.databinding.ItemPartnerBinding
import org.eclipsesoundscapes.databinding.ItemSectionBinding
import org.eclipsesoundscapes.model.Partner
import org.eclipsesoundscapes.model.Section
import java.util.*

/**
 * @author Joel Goncalves
 *
 * Adapter that populates a list of current and past partners
 */
class PartnersAdapter internal constructor(
    context: Context,
    currentPartners: ArrayList<Partner>,
    previousPartners: ArrayList<Partner>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val data = ArrayList<Any>()

    override fun getItemViewType(position: Int): Int {
        val item = data[position]
        if (item is Section) {
            return ITEM_VIEW_TYPE_SECTION
        } else if (item is Partner) {
            return ITEM_VIEW_TYPE_ITEM
        }
        return super.getItemViewType(position)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == ITEM_VIEW_TYPE_SECTION) {
            return SectionViewHolder.from(viewGroup)
        }

        return PartnerViewHolder.from(viewGroup)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
        val item = data[i]
        if (viewHolder is SectionViewHolder && item is Section) {
            viewHolder.bind(item.title)
        } else if (viewHolder is PartnerViewHolder && item is Partner) {
            viewHolder.bind(item)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    companion object {
        private const val ITEM_VIEW_TYPE_SECTION = 0
        private const val ITEM_VIEW_TYPE_ITEM = 1
    }

    init {
        if (previousPartners.isEmpty()) {
            data.addAll(currentPartners)
        } else {
            data.add(Section(context.getString(R.string.current_partners)))
            data.addAll(currentPartners)
            data.add(Section(context.getString(R.string.past_partners)))
            data.addAll(previousPartners)
        }
    }

    internal class SectionViewHolder private constructor(val binding: ItemSectionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(title: String) {
            binding.title.text = title
        }

        companion object {
            fun from(parent: ViewGroup): SectionViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemSectionBinding.inflate(layoutInflater, parent, false)
                return SectionViewHolder(binding)
            }
        }
    }

    internal class PartnerViewHolder private constructor(val binding: ItemPartnerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(partner: Partner) {
            binding.partnerName.text = partner.title
            binding.extraDetail.text = partner.link
            binding.partnerDescription.text = partner.description
            binding.partnerLogo.setImageDrawable(partner.logo)
            Linkify.addLinks(binding.extraDetail, Linkify.ALL)

            binding.root.nextFocusDownId = binding.extraDetail.id
        }

        companion object {
            fun from(parent: ViewGroup): PartnerViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemPartnerBinding.inflate(layoutInflater, parent, false)
                return PartnerViewHolder(binding)
            }
        }
    }
}