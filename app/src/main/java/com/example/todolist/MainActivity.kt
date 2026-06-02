
package com.example.todolist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.example.todolist.ui.component.BottomStatusBar
import com.example.todolist.ui.component.FilterBar
import com.example.todolist.ui.component.LeftSideBar
import com.example.todolist.ui.component.NoteListArea
import com.example.todolist.ui.component.TopNavigationBar
import com.example.todolist.ui.theme.TodoListTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TodoListTheme {
                var isDarkMode by remember { mutableStateOf(false) }
                var searchText by remember { mutableStateOf("") }

                val pageBg: Color
                val sideBarBg: Color
                val contentCardBg: Color
                val textPrimary: Color
                val mainColor: Color
                val dividerColor: Color
                val selectStrokeColor: Color

                if (isDarkMode) {
                    pageBg = Color(0xFF1E2229)
                    sideBarBg = Color(0xFF252A33)
                    contentCardBg = Color(0xFF2C303A)
                    textPrimary = Color(0xFFE8EBF0)
                    mainColor = Color(0xFF3B7EA1)
                    dividerColor = Color(0xFF444952)
                    selectStrokeColor = Color(0xFF3B7EA1)
                } else {
                    pageBg = Color(0xFFF5F7FA)
                    sideBarBg = Color.White
                    contentCardBg = Color.White
                    textPrimary = Color(0xFF333333)
                    mainColor = Color(0xFF4A90E2)
                    dividerColor = Color(0xFFE0E4EB)
                    selectStrokeColor = Color(0xFF4A90E2)
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = pageBg
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        TopNavigationBar(
                            isDarkMode = isDarkMode,
                            onModeChange = { isDarkMode = it },
                            searchText = searchText,
                            onSearchChange = { searchText = it },
                            textColor = textPrimary,
                            mainColor = mainColor
                        )
                        Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                            LeftSideBar(
                                modifier = Modifier.width(220.dp),
                                bgColor = sideBarBg,
                                textColor = textPrimary,
                                mainColor = mainColor,
                                dividerColor = dividerColor
                            )
                            Column {
                                FilterBar(
                                    textColor = textPrimary,
                                    mainColor = mainColor,
                                    dividerColor = dividerColor
                                )
                                Box(
                                    modifier = Modifier.weight(1f).fillMaxHeight()
                                ) {
                                    NoteListArea(
                                        cardBg = contentCardBg,
                                        textColor = textPrimary,
                                        mainColor = mainColor,
                                        selectStroke = selectStrokeColor
                                    )
                                    FloatingActionButton(
                                        onClick = {},
                                        modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
                                        shape = CircleShape,
                                        containerColor = mainColor
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.build),
                                            contentDescription = "新建笔记",
                                            modifier = Modifier.size(24.dp),
                                            colorFilter = ColorFilter.tint(Color.White)
                                        )
                                    }
                                }
                            }
                        }
                        BottomStatusBar(
                            bgColor = sideBarBg,
                            textColor = textPrimary,
                            dividerColor = dividerColor,
                            isDarkMode = isDarkMode
                        )
                    }
                }
            }
        }
    }
}



