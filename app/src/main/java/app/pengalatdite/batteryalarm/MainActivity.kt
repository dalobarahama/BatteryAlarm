package app.pengalatdite.batteryalarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import app.pengalatdite.batteryalarm.SharedPreferences.SEEKBAR_INPUT_VALUE
import app.pengalatdite.batteryalarm.SharedPreferences.SHARED_PREF
import app.pengalatdite.batteryalarm.SharedPreferences.SWITCH_VALUE
import app.pengalatdite.batteryalarm.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var presenter: MainActivityPresenter

    companion object {
        const val CHARGING = "Charging"
        const val NOT_CHARGING = "Not Charging"
        const val SWITCH_IS_ON = true
        const val SWITCH_IS_OFF = false
        const val SEEKBAR_INITIAL_VALUE = 90
        const val SEEKBAR_MAX_VALUE = 100
        var SEEKBAR_SAVED_VALUE = 0

        const val CHANNEL_ID = "1"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val batteryStatus: Intent? =
            IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { intentFilter ->
                baseContext.registerReceiver(null, intentFilter)
            }

        presenter = MainActivityPresenter(this, batteryStatus)

        binding.batteryStatus.text = getChargingStatus()
        binding.batteryPercentage.text = getBatteryPercentage()

        val sharedPref = this.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
        SEEKBAR_SAVED_VALUE =
            sharedPref.getInt(SEEKBAR_INPUT_VALUE, SEEKBAR_INITIAL_VALUE)

        val seek: SeekBar = binding.seekBar

        if (SEEKBAR_SAVED_VALUE != 0) {
            seek.incrementProgressBy(SEEKBAR_SAVED_VALUE)
            binding.seekBarValue.text = SEEKBAR_SAVED_VALUE.toString()
        } else {
            seek.incrementProgressBy(SEEKBAR_INITIAL_VALUE)
            binding.seekBarValue.text = SEEKBAR_INITIAL_VALUE.toString()
        }

        seek.max = SEEKBAR_MAX_VALUE
        seek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            var barProgress: Int = 0
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.seekBarValue.text = progress.toString()
                barProgress = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                with(sharedPref.edit()) {
                    putInt(SEEKBAR_INPUT_VALUE, barProgress)
                    apply()
                }
            }
        })

        binding.switchOnOff.isChecked = getSwitchStatus()
        binding.switchOnOff.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                with(sharedPref.edit()) {
                    putBoolean(SWITCH_VALUE, binding.switchOnOff.isChecked)
                    putBoolean(SWITCH_VALUE, true)
                    apply()
                }
                countDown()
            } else {
                with(sharedPref.edit()) {
                    putBoolean(SWITCH_VALUE, false)
                    apply()
                }
            }
        }

//        createNotificationChannel()

    }

    private fun getSwitchStatus(): Boolean {
        return if (presenter.switchIsChecked()) {
            SWITCH_IS_ON
        } else {
            SWITCH_IS_OFF
        }
    }

    private fun getChargingStatus(): String {
        Log.d("MainActivity", "getChargingStatus: ${presenter.batteryIsCharging()}")
        return if (presenter.batteryIsCharging()) {
            CHARGING
        } else {
            NOT_CHARGING
        }
    }

    private fun getBatteryPercentage(): String {
        return presenter.getBatteryChargingPercentage()?.toInt().toString()
    }

    private fun notification() {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_notifications_active)
            .setContentTitle("Battery Manager")
            .setContentText(getChargingStatus())
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)) {
            notify(10, builder.build())
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Channel Name"
            val descriptionText = "Channel Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun worker() {
        val request = OneTimeWorkRequestBuilder<CountDownWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()

        WorkManager.getInstance(this)
            .enqueue(request)
    }

    private fun countDown() {
        Log.d("Timer", "countDown: $SEEKBAR_SAVED_VALUE")
        object : CountDownTimer(((SEEKBAR_SAVED_VALUE * 1000).toLong()), 1000) {
            override fun onTick(p0: Long) {
                Log.d("Timer", "onTick: $p0")
            }

            override fun onFinish() {
                Log.d("Timer", "Finished")
                notification()
            }
        }.start()
    }
}