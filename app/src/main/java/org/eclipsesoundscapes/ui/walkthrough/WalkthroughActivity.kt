package org.eclipsesoundscapes.ui.walkthrough

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.*
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import kotlinx.parcelize.Parcelize
import org.eclipsesoundscapes.EclipseSoundscapesApp
import org.eclipsesoundscapes.R
import org.eclipsesoundscapes.data.DataManager
import org.eclipsesoundscapes.databinding.ActivityWalkthroughBinding
import org.eclipsesoundscapes.ui.base.BaseActivity
import org.eclipsesoundscapes.ui.main.MainActivity

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
 * Create a walk through with view pager
 * Displayed during applications first launch or launched from [AboutFragment]
 * @see WalkthroughFragment
 */
class WalkthroughActivity : BaseActivity() {
    private lateinit var binding: ActivityWalkthroughBinding
    private lateinit var callback: ViewPager2.OnPageChangeCallback
    private lateinit var adapter: WalkthroughPagerAdapter

    private var mode: Int = MODE_START
    private var dataManager: DataManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataManager = (application as EclipseSoundscapesApp).dataManager
        mode = intent.getIntExtra(EXTRA_MODE, MODE_START)

        binding = ActivityWalkthroughBinding.inflate(layoutInflater)
            .apply {
                if (mode == MODE_MENU) {
                    skipCloseButton.text = getString(R.string.close)
                    skipCloseButton.contentDescription = getString(R.string.close)
                }

                nextButton.setOnClickListener {
                    viewpager.currentItem = viewpager.currentItem + 1
                }

                prevButton.setOnClickListener {
                    viewpager.currentItem = viewpager.currentItem -1
                }

                skipCloseButton.setOnClickListener {
                    if (mode == MODE_MENU) {
                        onBackPressed()
                    } else {
                        viewpager.currentItem = adapter.itemCount - 1
                    }
                }

                adapter = WalkthroughPagerAdapter(this@WalkthroughActivity)
                viewpager.adapter = adapter

                callback = object: ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        updateNavigationButtons(position)
                    }
                }

                viewpager.registerOnPageChangeCallback(callback)
            }

        val view = binding.root
        setContentView(view)
    }

    fun updateNavigationButtons(position: Int) {
        val pageCount = adapter.itemCount

        when (position) {
            pageCount - 1 -> {
                // last page
                binding.nextButton.visibility = View.INVISIBLE
                binding.prevButton.visibility = View.VISIBLE

                if (mode != MODE_MENU) {
                    binding.skipCloseButton.visibility = View.GONE
                }
            }
            0 -> {
                // first page
                binding.nextButton.visibility = View.VISIBLE
                binding.skipCloseButton.visibility = View.VISIBLE
                binding.prevButton.visibility = View.GONE
            }
            else -> {
                binding.nextButton.visibility = View.VISIBLE
                binding.skipCloseButton.visibility = View.VISIBLE
                binding.prevButton.visibility = View.VISIBLE
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right)
    }

    override fun onStop() {
        super.onStop()
        binding.viewpager.unregisterOnPageChangeCallback(callback)
    }

    fun completeWalkthrough() {
        dataManager?.walkthroughComplete = true
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private inner class WalkthroughPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
        val pages = if (mode == MODE_START) {
            arrayListOf(
                R.layout.layout_walkthrough_one,
                R.layout.layout_walkthrough_two,
                R.layout.layout_walkthrough_three,
                R.layout.layout_walkthrough_four,
                R.layout.layout_walkthrough_five
            )
        } else {
            arrayListOf(
                R.layout.layout_walkthrough_one,
                R.layout.layout_walkthrough_two,
                R.layout.layout_walkthrough_three,
                R.layout.layout_walkthrough_four
            )
        }

        override fun getItemCount(): Int = pages.size

        override fun createFragment(position: Int): Fragment {
            return WalkthroughFragment.newInstance(
                WalkthroughPage(pages[position], position, pages.size)
            )
        }
    }

    @Parcelize
    data class WalkthroughPage(val screenId: Int, val currentPage: Int, val totalPages: Int): Parcelable

    companion object {
        const val EXTRA_MODE = "MODE"
        const val MODE_START = 0
        const val MODE_MENU = 1
    }
}