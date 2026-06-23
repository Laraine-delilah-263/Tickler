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
import com.example.todolist.model.entity.Category
import com.example.todolist.model.entity.Priority
import java.time.Instant
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTodoDialog(
    show: Boolean,
    textColor: Color,
    bgCardColor: Color,
    categoryList: List<Category>,
    priorityList: List<Priority>,
    closeDialog: () -> Unit,
    saveClick: (title: String, content: String, cateId: Long?, prioId: Long?, dateTimeMilli: Long) -> Unit
) {
    if (!show) return
    // 左侧输入数据
    var titleText by remember { mutableStateOf("") }
    var contentText by remember { mutableStateOf("") }

    // 分类下拉
    var cateExpanded by remember { mutableStateOf(false) }
    var selectedCate: Category? by remember { mutableStateOf(if (categoryList.isNotEmpty()) categoryList.first() else null) }

//    优先级下拉
    var prioExpanded by remember { mutableStateOf(false) }
    var selectedPriority: Priority? by remember { mutableStateOf(if (priorityList.isNotEmpty()) priorityList.first() else null) }

    // 右侧分步状态：true=选日历，false=选时分
    var showDatePage by remember { mutableStateOf(true) }

    // 日期选择器（禁用过去时间）
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val now = Instant.now().atZone(ZoneId.systemDefault()).toLocalDate()
                val targetDate =
                    Instant.ofEpochMilli(utcTimeMillis).atZone(ZoneId.systemDefault()).toLocalDate()
                // 目标日期 >= 今天 即可选择
                return !targetDate.isBefore(now)
            }
        }
    )

    // 时分选择器
    val timePickerState = rememberTimePickerState(
        initialHour = 8,
        initialMinute = 0,
        is24Hour = true
    )

    // 获取今天23:59的时间戳作为默认截止日期
    val defaultEndOfDay = remember {
        val nowLocalDate = Instant.now().atZone(ZoneId.systemDefault()).toLocalDate()
        val endOfDay = nowLocalDate.atTime(23, 59)
        endOfDay.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

//选择日期后自动切换分页
    LaunchedEffect(datePickerState.selectedDateMillis) {
        if (datePickerState.selectedDateMillis != null) {
            showDatePage = false
        }
    }


    Dialog(
        onDismissRequest = closeDialog,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Row(
            modifier = Modifier
//                宽度根据内部组件自动包裹
                .wrapContentWidth()
                .wrapContentHeight()
                .sizeIn(maxWidth = 800.dp, maxHeight = 650.dp)
                .background(color = bgCardColor, shape = RoundedCornerShape(12.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // ========== 左侧固定表单栏 ==========
            Column(
                modifier = Modifier
                    .weight(0.4f)
                    .padding(vertical = 16.dp, horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "新增待办事务",
                    color = textColor,
                    fontSize = androidx.compose.material3.MaterialTheme.typography.titleMedium.fontSize
                )
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
                        modifier = Modifier.menuAnchor()// 将此输入框作为下拉菜单的基准锚点
                    )
                    ExposedDropdownMenu(
                        expanded = cateExpanded,
                        onDismissRequest = { cateExpanded = false },
                        modifier = Modifier
                            .wrapContentWidth()
                            .heightIn(50.dp),
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
                        value = selectedPriority?.levelName ?: "暂无优先级",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("事务优先级", color = textColor) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = prioExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = prioExpanded,
                        onDismissRequest = { prioExpanded = false },
                        modifier = Modifier.heightIn(50.dp),
                        matchAnchorWidth = true
                    ) {
                        priorityList.forEach { prio ->
                            DropdownMenuItem(
                                text = { Text(prio.levelName) },
                                onClick = {
                                    selectedPriority = prio
                                    prioExpanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.weight(1f))

//                添加取消按钮
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            val zone = ZoneId.systemDefault()
                            val selectedDateMs =
                                datePickerState.selectedDateMillis ?: defaultEndOfDay
                            val selectedLocalDate = Instant.ofEpochMilli(selectedDateMs)
                                .atZone(zone)
                                .toLocalDate()
                            val fullDateTime = selectedLocalDate
                                .atTime(timePickerState.hour, timePickerState.minute)
                                .atZone(zone)
                                .toInstant()
                                .toEpochMilli()
                            saveClick(
                                titleText,
                                contentText,
                                selectedCate?.cataId,
                                selectedPriority?.prioId,
                                fullDateTime
                            )
                            closeDialog()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("添加")
                    }
                    Button(
                        onClick = closeDialog,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("取消")
                    }
                }
            }


            // 中间分割线
            Divider(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight(),
                color = textColor.copy(alpha = 0.2f)
            )

            // ========== 右侧分步切换区域 ==========
            Column(
                modifier = Modifier
                    .weight(0.6f)
                    .padding(start = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = if (showDatePage) "选择截止日期" else "选择截止时分",
                    color = textColor,
                    fontSize = androidx.compose.material3.MaterialTheme.typography.titleMedium.fontSize
                )

                // Box占满剩余宽度，按钮固定右下角不遮挡组件
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .widthIn(max = 400.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (showDatePage) {
                            DatePicker(
                                state = datePickerState,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(0.9f)
                            )
                        } else {
                            Text(text = "选择小时/分钟", color = textColor)
                            TimePicker(
                                state = timePickerState,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxWidth()
                            )
                        }
                    }
                    // 切换按钮固定右下角
                    Button(
                        onClick = { showDatePage = !showDatePage },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(top = 16.dp)
                    ) {
                        Text(if (showDatePage) "下一步" else "上一步")
                    }
                }
            }
        }
    }
}