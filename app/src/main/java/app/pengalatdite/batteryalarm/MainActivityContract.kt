package app.pengalatdite.batteryalarm

import android.content.Intent

interface MainActivityContract {
    fun getChargingStatus(batteryStatus: Intent?): Boolean

    fun getChargingPercentage(batteryStatus: Intent?): Float?
}