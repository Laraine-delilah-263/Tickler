package com.example.todolist.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.todolist.R

@Composable
fun TopNavigationBar(
    isDarkMode: Boolean,
    onModeChange: (Boolean) -> Unit,
    searchText: String,
    onSearchChange: (String) -> Unit,
    textColor: Color,
    mainColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo2),
                contentDescription = "应用图标",
                modifier = Modifier.size(36.dp)
            )
            Text(
                text = "车载记事本",
                style = MaterialTheme.typography.headlineSmall,
                color = textColor
            )
        }

        TextField(
            value = searchText,
            onValueChange = onSearchChange,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 32.dp)
                .height(56.dp),
            placeholder = { Text("搜索标题/正文...",
                color = textColor.copy(alpha = 0.6f),
                maxLines = 1
            ) },
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.search),
                    contentDescription = "搜索",
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(textColor.copy(alpha = 0.6f))
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(24.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
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