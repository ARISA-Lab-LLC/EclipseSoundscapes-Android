package org.eclipsesoundscapes.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import org.eclipsesoundscapes.EclipseSoundscapesApp
import org.eclipsesoundscapes.R
import org.eclipsesoundscapes.data.DataManager
import org.eclipsesoundscapes.databinding.ActivityMainBinding
import org.eclipsesoundscapes.ui.about.AboutFragment
import org.eclipsesoundscapes.ui.base.BaseActivity
import org.eclipsesoundscapes.ui.center.EclipseCenterFragment
import org.eclipsesoundscapes.ui.features.FeaturesFragment
import org.eclipsesoundscapes.ui.media.MediaFragment

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
 * Hosts four fragments (EclipseCenter, EclipseFeatures, Mediaplayer,
 * About) through BottomNavigationView. Provides back stack navigation and handles
 * runtime permission
 */

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    lateinit var dataManager: DataManager
        private set

    private lateinit var fragmentManager: FragmentManager

    private lateinit var bottomNavigationView: BottomNavigationView

    private val currentFragment: Fragment?
        get() {
            if (fragmentManager.backStackEntryCount != 0) {
                val fragmentTag = fragmentManager.getBackStackEntryAt(
                    fragmentManager.backStackEntryCount - 1
                ).name
                return fragmentManager.findFragmentByTag(fragmentTag)
            }
            return null
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater).apply {
            bottomNavigationView = navigation
            navigation.setOnNavigationItemSelectedListener {
                val fragmentClass = when (it.itemId) {
                    R.id.navigation_eclipse_features -> FeaturesFragment::class.java
                    R.id.navigation_eclipse_center -> EclipseCenterFragment::class.java
                    R.id.navigation_media -> MediaFragment::class.java
                    R.id.navigation_about -> AboutFragment::class.java
                    else -> FeaturesFragment::class.java
                }
                try {
                    replaceFragment(fragmentClass.newInstance() as Fragment)
                    it.isChecked = true
                    title = it.title
                    true
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            }
        }

        setContentView(binding.root)

        dataManager = (application as EclipseSoundscapesApp).dataManager
        fragmentManager = supportFragmentManager
        fragmentManager.addOnBackStackChangedListener { updateUI() }

        if (intent != null && intent.hasExtra(EXTRA_FRAGMENT_TAG)) {
            val fragmentTag = intent.getStringExtra(EXTRA_FRAGMENT_TAG)
            showFragment(fragmentTag)
        } else {
            showFragment(EclipseCenterFragment.TAG)
        }
    }

    private fun showFragment(tag: String?) {
        val fragment = getFragment(tag)
        val navigationItemId = getNavigationItemId(tag)
        if (fragment != null && navigationItemId != null) {
            val ft = fragmentManager.beginTransaction()
            ft.replace(R.id.navigation_content, fragment, tag)
            ft.addToBackStack(tag)
            ft.commit()
            bottomNavigationView.selectedItemId = navigationItemId
        }
    }

    private fun getFragment(tag: String?): Fragment? {
        when (tag) {
            EclipseCenterFragment.TAG -> return EclipseCenterFragment.newInstance()
            FeaturesFragment.TAG -> return FeaturesFragment.newInstance()
            MediaFragment.TAG -> return MediaFragment.newInstance()
            AboutFragment.TAG -> return AboutFragment.newInstance()
        }
        return null
    }

    private fun getNavigationItemId(tag: String?): Int? {
        when (tag) {
            EclipseCenterFragment.TAG -> return R.id.navigation_eclipse_center
            FeaturesFragment.TAG -> return R.id.navigation_eclipse_features
            MediaFragment.TAG -> return R.id.navigation_media
            AboutFragment.TAG -> return R.id.navigation_about
        }
        return null
    }

    private fun updateUI() {
        val fragmentAfterBackPress = currentFragment ?: return
        val fragTag = fragmentAfterBackPress.tag ?: return
        val fullPath = fragTag.split("\\.".toRegex()).toTypedArray()
        val id = when (fullPath[fullPath.size - 1]) {
            FeaturesFragment.TAG -> R.id.navigation_eclipse_features
            EclipseCenterFragment.TAG -> R.id.navigation_eclipse_center
            MediaFragment.TAG -> R.id.navigation_media
            AboutFragment.TAG -> R.id.navigation_about
            else -> R.id.navigation_eclipse_features
        }

        val menuItem = bottomNavigationView.menu.findItem(id)
        if (!menuItem.isChecked) {
            menuItem.isChecked = true
            menuItem.title = menuItem.title
        }
    }

    /**
     * Replace current fragment
     * @param fragment next fragment to be shown and added to the back stack
     */
    private fun replaceFragment(fragment: Fragment) {
        val backStateName = fragment.javaClass.name
        val fragmentPopped = fragmentManager.popBackStackImmediate(backStateName, 0)

        // fragment not in back stack - create
        if (!fragmentPopped && fragmentManager.findFragmentByTag(backStateName) == null) {
            val ft = fragmentManager.beginTransaction()
            ft.replace(R.id.navigation_content, fragment, backStateName)
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            ft.addToBackStack(backStateName)
            ft.commit()
        }
    }

    override fun onBackPressed() {
        when {
            fragmentManager.backStackEntryCount == 1 -> {
                finish()
            }
            fragmentManager.backStackEntryCount > 0 -> {
                supportFragmentManager.popBackStack()
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    companion object {
        const val EXTRA_FRAGMENT_TAG = "extra_fragment_tag"
    }
}