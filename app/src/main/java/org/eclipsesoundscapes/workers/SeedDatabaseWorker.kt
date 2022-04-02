package org.eclipsesoundscapes.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.eclipsesoundscapes.data.AppDatabase
import org.eclipsesoundscapes.model.EclipseConfiguration

class SeedDatabaseWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val filename = inputData.getString(KEY_FILENAME)
            if (filename != null) {
                applicationContext.assets.open(filename).use { inputStream ->
                    JsonReader(inputStream.reader()).use { jsonReader ->
                        val type = object : TypeToken<List<EclipseConfiguration>>() {}.type
                        val list: List<EclipseConfiguration> = Gson().fromJson(jsonReader, type)

                        val database = AppDatabase.getInstance(applicationContext)
                        database.eclipseConfigurationDao().insertAll(list)

                        Result.success()
                    }
                }
            } else {
                Result.failure()
            }
        } catch (ex: Exception) {
            Result.failure()
        }
    }

    companion object {
        private const val TAG = "SeedDatabaseWorker"
        const val KEY_FILENAME = "ECLIPSE_CONFIG_DATA_FILENAME"
    }
}