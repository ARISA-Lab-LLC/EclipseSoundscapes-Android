package org.eclipsesoundscapes.ui.media

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import org.eclipsesoundscapes.R
import org.eclipsesoundscapes.databinding.FragmentMediaBinding
import org.eclipsesoundscapes.model.Eclipse
import org.eclipsesoundscapes.model.MediaItem
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
 * Provides a list view of media/audio content of different eclipse view and launches
 * [MediaPlayerActivity]
 */
class MediaFragment : Fragment() {

    private val defaultEclipses = arrayListOf(
        Eclipse.BAILYS_BEADS,
        Eclipse.PROMINENCE,
        Eclipse.CORONA,
        Eclipse.HELMET_STREAMER,
        Eclipse.DIAMOND_RING
    )

    private var _binding: FragmentMediaBinding? = null
    private val binding get() = _binding!!

    private var mediaList: ArrayList<MediaItem> = ArrayList()
    private lateinit var mediaAdapter: MediaAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMediaBinding.inflate(inflater, container, false).apply {
            toolbar.setTitle(R.string.media)
            (activity as AppCompatActivity).setSupportActionBar(toolbar)

            mediaRecycler.layoutManager = LinearLayoutManager(context)
            mediaAdapter = MediaAdapter(mediaList, MediaAdapter.MediaClickListener { media ->
                activity?.let {
                    // start media player
                    it.startActivity(Intent(it, MediaPlayerActivity::class.java).apply {
                        arguments = bundleOf(
                            MediaPlayerActivity.EXTRA_TITLE to media.titleResId,
                            MediaPlayerActivity.EXTRA_IMG to media.imageResId,
                            MediaPlayerActivity.EXTRA_AUDIO to media.audioResId,
                            MediaPlayerActivity.EXTRA_DESCRIPTION to media.descriptionResId,
                            MediaPlayerActivity.EXTRA_LIVE to false,
                        )
                    })
                }
            })

            mediaRecycler.adapter = mediaAdapter
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        updateMediaContent()
    }

    private fun updateMediaContent() {
        (activity as? MainActivity)?.let {
            val items = ArrayList<Eclipse>()
            val showAll = resources.getBoolean(R.bool.show_all_content) || it.isAfterTotality
            when {
                showAll -> {
                    // show all media content
                    items.add(Eclipse.FIRST_CONTACT)
                    items.addAll(defaultEclipses)
                    items.add(Eclipse.TOTALITY)
                    binding.moreContent.visibility = View.GONE
                }
                it.isAfterFirstContact -> {
                    items.add(Eclipse.FIRST_CONTACT)
                    items.addAll(defaultEclipses)
                }
                else -> {
                    items.addAll(defaultEclipses)
                }
            }

            createMediaItems(items)

            if (showAll) {
                // the following media items are added separately because there is no
                // associated [Eclipse] type

                // sun as a star
                mediaList.add(
                    MediaItem(
                        R.drawable.sun_as_a_star,
                        R.string.sun_as_star,
                        R.string.audio_sun_as_star_full,
                        R.raw.sun_as_a_star
                    )
                )

                if (resources.getBoolean(R.bool.live_experience_enabled)) {
                    // real time experience audio
                    mediaList.add(
                        MediaItem(
                            R.drawable.eclipse_bailys_beads,
                            R.string.eclipse_experience,
                            R.string.bailys_beads_short,
                            R.raw.realtime_eclipse_shorts_saas
                        )
                    )
                }
            }

            mediaAdapter.notifyDataSetChanged()
        }
    }

    private fun createMediaItems(items: ArrayList<Eclipse>) {
        for (eclipse in items) {
            mediaList.add(
                MediaItem(
                    eclipse.imageResource(),
                    eclipse.title(),
                    eclipse.audioDescription(),
                    eclipse.audio()
                )
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "MediaFragment"

        @JvmStatic
        fun newInstance(): MediaFragment {
            return MediaFragment()
        }
    }
}