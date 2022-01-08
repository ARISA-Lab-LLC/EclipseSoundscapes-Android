package org.eclipsesoundscapes.ui.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.eclipsesoundscapes.R
import org.eclipsesoundscapes.databinding.ActivityFutureEclipsesBinding
import org.eclipsesoundscapes.databinding.ItemFutureEclipseBinding
import org.eclipsesoundscapes.ui.base.BaseActivity
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException
import java.nio.charset.Charset
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

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
 * An activity which parses a local json file to present a list view of upcoming eclipses
 */
class FutureEclipsesActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityFutureEclipsesBinding.inflate(layoutInflater).apply {
            supportActionBar?.title = getString(R.string.supported_eclipse)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            futureRecycler.setHasFixedSize(true)
            futureRecycler.layoutManager = LinearLayoutManager(this@FutureEclipsesActivity)
            futureRecycler.adapter = FutureEclipseAdapter(parseEclipseJson())
        }

        setContentView(binding.root)
    }

    private fun parseEclipseJson() : ArrayList<FutureEclipse> {
        val futureEclipses = ArrayList<FutureEclipse>()
        try {
            val jsonArray = JSONArray(loadJSONFromAsset())
            for (i in 0 until jsonArray.length()) {
                val eclipseObject = jsonArray.getJSONObject(i)
                val eclipse = FutureEclipse(
                    eclipseObject.getString("Date"), eclipseObject.getString("Time"),
                    eclipseObject.getString("Type"), eclipseObject.getString("Features")
                )
                if (upcoming(eclipse)) {
                    futureEclipses.add(eclipse)
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return futureEclipses
    }

    private fun upcoming(futureEclipse: FutureEclipse): Boolean {
        val dateString = futureEclipse.date
        try {
            val format = SimpleDateFormat("yyyy, MMM dd", Locale.US)
            val date = format.parse(dateString)
            return date != null && !date.before(Date())
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            onBackPressed()
            true
        } else super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right)
    }

    private fun loadJSONFromAsset(): String? {
        val json = try {
            val inputStream = assets.open("future_eclipses.json")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()

            String(buffer, Charset.defaultCharset())
        } catch (ex: IOException) {
            ex.printStackTrace()
            null
        }

        return json
    }

    /**
     * Class represents a future eclipse object, includes date, time, type and
     * what interactive features will be available through Eclipse Soundscapes application
     */
    internal class FutureEclipse(
        var date: String,
        var time: String,
        var type: String,
        var features: String
    )

    /**
     * An adapter for displaying upcoming eclipses
     */
    internal class FutureEclipseAdapter(private var futureEclipses: ArrayList<FutureEclipse>) :
        RecyclerView.Adapter<FutureEclipseAdapter.ViewHolder>() {

        internal class ViewHolder private constructor(val binding: ItemFutureEclipseBinding) : RecyclerView.ViewHolder(binding.root) {
            fun bind(eclipse: FutureEclipse) {
                val context = itemView.context

                binding.date.text = context.getString(
                    R.string.future_eclipse_label_format,
                    context.getString(R.string.date), eclipse.date
                )

                binding.time.text = context.getString(
                    R.string.future_eclipse_label_format,
                    context.getString(R.string.time), eclipse.time
                )

                binding.type.text = context.getString(
                    R.string.future_eclipse_label_format,
                    context.getString(R.string.type), eclipse.type
                )

                binding.features.text = context.getString(
                    R.string.future_eclipse_label_format,
                    context.getString(R.string.features),
                    context.getString(R.string.future_eclipse_features)
                )
            }

            companion object {
                fun from(parent: ViewGroup): ViewHolder {
                    val layoutInflater = LayoutInflater.from(parent.context)
                    val binding = ItemFutureEclipseBinding.inflate(layoutInflater, parent, false)
                    return ViewHolder(binding)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder.from(parent)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(futureEclipses[position])
        }

        override fun getItemCount(): Int {
            return futureEclipses.size
        }
    }
}