package com.example.todolist.util

import androidx.compose.ui.graphics.Color
import com.example.todolist.model.Enum.PriorityEnum
import com.example.todolist.ui.theme.*

fun getPriorityColor(level: String?): Color {
    return when{
        level=="已完成"->PriFinish
        else -> {
            val priority= PriorityEnum.getByDbLabel(level)
            priority?.color?:priorityDefault
        }
    }
}