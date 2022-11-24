package app.pengalatdite.batteryalarm

import android.content.Context
import android.os.CountDownTimer
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters

class CountDownWorker(context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {

    override fun doWork(): Result {
        return try {
            var isFinished = false
            object : CountDownTimer(10000, 1000) {
                override fun onTick(p0: Long) {
                    Log.d("Timer", "onTick: $p0")
                }

                override fun onFinish() {
                    Log.d("Timer", "Finished")
                    isFinished = true
                }
            }.start()

            val outputData = Data.Builder()
                .putString(COUNT_DOWN_RESULT, isFinished.toString())
                .build()

            Result.success(outputData)
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        const val COUNT_DOWN_RESULT = "COUNT_DOWN_RESULT"
    }
}