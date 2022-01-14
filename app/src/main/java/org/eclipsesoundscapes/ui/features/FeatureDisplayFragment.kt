package org.eclipsesoundscapes.ui.features

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.eclipsesoundscapes.databinding.FragmentFeatureDisplayBinding
import org.eclipsesoundscapes.model.Eclipse
import org.eclipsesoundscapes.ui.rumblemap.RumbleMapInteractionActivity

import android.content.Intent

class FeatureDisplayFragment: Fragment() {
    companion object {
        private const val ECLIPSE_ARG = "eclipse"

        @JvmStatic
        fun newInstance(eclipse: Eclipse) = FeatureDisplayFragment().apply {
            arguments = bundleOf (
                ECLIPSE_ARG to eclipse
            )
        }
    }

    private var _binding: FragmentFeatureDisplayBinding? = null
    private val binding get() = _binding!!

    private lateinit var eclipse: Eclipse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            eclipse = it.getSerializable(ECLIPSE_ARG) as Eclipse
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeatureDisplayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.descriptionView.eclipseDescription.text = context?.getString(eclipse.description())
        binding.rumbleMapView.contactPointImg.setImageResource(eclipse.imageResource())
        binding.rumbleMapView.contactPointImg.setOnClickListener {
            activity?.startActivity(Intent(activity, RumbleMapInteractionActivity::class.java).apply {
                putExtra(RumbleMapInteractionActivity.EXTRA_IMG, eclipse.imageResource())
            })
        }

        viewLifecycleOwner.lifecycleScope.launch {
            // repeatOnLifecycle launches the block in a new coroutine every time the
            // lifecycle is in the STARTED state (or above) and cancels it when it's STOPPED.
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                (parentFragment as? FeaturesFragment)?.let {
                    it.tabState.collect { position ->
                        updateView(position)
                    }
                }
            }
        }
    }

    private fun updateView(position: Int) {
        binding.descriptionView.root.visibility = if (position == 0) { View.VISIBLE } else { View.GONE }
        binding.rumbleMapView.root.visibility = if (position == 0) { View.GONE } else { View.VISIBLE }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}