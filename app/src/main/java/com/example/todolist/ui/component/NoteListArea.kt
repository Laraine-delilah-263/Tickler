package com.example.todolist.ui.component

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.todolist.R
import com.example.todolist.model.dao.TodoJoinData
import com.example.todolist.util.getPriorityColor
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

// 滑动枚举别名简化
private val StartToEnd = SwipeToDismissBoxValue.StartToEnd
private val EndToStart = SwipeToDismissBoxValue.EndToStart

// 行坐标数据类：存储单条事务窗口坐标
data class TodoRowBounds(
    val affId: Long,
    val index: Int,
    val top: Float,
    val bottom: Float
)

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
    onToggleSelect: (Long) -> Unit,
    onClickTodoItem: (TodoJoinData) -> Unit
) {
    val dateFormatter = remember {
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    }
    val density = LocalDensity.current
    val autoScrollEdgePx = remember { with(density) { 40.dp.toPx() } }
    val autoScrollStepPx = remember { with(density) { 1.5.dp.toPx() } }

    // 拖拽状态
    var draggingAffId by remember { mutableStateOf<Long?>(null) }
    var dragStartIndex by remember { mutableIntStateOf(-1) }
    var dragInsertionIndex by remember { mutableIntStateOf(-1) }
    var dragStartFingerY by remember { mutableFloatStateOf(0f) }
    var dragOffset by remember { mutableFloatStateOf(0f) }
    var dragScrollOffset by remember { mutableFloatStateOf(0f) }

    val rowBounds = remember { mutableMapOf<Long, TodoRowBounds>() }
    var dragRowBounds by remember { mutableStateOf<Map<Long, TodoRowBounds>>(emptyMap()) }
    var dragSnapshotList by remember { mutableStateOf<List<TodoJoinData>>(emptyList()) }

    var listViewportTop by remember { mutableFloatStateOf(0f) }
    var listViewportBottom by remember { mutableFloatStateOf(0f) }
    val lazyListState = rememberLazyListState()

    val stableTodoList = rememberUpdatedState(todoList)
    val stableOnOrderChanged = rememberUpdatedState(onOrderChanged)

    // 边缘自动滚动
    LaunchedEffect(draggingAffId) {
        while (draggingAffId != null) {
            val fingerY = dragStartFingerY + dragOffset - dragScrollOffset
            var scrollDelta = 0f
            if (fingerY < listViewportTop + autoScrollEdgePx) scrollDelta = -autoScrollStepPx
            if (fingerY > listViewportBottom - autoScrollEdgePx) scrollDelta = autoScrollStepPx

            if (scrollDelta != 0f) {
                val consumed = lazyListState.scrollBy(scrollDelta)
                if (consumed != 0f) dragScrollOffset += consumed
            }
            withFrameNanos { }
        }
    }

    // 拖拽开始：快照完整列表+坐标，彻底隔离实时列表干扰
    fun onDragStart(fingerY: Float, startIdx: Int, affId: Long) {
        draggingAffId = affId
        dragStartIndex = startIdx
        dragInsertionIndex = startIdx
        dragStartFingerY = fingerY
        dragOffset = 0f
        dragScrollOffset = 0f
        dragSnapshotList = stableTodoList.value.toList()
        dragRowBounds = rowBounds.toMap()
    }

    // 拖拽移动：基于快照列表遍历，支持远距离跨条目交换
    fun onDragMove(deltaY: Float) {
        dragOffset += deltaY
        val fingerScreenY = dragStartFingerY + dragOffset - dragScrollOffset
        val boundsMap = dragRowBounds
        val snapshotItems = dragSnapshotList
        val sortedBounds = boundsMap.values.sortedBy { it.index }
        var targetInsert = snapshotItems.size

        // 遍历快照所有行，匹配视觉位置
        for (bounds in sortedBounds) {
            val visualTop = bounds.top - dragScrollOffset
            val visualBottom = bounds.bottom - dragScrollOffset
            val visualCenter = (visualTop + visualBottom) / 2f

            when {
                fingerScreenY < visualCenter -> {
                    targetInsert = bounds.index
                    break
                }
                fingerScreenY < visualBottom -> {
                    targetInsert = bounds.index + 1
                    break
                }
                else -> targetInsert = bounds.index + 1
            }
        }
        dragInsertionIndex = targetInsert.coerceIn(0, snapshotItems.size)
    }

    // 拖拽结束：任意两个位置交换逻辑修复
    fun onDragEnd() {
        val fromIdx = dragStartIndex
        val insertIdx = dragInsertionIndex
        val snapshot = dragSnapshotList.toMutableList()

        if (fromIdx in snapshot.indices && insertIdx in 0..snapshot.size && fromIdx != insertIdx) {
            val targetIndex = if (insertIdx > fromIdx) insertIdx - 1 else insertIdx
            val item = snapshot.removeAt(fromIdx)
            snapshot.add(targetIndex.coerceIn(0, snapshot.size), item)
            stableOnOrderChanged.value(snapshot)
        }

        // 重置拖拽状态
        draggingAffId = null
        dragStartIndex = -1
        dragInsertionIndex = -1
        dragRowBounds = emptyMap()
        dragSnapshotList = emptyList()
        dragOffset = 0f
        dragScrollOffset = 0f
    }

    LazyColumn(
        state = lazyListState,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .onGloballyPositioned { coordinates ->
                val top = coordinates.positionInWindow().y
                listViewportTop = top
                listViewportBottom = top + coordinates.size.height
            },
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(todoList, key = { it.affId }) { todo ->
            // 稳定获取当前索引，替代不稳定 indexOf
            val currentIndex = todoList.indexOfFirst { it.affId == todo.affId }
            val nowMs = System.currentTimeMillis()
            val isOverdue = todo.endTime < nowMs || todo.isExpired == 1
            val isFinished = todo.isFinish == 1
            val isDimStyle = isOverdue && !isFinished
            val dimAlpha = if (isDimStyle) 0.55f else 1f
            val isDraggingCurrent = draggingAffId == todo.affId

            // 拖动时中间条目位移计算（适配远距离拖动区间）
            val rowDisplacement = run {
                val start = dragStartIndex
                val insert = dragInsertionIndex
                if (draggingAffId == null || start == -1 || insert == -1) return@run 0f
                val bounds = dragRowBounds[todo.affId] ?: return@run 0f
                val itemHeight = bounds.bottom - bounds.top

                when {
                    insert > start && currentIndex in (start + 1) until insert -> -itemHeight
                    insert < start && currentIndex in insert until start -> itemHeight
                    else -> 0f
                }
            }

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

            SwipeToDismissBox(
                state = swipeState,
                enableDismissFromStartToEnd = !batchMode,
                enableDismissFromEndToStart = !batchMode,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .scale(if (isDraggingCurrent) 1.02f else 1f)
                    .graphicsLayer {
                        translationY = if (isDraggingCurrent) dragOffset else rowDisplacement
                        alpha = if (isDraggingCurrent) 0.9f else 1f
                    },
                backgroundContent = {
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
                            StartToEnd -> Icon(
                                painter = painterResource(id = R.drawable.check),
                                contentDescription = "标记完成",
                                tint = Color.White,
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )
                            EndToStart -> Icon(
                                painter = painterResource(id = R.drawable.delete),
                                contentDescription = "删除事务",
                                tint = Color.White,
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )
                            else -> Unit
                        }
                    }
                }
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(if (isDraggingCurrent) 8.dp else 0.dp, RoundedCornerShape(8.dp))
                        .border(
                            BorderStroke(1.dp, selectStroke.copy(alpha = dimAlpha)),
                            RoundedCornerShape(8.dp)
                        )
                        .clickable(enabled = !batchMode && draggingAffId == null) {
                            onClickTodoItem(todo)
                        }
                        .onGloballyPositioned { coordinates ->
                            val topY = coordinates.positionInWindow().y
                            val bottomY = topY + coordinates.size.height
                            rowBounds[todo.affId] = TodoRowBounds(
                                affId = todo.affId,
                                index = currentIndex,
                                top = topY,
                                bottom = bottomY
                            )
                        },
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
                                        onCheckedChange = { onToggleSelect(todo.affId) },
                                        colors = androidx.compose.material3.CheckboxDefaults.colors(checkedColor = mainColor)
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
                            if (!batchMode) {
                                val stableIndex = rememberUpdatedState(currentIndex)
                                val stableAff = rememberUpdatedState(todo.affId)
                                IconButton(
                                    modifier = Modifier.pointerInput(todo.affId) {
                                        detectDragGesturesAfterLongPress(
                                            onDragStart = { offset ->
                                                onDragStart(offset.y, stableIndex.value, stableAff.value)
                                            },
                                            onDragEnd = { onDragEnd() },
                                            onDragCancel = { onDragEnd() },
                                            onDrag = { change, dragAmount ->
                                                change.consume()
                                                onDragMove(dragAmount.y)
                                            }
                                        )
                                    },
                                    onClick = {}
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.drag),
                                        contentDescription = "长按拖动排序",
                                        tint = textColor.copy(alpha = dimAlpha)
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