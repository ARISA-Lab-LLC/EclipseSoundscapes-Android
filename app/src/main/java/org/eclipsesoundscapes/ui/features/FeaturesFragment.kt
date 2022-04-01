package org.eclipsesoundscapes.ui.features

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
import org.eclipsesoundscapes.databinding.FragmentFeaturesBinding
import org.eclipsesoundscapes.model.Eclipse
import org.eclipsesoundscapes.model.Section
import org.eclipsesoundscapes.ui.rumblemap.RumbleMapInteractionActivity

@AndroidEntryPoint
class FeaturesFragment : Fragment() {
    companion object {
        const val TAG = "FeaturesFragment"

        @JvmStatic
        fun newInstance() = FeaturesFragment()
    }

    private val viewModel: FeaturesViewModel by viewModels()

    private var _binding: FragmentFeaturesBinding? = null
    private val binding get() = _binding!!

    private lateinit var eclipses: ArrayList<Any>
    private lateinit var featuresAdapter: FeaturesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeaturesBinding.inflate(inflater, container, false).apply {
            val toolbar = appBar.toolbar
            toolbar.setTitle(R.string.features)
            (activity as AppCompatActivity).setSupportActionBar(toolbar)

            eclipses = ArrayList()

            // annular eclipse
            eclipses.add(Section(getString(R.string.annular_solar_eclipse)))
            eclipses.addAll(Eclipse.annularEclipseMedia())

            // total eclipse
            eclipses.add(Section(getString(R.string.total_solar_eclipse)))
            eclipses.addAll(Eclipse.totalEclipseMedia())


            recycler.layoutManager = LinearLayoutManager(context)
            featuresAdapter = FeaturesAdapter(eclipses, FeaturesAdapter.FeaturesClickListener { media, openRumbleMap ->
                if (openRumbleMap) {
                    activity?.startActivity(Intent(activity, RumbleMapInteractionActivity::class.java).apply {
                        putExtra(RumbleMapInteractionActivity.EXTRA_IMG, media.imageResource())
                    })
                } else {
                    // show description
                    activity?.startActivity(Intent(activity, DescriptionActivity::class.java).apply {
                        putExtra(DescriptionActivity.EXTRA_ECLIPSE, media)
                    })
                }
            })

            recycler.adapter = featuresAdapter
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}