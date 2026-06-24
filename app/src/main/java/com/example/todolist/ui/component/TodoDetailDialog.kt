package com.example.todolist.ui.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.todolist.model.dao.TodoJoinData
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TodoDetailDialog(
    todo: TodoJoinData,
    textColor: androidx.compose.ui.graphics.Color,
    cardBg: androidx.compose.ui.graphics.Color,
    onClose: () -> Unit
) {
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    AlertDialog(
        onDismissRequest = onClose,
        containerColor = cardBg,
        title = {
            Text(text = "待办详情", color = textColor, style = MaterialTheme.typography.headlineSmall)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("标题：${todo.title}", color = textColor)
                Text("内容：${todo.detail}", color = textColor)
                Text("分类：${todo.label ?: "未分类"}", color = textColor)
                Text("优先级：${todo.levelName}", color = textColor)
                Text("截止时间：${Instant.ofEpochMilli(todo.endTime).atZone(ZoneId.systemDefault()).format(dateFormatter)}", color = textColor)
                val finishText = if (todo.isFinish == 1) "已完成" else "未完成"
                Text("状态：$finishText", color = textColor)
            }
        },
        // 删除confirmButton、dismissButton，无任何按钮
        confirmButton = {},
        dismissButton = {}
    )
}