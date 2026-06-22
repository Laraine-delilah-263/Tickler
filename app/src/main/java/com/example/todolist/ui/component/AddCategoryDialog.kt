package com.example.todolist.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import com.example.todolist.model.entity.Category

@Composable
fun AddCategoryDialog(
    show: Boolean,
    textColor: Color,
    bgCardColor: Color,
    closeDialog: () -> Unit,
    saveNewCategory: (String) -> Unit
) {
    if (!show) return

    var inputLabel by remember { mutableStateOf("") }

    Dialog(onDismissRequest = closeDialog) {
        Card(
            modifier = Modifier.fillMaxWidth(0.85f),
            shape = RoundedCornerShape(12.dp),
            colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = bgCardColor)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = "新增自定义分类标签", color = textColor, fontSize = androidx.compose.material3.MaterialTheme.typography.titleMedium.fontSize)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = inputLabel,
                    onValueChange = { inputLabel = it },
                    label = { Text("输入标签名称", color = textColor) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(20.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = {
                            if (inputLabel.isNotBlank()) {
                                saveNewCategory(inputLabel.trim())
                                closeDialog()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("添加")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
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
}