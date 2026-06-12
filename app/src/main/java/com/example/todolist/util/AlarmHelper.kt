package com.example.todolist.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.todolist.receiver.TodoAlarmReceiver

class AlarmHelper(private val context: Context) {
    private val alarmManager: AlarmManager by lazy {
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    // 设置事务截止精准闹钟
    fun setDeadlineAlarm(todoAffId: Long, deadlineTime: Long) {
        val intent = Intent(context, TodoAlarmReceiver::class.java).apply {
            putExtra(TodoAlarmReceiver.EXTRA_TODO_ID, todoAffId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            todoAffId.toInt(),
            intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            else PendingIntent.FLAG_UPDATE_CURRENT
        )
        // 精准闹钟，休眠也能唤醒，到期立刻执行广播
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, deadlineTime, pendingIntent)
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, deadlineTime, pendingIntent)
        }
    }

    // 删除事务时取消闹钟
    fun cancelAlarm(todoAffId: Long) {
        val intent = Intent(context, TodoAlarmReceiver::class.java).apply {
            putExtra(TodoAlarmReceiver.EXTRA_TODO_ID, todoAffId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            todoAffId.toInt(),
            intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            else PendingIntent.FLAG_NO_CREATE
        )
        pendingIntent?.let { alarmManager.cancel(it) }
    }
}