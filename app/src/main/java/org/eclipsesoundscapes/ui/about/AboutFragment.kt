package org.eclipsesoundscapes.ui.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import org.eclipsesoundscapes.R
import org.eclipsesoundscapes.databinding.FragmentAboutBinding
import org.eclipsesoundscapes.ui.rumblemap.RumbleMapInstructionsActivity
import org.eclipsesoundscapes.ui.walkthrough.WalkthroughActivity

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
 * Provides a brief description of the Eclipse Soundscapes Project and creates a list view
 * other navigation options
 * @see OurTeamActivity
 *
 * @see OurPartnersActivity
 *
 * @see FutureEclipsesActivity
 *
 * @see WalkthroughActivity
 *
 * @see SettingsActivity
 */

enum class AboutItem {
    RUMBLE_MAP,
    TEAM,
    PARTNERS,
    FUTURE_ECLIPSES,
    WALKTHROUGH,
    FEEDBACK,
    SETTINGS,
    LEGAL
}

class AboutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAboutBinding.inflate(inflater, container, false).apply {
            val toolbar = appBar.toolbar
            toolbar.title = getString(R.string.about_us)
            (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)

            aboutListView.layoutManager = LinearLayoutManager(activity)
            aboutListView.hasFixedSize()

            // adapter
            val aboutAdapter = AboutAdapter(AboutItem.values().toCollection(ArrayList()), AboutAdapter.AboutClickListener {
                when (it) {
                    AboutItem.RUMBLE_MAP ->
                        showActivity(Intent(activity, RumbleMapInstructionsActivity::class.java))
                    AboutItem.TEAM ->
                        showActivity(Intent(activity, OurTeamActivity::class.java))
                    AboutItem.PARTNERS ->
                        showActivity(Intent(activity, OurPartnersActivity::class.java))
                    AboutItem.FUTURE_ECLIPSES ->
                        showActivity(Intent(activity, FutureEclipsesActivity::class.java))
                    AboutItem.WALKTHROUGH -> {
                        showActivity(Intent(activity, WalkthroughActivity::class.java).apply {
                            putExtra(WalkthroughActivity.EXTRA_MODE, WalkthroughActivity.MODE_MENU)
                        })
                    }
                    AboutItem.FEEDBACK -> {
                        activity?.getString(R.string.link_feedback_form)?.let {
                            showActivity(Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse(it)
                            })
                        }
                    }
                    AboutItem.SETTINGS -> {
                        showActivity(Intent(activity, SettingsActivity::class.java).apply {
                            putExtra(SettingsActivity.EXTRA_SETTINGS_MODE, SettingsActivity.MODE_SETTINGS)
                        })
                    }
                    AboutItem.LEGAL -> {
                        showActivity(Intent(activity, SettingsActivity::class.java).apply {
                            putExtra(SettingsActivity.EXTRA_SETTINGS_MODE, SettingsActivity.MODE_LEGAL)
                        })
                    }
                }
            })

            aboutListView.adapter = aboutAdapter
        }

        return binding.root
    }

    private fun showActivity(intent: Intent) {
        activity?.startActivity(intent)
        activity?.overridePendingTransition(
            R.anim.anim_slide_in_right,
            R.anim.anim_slide_out_left
        )
    }

    companion object {
        const val TAG = "AboutFragment"

        @JvmStatic
        fun newInstance(): AboutFragment {
            return AboutFragment()
        }
    }
}