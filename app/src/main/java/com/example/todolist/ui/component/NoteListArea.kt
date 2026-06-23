package com.example.todolist.ui.component

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.todolist.model.dao.TodoJoinData
import com.example.todolist.util.getPriorityColor
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import com.example.todolist.R
import androidx.compose.runtime.LaunchedEffect

// 滑动枚举别名简化
private val StartToEnd = SwipeToDismissBoxValue.StartToEnd
private val EndToStart = SwipeToDismissBoxValue.EndToStart

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
    onOrderChanged: (List<TodoJoinData>) -> Unit,
    batchMode: Boolean,
    selectedIds: List<Long>,
    onToggleSelect: (Long) -> Unit
) {
    val dateFormatter = remember {
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    }
    // 绑定LazyColumn滚动状态
    val lazyListState = rememberLazyListState()
// 拖拽排序核心状态，交换数据后回调onOrderChanged
    val reorderState = rememberReorderableLazyListState(lazyListState) { fromItem, toItem ->
        val newList = todoList.toMutableList()
        val moveItem = newList.removeAt(fromItem.index)
        newList.add(toItem.index, moveItem)
        // 将新顺序传给外层MainActivity持久化
        onOrderChanged(newList)
    }


    LazyColumn(
        state = lazyListState,//新增绑定滚动状态
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(todoList, key = { it.affId }) { todo ->
            // 过期、完成状态判断
            val nowMs = System.currentTimeMillis()
            val isOverdue = todo.endTime < nowMs || todo.isExpired == 1
            val isFinished = todo.isFinish == 1
            val isDimStyle = isOverdue && !isFinished
            val dimAlpha = if (isDimStyle) 0.55f else 1f


            val swipeState = rememberSwipeToDismissBoxState(
                confirmValueChange = { dismissValue ->
                    if (batchMode) return@rememberSwipeToDismissBoxState false
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
                positionalThreshold = { totalWidth -> totalWidth * 0.3f }
            )

            if (batchMode) {
                // 批量模式：不开启拖拽，直接渲染卡片
                SwipeToDismissBox(
                    state = swipeState,
                    enableDismissFromStartToEnd = false,
                    enableDismissFromEndToStart = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    backgroundContent = {
                        val progress = swipeState.progress
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    when (swipeState.targetValue) {
                                        StartToEnd -> lerp(
                                            Color.Transparent,
                                            Color(0xFF2196F3),
                                            progress
                                        )

                                        EndToStart -> lerp(
                                            Color.Transparent,
                                            Color(0xFFF44336),
                                            progress
                                        )

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
                            .border(
                                BorderStroke(1.dp, selectStroke.copy(alpha = dimAlpha)),
                                RoundedCornerShape(8.dp)
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = cardBg.copy(alpha = dimAlpha)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column {
                            Text(
                                text = Instant
                                    .ofEpochMilli(todo.endTime)
                                    .atZone(ZoneId.systemDefault())
                                    .format(dateFormatter),
                                color = Color.Gray.copy(alpha = 0.7f * dimAlpha),
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
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    if (batchMode) {
                                        androidx.compose.material3.Checkbox(
                                            checked = selectedIds.contains(todo.affId),
                                            onCheckedChange = {
                                                onToggleSelect(todo.affId)
                                            },
                                            colors = androidx.compose.material3.CheckboxDefaults.colors(
                                                checkedColor = mainColor
                                            )
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .width(4.dp)
                                            .height(40.dp)
                                            .background(getPriorityColor(todo.levelName).copy(alpha = dimAlpha))
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "${todo.title} | ${todo.detail}",
                                        color = textColor.copy(alpha = dimAlpha),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.padding(end = 8.dp),
                                        textDecoration = if (isFinished) TextDecoration.LineThrough else TextDecoration.None
                                    )
                                }
                                Text(
                                    text = todo.label ?: "未分类",
                                    color = Color.White.copy(alpha = dimAlpha),
                                    modifier = Modifier
                                        .background(
                                            mainColor.copy(alpha = dimAlpha),
                                            RoundedCornerShape(99.dp)
                                        )
                                        .padding(horizontal = 10.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            } else {
                // 非批量模式：启用整行拖拽
                ReorderableItem(
                    state = reorderState,
                    key = todo.affId
                ) { isDragging ->
                    // isDragging：拖拽时的状态，添加缩放/阴影视觉效果
                    val dragScale = if (isDragging) 1.02f else 1f
                    val dragElevation = if (isDragging) 8.dp else 0.dp

                    SwipeToDismissBox(
                        state = swipeState,
                        enableDismissFromStartToEnd = true,
                        enableDismissFromEndToStart = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .scale(dragScale) // 拖拽轻微放大
                            .pointerInput(Unit){
                                detectVerticalDragGestures{_,_->}
                            },
                        backgroundContent = {
                            val progress = swipeState.progress
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        when (swipeState.targetValue) {
                                            StartToEnd -> lerp(
                                                Color.Transparent,
                                                Color(0xFF2196F3),
                                                progress
                                            )

                                            EndToStart -> lerp(
                                                Color.Transparent,
                                                Color(0xFFF44336),
                                                progress
                                            )

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
                                .shadow(dragElevation, RoundedCornerShape(8.dp)) // 拖拽增加阴影
                                .border(
                                    BorderStroke(1.dp, selectStroke.copy(alpha = dimAlpha)),
                                    RoundedCornerShape(8.dp)
                                ),
                            colors = CardDefaults.cardColors(
                                containerColor = cardBg.copy(alpha = dimAlpha)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column {
                                Text(
                                    text = Instant
                                        .ofEpochMilli(todo.endTime)
                                        .atZone(ZoneId.systemDefault())
                                        .format(dateFormatter),
                                    color = Color.Gray.copy(alpha = 0.7f * dimAlpha),
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
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        // 批量模式才显示复选框，拖拽模式隐藏
                                        Box(
                                            modifier = Modifier
                                                .width(4.dp)
                                                .height(40.dp)
                                                .background(
                                                    getPriorityColor(todo.levelName).copy(
                                                        alpha = dimAlpha
                                                    )
                                                )
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = "${todo.title} | ${todo.detail}",
                                            color = textColor.copy(alpha = dimAlpha),
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.padding(end = 8.dp),
                                            textDecoration = if (isFinished) TextDecoration.LineThrough else TextDecoration.None
                                        )
                                    }
                                    Text(
                                        text = todo.label ?: "未分类",
                                        color = Color.White.copy(alpha = dimAlpha),
                                        modifier = Modifier
                                            .background(
                                                mainColor.copy(alpha = dimAlpha),
                                                RoundedCornerShape(99.dp)
                                            )
                                            .padding(horizontal = 10.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}