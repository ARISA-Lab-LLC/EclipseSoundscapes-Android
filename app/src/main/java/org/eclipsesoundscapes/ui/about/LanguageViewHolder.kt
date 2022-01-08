package org.eclipsesoundscapes.ui.about

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.eclipsesoundscapes.R
import org.eclipsesoundscapes.databinding.ItemLanguageBinding
import org.eclipsesoundscapes.util.LocaleUtils
import java.util.*

class LanguageViewHolder private constructor(val binding: ItemLanguageBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(language: Locale, clickListener: LanguageClickListener) {

        val currentLanguage = LocaleUtils.getCurrentLanguage(itemView.context)

        binding.language.text = language.getDisplayName(currentLanguage)
        binding.language.setOnClickListener {
            clickListener.onLanguageSelected(language)
        }

        if (language == currentLanguage) {
            binding.language.isChecked = true
            binding.language.setCheckMarkDrawable(R.drawable.ic_check_white)
        } else {
            binding.language.isChecked = false
            binding.language.checkMarkDrawable = null
        }
    }

    companion object {
        fun from(parent: ViewGroup): LanguageViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemLanguageBinding.inflate(layoutInflater, parent, false)
            return LanguageViewHolder(binding)
        }
    }

    class LanguageClickListener(val languageClickListener: (language: Locale) -> Unit) {
        fun onLanguageSelected(language: Locale) = languageClickListener(language)
    }
}