package org.eclipsesoundscapes.ui.features

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.eclipsesoundscapes.R
import org.eclipsesoundscapes.databinding.FragmentFeaturesBinding
import org.eclipsesoundscapes.model.Eclipse
import org.eclipsesoundscapes.ui.main.MainActivity

class FeaturesFragment : Fragment() {
    companion object {
        const val TAG = "FeaturesFragment"

        @JvmStatic
        fun newInstance() = FeaturesFragment()
    }

    private var _binding: FragmentFeaturesBinding? = null
    private val binding get() = _binding!!

    private lateinit var eclipses: ArrayList<Eclipse>

    // tracks the current selected tab
    private val _tabState = MutableStateFlow(0)
    val tabState: StateFlow<Int> = _tabState

    private var currentPosition = 0
        set(value) {
            field = when {
                value > eclipses.size - 1 -> {
                    // reset position to start
                    0
                }
                value < 0 -> {
                    // set position to last item
                    eclipses.size - 1
                }
                else -> {
                    value
                }
            }

            savePosition()
            updateTitle()
            showEclipse()
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeaturesBinding.inflate(inflater, container, false)

        binding.nextButton.setOnClickListener { currentPosition += 1 }

        binding.previousButton.setOnClickListener { currentPosition -= 1 }

        binding.toolbarTitle.doOnTextChanged { _, _, _, _ -> announceChange() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val lastPosition = (activity as? MainActivity)?.dataManager?.featuresPosition

        binding.tabs.apply {
            val descriptionTab =
                binding.tabs.newTab().setText(view.context.getString(R.string.description))
            val imageTab =
                binding.tabs.newTab().setText(view.context.getString(R.string.rumble_map))

            addTab(descriptionTab)
            addTab(imageTab)

            lastPosition?.second?.let {
                _tabState.value = it
                selectTab(
                    if (it == 0) {
                        descriptionTab
                    } else {
                        imageTab
                    }
                )
            }

            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.let {
                        _tabState.value = tab.position
                        announceChange()
                    }

                    savePosition()
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}

                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })
        }

        eclipses =
            if (view.context.resources.getBoolean(R.bool.show_all_content) || (activity as? MainActivity)?.isAfterTotality == true) {
                // show all eclipses
                Eclipse.values().toCollection(ArrayList())
            } else {
                arrayListOf(
                    Eclipse.FIRST_CONTACT,
                    Eclipse.BAILYS_BEADS,
                    Eclipse.BAILYS_BEADS_CLOSEUP,
                    Eclipse.CORONA,
                    Eclipse.DIAMOND_RING,
                    Eclipse.HELMET_STREAMER,
                    Eclipse.HELMET_STREAMER_CLOSEUP,
                    Eclipse.PROMINENCE,
                    Eclipse.PROMINENCE_CLOSEUP
                )
            }

        currentPosition = lastPosition?.first ?: 0
    }

    override fun onStart() {
        super.onStart()
        (activity as? AppCompatActivity)?.let { activity ->
            activity.setSupportActionBar(binding.toolbar)
            activity.supportActionBar?.let {
                it.setDisplayShowTitleEnabled(false)
                it.setDisplayHomeAsUpEnabled(false)
            }
        }
    }

    private fun showEclipse() {
        childFragmentManager.beginTransaction().apply {
            replace(R.id.container, FeatureDisplayFragment.newInstance(eclipses[currentPosition]))
            commit()
        }
    }

    /**
     * Update title to the current [Eclipse] in display
     */
    private fun updateTitle() = binding.toolbarTitle.setText(eclipses[currentPosition].title())

    /**
     * Announce when user has switched tabs or tapped to view a different [Eclipse]
     */
    private fun announceChange() {
        val title = context?.getString(eclipses[currentPosition].title())

        val announce = if (binding.tabs.selectedTabPosition == 0) {
            context?.getString(R.string.viewing_desc_format, title)
        } else {
            context?.getString(R.string.viewing_desc_format, title)
        }

        binding.toolbarTitle.announceForAccessibility(announce)
    }

    private fun savePosition() {
        (activity as? MainActivity)?.dataManager?.saveFeaturesPosition(
            currentPosition,
            binding.tabs.selectedTabPosition
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}