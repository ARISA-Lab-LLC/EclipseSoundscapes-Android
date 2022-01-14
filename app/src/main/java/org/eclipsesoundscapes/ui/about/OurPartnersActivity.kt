package org.eclipsesoundscapes.ui.about

import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import org.eclipsesoundscapes.R
import org.eclipsesoundscapes.databinding.ActivityOurPartnersBinding
import org.eclipsesoundscapes.model.Partner
import org.eclipsesoundscapes.ui.base.BaseActivity
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
 * Displays a list view of Eclipse Soundscapes partners
 */
class OurPartnersActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityOurPartnersBinding.inflate(layoutInflater).apply {
            supportActionBar?.title = getString(R.string.our_partners)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            partnersRecyclerview.layoutManager = LinearLayoutManager(this@OurPartnersActivity)
            partnersRecyclerview.isNestedScrollingEnabled = false

            val currentPartners = getPartners(true)
            val adapter = PartnersAdapter(this@OurPartnersActivity, currentPartners, ArrayList())
            partnersRecyclerview.adapter = adapter
        }

        val view = binding.root
        setContentView(view)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right)
    }

    /**
     * Retrieves a list of partners
     * @param isCurrent true if partners are current supporters, otherwise past
     * @return a [java.util.List] of [partners][Partner]
     */
    private fun getPartners(isCurrent: Boolean): ArrayList<Partner> {
        val partners = ArrayList<Partner>()
        val res = resources
        val logos =
            res.obtainTypedArray(if (isCurrent) R.array.current_partners_logo else R.array.past_partners_logo)
        val titles =
            resources.getStringArray(if (isCurrent) R.array.current_partners_title else R.array.past_partners_title)
        val descriptions =
            resources.getStringArray(if (isCurrent) R.array.current_partners_desc else R.array.past_partners_desc)
        val links =
            resources.getStringArray(if (isCurrent) R.array.current_partners_link else R.array.past_partners_link)


        // assumes all partner data is in order and of equal length
        for (i in titles.indices) {
            val partner = Partner(titles[i], descriptions[i], links[i], logos.getDrawable(i))
            partners.add(partner)
        }

        logos.recycle()
        return partners
    }
}