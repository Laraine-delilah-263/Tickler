package com.example.todolist.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.todolist.dao.TodoJoinData
import com.example.todolist.util.getPriorityColor

//待办事务列表：接收数据库联查完整数据，移除假数据
@Composable
fun NoteListArea(
    todoList: List<TodoJoinData>,
    cardBg: Color,
    textColor: Color,
    selectStroke: Color,
    mainColor: Color
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(todoList) { todo ->
            // 1. 根据优先级名称获取左侧竖条颜色
            val priorityColor = getPriorityColor(todo.levelName)
            // 2. 拼接文本格式：标题 | 详情
            val showText = "${todo.title} | ${todo.detail}"
            // 3. 右侧标签显示分类，无分类则展示「未分类」
            val tagText = todo.label ?: "未分类"

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(BorderStroke(1.dp, selectStroke), RoundedCornerShape(8.dp)),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    //左侧：优先级彩色竖线 + 标题|详情文本
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(40.dp)
                                .background(priorityColor)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = showText,
                            color = textColor,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                    //右侧：圆角分类标签，背景
                    Text(
                        text = tagText,
                        color = Color.White,
                        modifier = Modifier
                            .background(mainColor, RoundedCornerShape(99.dp))
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    )
                }
            }
        }
    }
}