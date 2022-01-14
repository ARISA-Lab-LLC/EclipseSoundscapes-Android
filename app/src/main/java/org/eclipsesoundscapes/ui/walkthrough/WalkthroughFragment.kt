package org.eclipsesoundscapes.ui.walkthrough

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import org.eclipsesoundscapes.R

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
 * Reycled in the walk-through viewpager to display layouts
 * @see WalkthroughActivity
 */
class WalkthroughFragment : Fragment() {

    private lateinit var walkthroughPage: WalkthroughActivity.WalkthroughPage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        walkthroughPage =
            arguments?.getParcelable<WalkthroughActivity.WalkthroughPage>(TUTORIAL_PAGE) as WalkthroughActivity.WalkthroughPage
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(walkthroughPage.screenId, container, false)
        val currentPage = rootView.findViewById<TextView>(R.id.current_page)

        val pageText = getString(
            R.string.page_indicator,
            walkthroughPage.currentPage + 1,
            walkthroughPage.totalPages
        )

        currentPage.text = pageText
        currentPage.contentDescription = pageText

        if (walkthroughPage.screenId == R.layout.layout_walkthrough_five) {
            // handle permissions before main content
            val askLaterButton = rootView.findViewById<Button>(R.id.button_ask_later)
            val locationButton = rootView.findViewById<Button>(R.id.button_location)
            askLaterButton.setOnClickListener {
                (activity as? WalkthroughActivity)?.completeWalkthrough()
            }

            locationButton.setOnClickListener {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        return rootView
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                (activity as? WalkthroughActivity)?.completeWalkthrough()
            }
        }

    companion object {
        private const val TUTORIAL_PAGE = "tutorial_page"

        fun newInstance(walkthroughPage: WalkthroughActivity.WalkthroughPage): WalkthroughFragment {
            return WalkthroughFragment()
                .apply {
                    arguments = bundleOf(
                        TUTORIAL_PAGE to walkthroughPage
                    )
                }
        }
    }
}