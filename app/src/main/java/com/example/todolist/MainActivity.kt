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

//数据库依赖
import androidx.room.Room
import com.example.todolist.dao.CategoryDao
import com.example.todolist.dao.TodoJoinData
import com.example.todolist.database.AppDatabase
import com.example.todolist.entity.Category
import com.example.todolist.entity.Priority
import com.example.todolist.entity.TodoAffair
import com.example.todolist.ui.component.AddTodoDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    //初始化数据库与Dao
    private val db by lazy { AppDatabase.getDatabase(applicationContext) }
    private val todoDao by lazy { db.todoDao() }
    private val categoryDao by lazy { db.categoryDao() }
    private val priorityDao by lazy { db.priorityDao() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        数据库和表的创建
//        默认初始化数据：IO协程执行，判空后再插入
        kotlinx.coroutines.GlobalScope.launch(Dispatchers.IO) {
            // 1. 判断分类表是否为空
            val categoryList = categoryDao.getCategoryList()
            if (categoryList.isEmpty()) {
                categoryDao.insertCategory(Category(label = "日常事务"))
            }

            // 2. 判断优先级表是否为空
            val priorityList = priorityDao.getPriorityList()
            if (priorityList.isEmpty()) {
                priorityDao.insertPriority(Priority(levelName = "常规"))
            }

            // 3. 判断待办表是否为空，插入第一条默认待办
            val todoList = todoDao.getAllTodo()
            if (todoList.isEmpty()) {
                val now = System.currentTimeMillis()
                val endTime = now + 24 * 3600_000
                val todo = TodoAffair(
                    title = "初始待办",
                    detail = "系统默认第一条待办事项",
                    startTime = now,
                    endTime = endTime,
                    categoryId = 1,
                    priorityId = 1
                )
                todoDao.insertTodo(todo)
            }
        }

        setContent {
            TodoListTheme {
//                输入搜索的临时文本
                var searchkeyword by remember { mutableStateOf("") }
                var queryResult by remember { mutableStateOf("") }
                var isDarkMode by remember { mutableStateOf(false) }
                var searchText by remember { mutableStateOf("") }
//                待办数据列表
                val scope = rememberCoroutineScope()
                //弹窗控制标记
                var openAddDialog by remember { mutableStateOf(false) }
                // 存储数据库联查完整数据
                var todoDataSource by remember { mutableStateOf<List<TodoJoinData>>(emptyList()) }
                var allCategory by remember { mutableStateOf<List<Category>>(emptyList()) }
                var allPriority by remember { mutableStateOf<List<Priority>>(emptyList()) }
//                标签筛选
                var selectCategory by remember { mutableStateOf("全部分类") }
                var selectPriority by remember { mutableStateOf("全部等级") }
//                根据搜索文本过滤待办列表
                val filterTodoList=remember(searchText,todoDataSource,selectCategory,selectPriority) {
                    var list=todoDataSource
//                    1.搜索文本过滤
                    if (searchText.isBlank()){
                        todoDataSource
                    }else{
                        val keyword=searchText.trim().lowercase()
                        todoDataSource.filter { todo->
                            todo.title.lowercase().contains(keyword)||todo.detail.lowercase().contains(keyword)                        }
                    }
//                    2.分类过滤
                    if (selectCategory != "全部分类") {
                        list = list.filter { it.label == selectCategory }
                    }
                    // 3. 优先级过滤
                    if (selectPriority != "全部等级") {
                        list = list.filter { it.levelName == selectPriority }
                    }
                    list
                }
                // 监听数据库变化，自动刷新事务列表
                LaunchedEffect(Unit) {
                    todoDao.queryTodoJoinAll().collect { list ->
                        todoDataSource = list
                    }
                }
//                加载全部分类数据
                LaunchedEffect(Unit) {
                    categoryDao.getAllCategory().collect { cateList ->
                        allCategory = cateList
                    }
                }
//                优先级
                LaunchedEffect(Unit) {
                    priorityDao.getAllPriority().collect{ prioList->allPriority = prioList
                    }
                }

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
                            searchText = searchText,
                            onSearchChange = { searchText = it },
                            textColor = textPrimary,
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
                                    dividerColor = dividerColor,
                                    categoryList = allCategory,
                                    priorityList = allPriority,
                                    onCategorySelect={selectCategory=it},
                                    onPrioritySelect={selectPriority=it},
//                                    监听高亮
                                    currentSelectCategory = selectCategory,
                                    currentSelectPriority = selectPriority
                                )
                                Box(
                                    modifier = Modifier.weight(1f).fillMaxHeight()
                                ) {
//                                    传入数据库真实数据
                                    NoteListArea(
                                        cardBg = contentCardBg,
                                        textColor = textPrimary,
                                        mainColor = mainColor,
//                                        todoList=todoDataSource,
                                        todoList=filterTodoList,
                                        selectStroke = selectStrokeColor
                                    )
//                                    新建笔记按钮
                                    FloatingActionButton(
                                        onClick = {
                                            openAddDialog = true
                                        },
                                        modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
                                        shape = CircleShape,
                                        containerColor = mainColor
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.add),
                                            contentDescription = "新建事务",
                                            modifier = Modifier.size(24.dp),
                                            colorFilter = ColorFilter.tint(Color.White)
                                        )
                                    }
                                }
                            }
                        }
                        BottomStatusBar(
                            isDarkMode = isDarkMode,
                            onModeChange = { isDarkMode = it },
                            bgColor = sideBarBg,
                            textColor = textPrimary,
                            dividerColor = dividerColor,
                            mainColor = mainColor
                        )
                    }
                }

                //挂载弹窗
                AddTodoDialog(
                    show = openAddDialog,
                    textColor = textPrimary,
                    categoryList = allCategory,//分类数据
                    priorityList = allPriority,
                    bgCardColor = contentCardBg, //跟随页面卡片底色
                    closeDialog = { openAddDialog = false },
                    saveClick = { titleInput,content,cateId,prioId, fullEndTimeMs ->
                        //IO线程插入数据库
                        scope.launch(Dispatchers.IO) {
                            val now = System.currentTimeMillis()
                            //标题固定，内容填输入文字，截止时间=当天+选定时分，分类/优先级暂时null
                            val todo = TodoAffair(
                                title = titleInput,
                                detail = content,
                                startTime = now,
                                endTime = fullEndTimeMs,
                                categoryId = cateId,
                                priorityId = prioId
                            )
                            todoDao.insertTodo(todo)
                        }
                    }
                )
            }
        }
    }
}
