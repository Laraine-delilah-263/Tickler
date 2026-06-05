package com.example.todolist.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTodoDialog(
    show: Boolean,
    textColor: Color,
    bgCardColor: Color,
    closeDialog: () -> Unit
) {
    if (!show) return

    var titleText by remember { mutableStateOf("") }
    var contentText by remember { mutableStateOf("") }
    val dateState = rememberDatePickerState()

    // 页面步骤：0=选日期，1=选时分
    var step by remember { mutableStateOf(0) }

    // 下拉数据源
    val hourList = (0..23).map { it.toString() }
    var hourExpanded by remember { mutableStateOf(false) }
    var selectHour by remember { mutableStateOf("0") }

    val minuteList = (0..59).map { it.toString() }
    var minuteExpanded by remember { mutableStateOf(false) }
    var selectMinute by remember { mutableStateOf("0") }

    Dialog(onDismissRequest = {
        step = 0
        closeDialog()
    }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(color = bgCardColor, shape = RoundedCornerShape(12.dp))
                .padding(vertical = 16.dp, horizontal = 16.dp)
        ) {
            Text(
                text = if (step == 0) "第一步：选择截止日期" else "第二步：选择时分",
                color = textColor
            )
            Spacer(modifier = Modifier.height(10.dp))

            // 步骤1：日期选择
            if (step == 0) {
                DatePicker(
                    state = dateState,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                // 选完日期自动切时分页
                dateState.selectedDateMillis?.let {
                    step = 1
                }
            }

            // 步骤2：小时+分钟下拉
            if (step == 1) {
                //标题输入
                OutlinedTextField(
                    value = titleText,
                    onValueChange = { titleText = it },
                    label = { Text("标题", color = textColor) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))

                //内容输入
                OutlinedTextField(
                    value = contentText,
                    onValueChange = { contentText = it },
                    label = { Text("事务内容", color = textColor) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                //小时下拉
                ExposedDropdownMenuBox(
                    expanded = hourExpanded,
                    onExpandedChange = { hourExpanded = !hourExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectHour,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("小时(0~23)", color = textColor) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = hourExpanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = hourExpanded,
                        onDismissRequest = { hourExpanded = false }
                    ) {
                        hourList.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    selectHour = item
                                    hourExpanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))

                //分钟下拉
                ExposedDropdownMenuBox(
                    expanded = minuteExpanded,
                    onExpandedChange = { minuteExpanded = !minuteExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectMinute,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("分钟(0~59)", color = textColor) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = minuteExpanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = minuteExpanded,
                        onDismissRequest = { minuteExpanded = false }
                    ) {
                        minuteList.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    selectMinute = item
                                    minuteExpanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // 底部两个按钮垂直排布，解决按钮显示不全
            Column {
                Button(
                    onClick = {
                        step = 0
                        closeDialog()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("确认（暂不保存）")
                }
                Spacer(modifier = Modifier.height(6.dp))
                Button(
                    onClick = {
                        step = 0
                        closeDialog()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("取消")
                }
            }
        }
    }
}