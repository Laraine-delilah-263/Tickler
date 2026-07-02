package com.example.todolist.ui.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.todolist.model.dao.TodoJoinData
import com.example.todolist.model.entity.Category
import com.example.todolist.model.entity.Priority
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoDetailDialog(
    todo: TodoJoinData,
    textColor: Color,
    bgCardColor: Color,
    categoryList: List<Category>,
    priorityList: List<Priority>,
    onUpdateTodo: (title: String, content: String, cateId: Long?, prioId: Long?, endTimeMs: Long) -> Unit,
    onClose: () -> Unit
) {
    var isEditMode by remember { mutableStateOf(false) }

    var titleText by remember { mutableStateOf(todo.title) }
    var contentText by remember { mutableStateOf(todo.detail) }
    var selectedCate by remember { mutableStateOf(categoryList.find { it.cataId == todo.categoryId }) }
    var selectedPrio by remember { mutableStateOf(priorityList.find { it.prioId == todo.priorityId }) }

    var cateExpanded by remember { mutableStateOf(false) }
    var prioExpanded by remember { mutableStateOf(false) }
    var showDatePage by remember { mutableStateOf(true) }

    // 禁用过去日期，只能选择今日及以后
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = todo.endTime,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val today = LocalDate.now()
                val targetDate = Instant.ofEpochMilli(utcTimeMillis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                return !targetDate.isBefore(today)
            }
        }
    )

    val localDateTime =
        LocalDateTime.ofInstant(Instant.ofEpochMilli(todo.endTime), ZoneId.systemDefault())
    val timePickerState = rememberTimePickerState(
        initialHour = localDateTime.hour,
        initialMinute = localDateTime.minute,
        is24Hour = true
    )

    LaunchedEffect(datePickerState.selectedDateMillis) {
        if (datePickerState.selectedDateMillis != null) {
            showDatePage = false
        }
    }

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Row(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight()
                .sizeIn(
                    maxWidth = if (isEditMode) 820.dp else 420.dp,
                    maxHeight = if (isEditMode) 680.dp else 400.dp
                )
                .background(color = bgCardColor, shape = RoundedCornerShape(12.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            if (!isEditMode) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "待办详情",
                            color = textColor,
                            fontSize = androidx.compose.material3.MaterialTheme.typography.titleMedium.fontSize
                        )
                        Button(onClick = { isEditMode = true }) {
                            Text("编辑")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("标题：${todo.title}", color = textColor)
                        Text("内容：${todo.detail}", color = textColor)
                        Text("分类：${todo.label ?: "未分类"}", color = textColor)
                        Text("优先级：${todo.levelName}", color = textColor)
                        Text(
                            "截止时间：${
                                localDateTime.format(
                                    java.time.format.DateTimeFormatter.ofPattern(
                                        "yyyy-MM-dd HH:mm"
                                    )
                                )
                            }",
                            color = textColor
                        )
                        val finishStr = if (todo.isFinish == 1) "已完成" else "未完成"
                        Text("状态：$finishStr", color = textColor)
                    }
                }
            } else {
                // 左侧表单
                Column(
                    modifier = Modifier
                        .weight(0.4f)
                        .padding(vertical = 16.dp, horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "编辑待办事务",
                            color = textColor,
                            fontSize = androidx.compose.material3.MaterialTheme.typography.titleMedium.fontSize
                        )
                    }

                    OutlinedTextField(
                        value = titleText,
                        onValueChange = { titleText = it },
                        label = { Text("标题", color = textColor) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = contentText,
                        onValueChange = { contentText = it },
                        label = { Text("事务内容", color = textColor) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .heightIn(min = 100.dp),
                        maxLines = 3
                    )

                    ExposedDropdownMenuBox(
                        expanded = cateExpanded,
                        onExpandedChange = { cateExpanded = !cateExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedCate?.label ?: "暂无分类",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("事务分类", color = textColor) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cateExpanded) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = cateExpanded,
                            onDismissRequest = { cateExpanded = false },
                            matchAnchorWidth = true
                        ) {
                            categoryList.forEach { cate ->
                                DropdownMenuItem(
                                    text = { Text(cate.label) },
                                    onClick = {
                                        selectedCate = cate
                                        cateExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    ExposedDropdownMenuBox(
                        expanded = prioExpanded,
                        onExpandedChange = { prioExpanded = !prioExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedPrio?.levelName ?: "暂无优先级",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("事务优先级", color = textColor) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = prioExpanded) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = prioExpanded,
                            onDismissRequest = { prioExpanded = false },
                            matchAnchorWidth = true
                        ) {
                            priorityList.forEach { prio ->
                                DropdownMenuItem(
                                    text = { Text(prio.levelName) },
                                    onClick = {
                                        selectedPrio = prio
                                        prioExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                val zone = ZoneId.systemDefault()
                                val selectedDate =
                                    datePickerState.selectedDateMillis ?: todo.endTime
                                val date =
                                    Instant.ofEpochMilli(selectedDate).atZone(zone).toLocalDate()
                                val finalTime = date
                                    .atTime(timePickerState.hour, timePickerState.minute)
                                    .atZone(zone)
                                    .toInstant()
                                    .toEpochMilli()

                                onUpdateTodo(
                                    titleText,
                                    contentText,
                                    selectedCate?.cataId,
                                    selectedPrio?.prioId,
                                    finalTime
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("保存修改")
                        }
                        Button(
                            onClick = onClose,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("取消")
                        }
                    }
                }

                Divider(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight(),
                    color = textColor.copy(alpha = 0.2f)
                )

                Column(
                    modifier = Modifier
                        .weight(0.6f)
                        .padding(start = 16.dp)
                        .heightIn(max = 550.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (showDatePage) "选择截止日期" else "选择截止时分",
                        color = textColor,
                        fontSize = androidx.compose.material3.MaterialTheme.typography.titleMedium.fontSize
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .widthIn(max = 360.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxHeight(0.85f)) {
                            if (showDatePage) {
                                DatePicker(
                                    state = datePickerState,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            } else {
                                TimePicker(
                                    state = timePickerState,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        Button(
                            onClick = { showDatePage = !showDatePage },
                            modifier = Modifier.align(Alignment.BottomEnd)
                        ) {
                            Text(if (showDatePage) "下一步" else "上一步")
                        }
                    }
                }
            }
        }
    }
}