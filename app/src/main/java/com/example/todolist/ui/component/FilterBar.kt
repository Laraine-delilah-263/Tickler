package com.example.todolist.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

//分类筛选列表页面
@Composable
fun FilterBar(
    textColor: Color,
    mainColor: Color,
    dividerColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            listOf("全部标签", "行程备忘", "待办事项", "用车记录", "生活备忘").forEach { tag ->
                FilterChip(
                    text = tag,
                    textColor = textColor,
                    selectColor = mainColor
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            listOf("全部等级", "紧急", "重要", "常规", "暂缓").forEach { level ->
                FilterChip(
                    text = level,
                    textColor = textColor,
                    selectColor = mainColor
                )
            }
        }

        Divider(color = dividerColor, thickness = 1.dp)
    }
}

@Composable
fun FilterChip(
    text: String,
    textColor: Color,
    selectColor: Color
) {
    Box(
        modifier = Modifier
            .background(
                color = selectColor.copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable { }
    ) {
        Text(text = text, color = textColor)
    }
}