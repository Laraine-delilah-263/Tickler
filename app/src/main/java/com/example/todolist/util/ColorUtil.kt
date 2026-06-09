package com.example.todolist.util

import androidx.compose.ui.graphics.Color

fun getPriorityColor(level: String?): Color {
    return when (level) {
        "紧急" -> Color(0xFFE53935)
        "重要" -> Color(0xFFF57C00)
        "常规" -> Color(0xFF2196F3)
        "暂缓" -> Color(0xFF757575)
        "已完成" -> Color(0xFF81C784)
        else -> Color.Gray
    }
}