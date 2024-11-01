package com.example.opsc_poe_part_2

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.hash.HashCode
import java.time.ZoneId

/*
    Code Attribution
    Title: THIS Is How You Schedule Alarms on Android with AlarmManager
    Author: Philipp Lackner
    Post Link: https://www.youtube.com/watch?v=mWb_hEBLIqA&t=518s
    Usage: learned to schedule notifications
*/
class GoalAlarmScheduler (private val context: Context): AlarmScheduler {
    private  val alarmManager = context.getSystemService(AlarmManager::class.java)
    override fun schedule(item: AlarmItem) {
        val intent = Intent(context, AlarmReciever::class.java).apply {
            putExtra("goal", item.goalString)
        }

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            item.time.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000,
            PendingIntent.getBroadcast(
                context,
                item.id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    override fun cancel(item: AlarmItem) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                item.id,
                Intent(context, AlarmReciever::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }
}