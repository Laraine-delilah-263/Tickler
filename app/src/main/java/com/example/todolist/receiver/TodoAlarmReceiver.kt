package com.example.todolist.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import kotlinx.coroutines.runBlocking
import com.example.todolist.model.database.AppDatabase
import com.example.todolist.util.NotifyHelper

class TodoAlarmReceiver : BroadcastReceiver() {
    companion object {
        const val EXTRA_TODO_ID = "target_todo_aff_id"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        context ?: return
        val todoId = intent?.getLongExtra(EXTRA_TODO_ID, -1) ?: return
        if (todoId == -1L) return

        // 协程查询数据库、发通知、标记isExpired=1防重复
        runBlocking {
            val dao = AppDatabase.getDatabase(context).todoDao()
            val targetTodo = dao.getTodoById(todoId) ?: return@runBlocking
            // 只有未过期才发通知
            if (targetTodo.isExpired == 0) {
                // ========== 新增权限校验，消除IDE权限警告 ==========
                var hasNotifyPermission = true
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    hasNotifyPermission = ContextCompat.checkSelfPermission(
                        context,
                        android.Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                }
                // 拥有通知权限才执行推送
                if (hasNotifyPermission) {
                    val notifyHelper = NotifyHelper(context)
                    notifyHelper.showDeadlineNotify(targetTodo)
                }
                // 无论是否有权限，都标记已过期，避免反复触发闹钟逻辑
                dao.markTodoExpired(todoId)
            }
        }

    }
}