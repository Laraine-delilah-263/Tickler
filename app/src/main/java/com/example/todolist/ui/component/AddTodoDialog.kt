package com.example.todolist.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
    closeDialog: () -> Unit,
    saveClick: (content: String, h: String, m: String) -> Unit
) {
    if (!show) return

    var contentText by remember { mutableStateOf("") }

    //时分数据源
    val hourList = (0..23).map { it.toString() }
    var hourExpanded by remember { mutableStateOf(false) }
    var selectHour by remember { mutableStateOf("0") }

    val minuteList = (0..59).map { it.toString() }
    var minuteExpanded by remember { mutableStateOf(false) }
    var selectMinute by remember { mutableStateOf("0") }

    Dialog(onDismissRequest = closeDialog) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(color = bgCardColor, shape = RoundedCornerShape(12.dp))
                .padding(vertical = 16.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "新增待办事务", color = textColor)

            //第一行：仅内容输入，删除标题框
            OutlinedTextField(
                value = contentText,
                onValueChange = { contentText = it },
                label = { Text("事务内容", color = textColor) },
                modifier = Modifier.fillMaxWidth()
            )

//            年份选择


            //第二行：小时+分钟同一行，下拉框限制高度
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                //小时下拉
                ExposedDropdownMenuBox(
                    expanded = hourExpanded,
                    onExpandedChange = { hourExpanded = !hourExpanded },
                    modifier = Modifier.weight(1f)
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
                        onDismissRequest = { hourExpanded = false },
                        modifier = Modifier.heightIn(max = 180.dp) //下拉列表限高
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

                //分钟下拉
                ExposedDropdownMenuBox(
                    expanded = minuteExpanded,
                    onExpandedChange = { minuteExpanded = !minuteExpanded },
                    modifier = Modifier.weight(1f)
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
                        onDismissRequest = { minuteExpanded = false },
                        modifier = Modifier.heightIn(max = 180.dp) //下拉列表限高
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
            }

            //底部按钮
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                    onClick = {
                        saveClick(contentText, selectHour, selectMinute)
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
    }
}