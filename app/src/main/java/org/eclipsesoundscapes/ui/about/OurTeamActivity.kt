package org.eclipsesoundscapes.ui.about

import android.content.res.TypedArray
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.addCallback
import androidx.recyclerview.widget.LinearLayoutManager
import org.eclipsesoundscapes.R
import org.eclipsesoundscapes.databinding.ActivityOurTeamBinding
import org.eclipsesoundscapes.ui.base.BaseActivity

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
 * Displays a list view of the Eclipse Soundscapes team
 */
class OurTeamActivity : BaseActivity() {

    private var teamPhotos: TypedArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityOurTeamBinding.inflate(layoutInflater).apply {
            setSupportActionBar(appBar.toolbar)
            supportActionBar?.title = getString(R.string.our_team)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            teamsRecyclerview.layoutManager = LinearLayoutManager(this@OurTeamActivity)

            teamsRecyclerview.adapter = createAdapter()
            teamsRecyclerview.isNestedScrollingEnabled = false
        }

        setContentView(binding.root)

        onBackPressedDispatcher.addCallback {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, R.anim.anim_slide_in_left, R.anim.anim_slide_out_right)
            } else {
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right)
            }

            finish()
            return@addCallback
        }
    }

    private fun createAdapter() : TeamAdapter {
        val teamList = resources.getStringArray(R.array.team)
        val teamDescription = resources.getStringArray(R.array.team_bio)
        val teamTitle = resources.getStringArray(R.array.team_title)
        teamPhotos = resources.obtainTypedArray(R.array.team_photos)

        return TeamAdapter(teamList, teamTitle, teamDescription, teamPhotos)
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

    override fun onDestroy() {
        super.onDestroy()
        teamPhotos?.recycle()
    }
}