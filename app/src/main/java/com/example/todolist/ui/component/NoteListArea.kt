package com.example.todolist.ui.component

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.todolist.R
import com.example.todolist.model.dao.TodoJoinData
import com.example.todolist.util.getPriorityColor
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import androidx.compose.ui.text.style.TextDecoration

// 滑动枚举别名简化
private val StartToEnd = SwipeToDismissBoxValue.StartToEnd
private val EndToStart = SwipeToDismissBoxValue.EndToStart
private val Settled = SwipeToDismissBoxValue.Settled

@SuppressLint("RememberReturnType")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NoteListArea(
    todoList: List<TodoJoinData>,
    cardBg: Color,
    textColor: Color,
    selectStroke: Color,
    mainColor: Color,
    onDeleteTodo: (Long) -> Unit,
    onMarkComplete: (Long) -> Unit,
    onOrderChanged: (List<TodoJoinData>) -> Unit
) {
    val dateFormatter = remember {
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(todoList, key = { it.affId }) { todo ->
//            判断是否过期
            val nowMs= System.currentTimeMillis()
            val isOverdue=todo.endTime<nowMs||todo.isExpired==1
            val isFinished=todo.isFinish==1
            val showGrayBg=isOverdue&&!isFinished
            // 1. 增加阈值，滑动超过距离才显示背景
            val swipeState = rememberSwipeToDismissBoxState(
                confirmValueChange = { dismissValue ->
                    when (dismissValue) {
                        StartToEnd -> {
                            onMarkComplete(todo.affId)
                            false
                        }
                        EndToStart -> {
                            onDeleteTodo(todo.affId)
                            true
                        }
                        else -> false
                    }
                },
                positionalThreshold = { totalWidth -> totalWidth * 0.3f } // 滑动30%宽度触发变色
            )

            SwipeToDismissBox(
                state = swipeState,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                backgroundContent = {
                    // 修复点2：统一填充全屏背景，渐变起始色改为透明，滑动立刻可见
                    val progress = swipeState.progress
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                when (swipeState.targetValue) {
                                    StartToEnd -> lerp(Color.Transparent, Color(0xFF2196F3), progress)
                                    EndToStart -> lerp(Color.Transparent, Color(0xFFF44336), progress)
                                    else -> Color.Transparent
                                }
                            ),
                        contentAlignment = when (swipeState.targetValue) {
                            StartToEnd -> Alignment.CenterStart
                            EndToStart -> Alignment.CenterEnd
                            else -> Alignment.Center
                        }
                    ) {
                        when (swipeState.targetValue) {
                            StartToEnd -> {
                                Icon(
                                    painter = painterResource(id = R.drawable.check),
                                    contentDescription = "标记完成",
                                    tint = Color.White,
                                    modifier = Modifier.padding(horizontal = 20.dp)
                                )
                            }
                            EndToStart -> {
                                Icon(
                                    painter = painterResource(id = R.drawable.delete),
                                    contentDescription = "删除事务",
                                    tint = Color.White,
                                    modifier = Modifier.padding(horizontal = 20.dp)
                                )
                            }
                            else -> Unit
                        }
                    }
                }
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(BorderStroke(1.dp, selectStroke), RoundedCornerShape(8.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = if (showGrayBg) cardBg.copy(alpha = 0.6f) else cardBg),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column {
                        Text(
                            text = Instant
                                .ofEpochMilli(todo.endTime)
                                .atZone(ZoneId.systemDefault())
                                .format(dateFormatter),
                            color = Color.Gray.copy(alpha = 0.7f),
                            modifier = Modifier.padding(start = 16.dp, top = 5.dp),
                            fontSize = androidx.compose.material3.MaterialTheme.typography.bodySmall.fontSize
                        )
                        Row(
                            modifier = Modifier
                                .padding(start = 16.dp, end = 16.dp, bottom = 10.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier
                                        .width(4.dp)
                                        .height(40.dp)
                                        .background(getPriorityColor(todo.levelName))
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "${todo.title} | ${todo.detail}",
                                    color = textColor,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(end = 8.dp),
                                    textDecoration = if (isFinished) TextDecoration.LineThrough else TextDecoration.None
                                )
                            }
                            Text(
                                text = todo.label ?: "未分类",
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
    }
}