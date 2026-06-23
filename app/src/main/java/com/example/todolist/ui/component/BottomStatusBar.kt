package com.example.todolist.ui.component


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

//底部状态栏

@Composable
fun BottomStatusBar(
    bgColor: Color,
    textColor: Color,
    dividerColor: Color,
    isDarkMode: Boolean,
    onModeChange: (Boolean) -> Unit,
    mainColor: Color,
    batchMode: Boolean,
    selectedCount: Int,
    onBatchDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if(batchMode){
            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onBatchDelete,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
            ) {
                Text("批量删除($selectedCount)")
            }
        }else{
                Text(
                    text = "当前模式：${if (isDarkMode) "夜间模式" else "日间模式"}",
                    color = textColor,
                    style = MaterialTheme.typography.bodySmall
                )

                Switch(
                    checked = isDarkMode,
                    onCheckedChange = onModeChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = mainColor,
                        checkedTrackColor = mainColor.copy(alpha = 0.5f)
                    )
                )
        }

    }
}