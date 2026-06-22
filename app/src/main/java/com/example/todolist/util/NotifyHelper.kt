package com.example.todolist.util

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.todolist.R
import com.example.todolist.model.entity.TodoAffair

class NotifyHelper(private val context: Context) {
    // 渠道ID（全局统一，教材要求Builder与渠道ID一致）
    companion object {
        const val CHANNEL_ID = "todo_deadline_channel"
        const val CHANNEL_NAME = "待办截止提醒"
    }

    // ========== 教材标准第一步：创建通知渠道（onCreate执行一次） ==========
    fun createNotifyChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
            val channel = android.app.NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                android.app.NotificationManager.IMPORTANCE_HIGH
            )
            manager.createNotificationChannel(channel)
        }
    }

    // ========== 教材标准第二步：构建并弹出通知 ==========
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showDeadlineNotify(todo: TodoAffair) {
        // 教材标准Builder：传context + 渠道ID
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("待办已到期：${todo.title}")    // 标题
            .setContentText(todo.detail)                    // 正文
            .setSmallIcon(R.drawable.logo2)                // 状态栏小图标（教材必填）
            .setAutoCancel(true)
        val notification = builder.build()

        // 教材标准第三步：notify，affId作为唯一通知ID，保证不重复覆盖
        NotificationManagerCompat.from(context).notify(todo.affId.toInt(), notification)
    }
}