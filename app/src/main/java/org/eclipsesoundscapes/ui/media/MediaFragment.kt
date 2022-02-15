package org.eclipsesoundscapes.ui.media

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import org.eclipsesoundscapes.R
import org.eclipsesoundscapes.databinding.FragmentMediaBinding
import org.eclipsesoundscapes.model.Eclipse
import org.eclipsesoundscapes.model.MediaItem

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

@AndroidEntryPoint
class MediaFragment : Fragment() {

    private val viewModel: MediaViewModel by viewModels()

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

            mediaList.clear()
            createMediaItems(Eclipse.mediaEclipses())

            mediaRecycler.layoutManager = LinearLayoutManager(context)
            mediaAdapter = MediaAdapter(mediaList, MediaAdapter.MediaClickListener { media ->
                activity?.let {
                    // start media player
                    it.startActivity(Intent(it, MediaPlayerActivity::class.java).apply {
                        putExtra(MediaPlayerActivity.EXTRA_MEDIA, media)
                        putExtra(MediaPlayerActivity.EXTRA_LIVE, false)
                    })
                }
            })

            mediaRecycler.adapter = mediaAdapter
        }

        return binding.root
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