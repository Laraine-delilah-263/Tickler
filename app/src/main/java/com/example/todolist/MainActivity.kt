package com.example.todolist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.example.todolist.ui.component.BottomStatusBar
import com.example.todolist.ui.component.FilterBar
import com.example.todolist.ui.component.LeftSideBar
import com.example.todolist.ui.component.NoteListArea
import com.example.todolist.ui.component.TopNavigationBar
import com.example.todolist.ui.theme.TodoListTheme
//数据库依赖
import com.example.todolist.model.dao.TodoJoinData
import com.example.todolist.model.database.AppDatabase
import com.example.todolist.model.entity.Category
import com.example.todolist.model.entity.Priority
import com.example.todolist.model.entity.TodoAffair
import com.example.todolist.ui.component.AddTodoDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.todolist.ui.component.TodoExpireTopToast
import com.example.todolist.ui.component.AddCategoryDialog
import com.example.todolist.ui.component.TodoDetailDialog
import com.example.todolist.viewmodel.MainViewModel
import com.example.todolist.viewmodel.MainViewModelFactory
import androidx.lifecycle.viewModelScope

class MainActivity : ComponentActivity() {

    // 新增：获取ViewModel实例
    private val mainVm: MainViewModel by viewModels {
        MainViewModelFactory(application)
    }

    //初始化数据库与Dao
    private val db by lazy { AppDatabase.getDatabase(applicationContext) }
    private val todoDao by lazy { db.todoDao() }
    private val categoryDao by lazy { db.categoryDao() }
    private val priorityDao by lazy { db.priorityDao() }

    // 校验分类下是否存在待办，执行删除
    private fun checkAndDeleteCategory(
        category: Category,
        scope: kotlinx.coroutines.CoroutineScope,
        onCannotDelete:()-> Unit
        ) {
        scope.launch(Dispatchers.IO) {
            // 查询该分类下所有待办
            val count = todoDao.countTodoByCategory(category.cataId)
            if (count > 0) {
                // 存在事务，弹出提醒
                scope.launch(Dispatchers.Main) {
                    onCannotDelete()
                }
            } else {
                // 无关联事务，直接删除分类
                categoryDao.deleteCategory(category.cataId)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        数据库和表的创建
//        默认初始化数据：IO协程执行，判空后再插入
        mainVm.initDatabaseDefaultData()
        setContent {
            TodoListTheme {
                // 分类删除弹窗
                var showCateWarnDialog by remember { mutableStateOf(false) }
                var targetDeleteCate by remember { mutableStateOf<Category?>(null) }
                // 详情弹窗
                var showDetailDialog by remember { mutableStateOf(false) }
                var targetDetailTodo by remember { mutableStateOf<TodoJoinData?>(null) }
//              批量管理模式开关
                var batchManageMode by remember { mutableStateOf(false) }
//              存储选中的待办affId
                val selectedTodoIds = remember { mutableStateListOf<Long>() }
//                新增标签弹窗
                var openAddCateDialog by remember { mutableStateOf(false) }
//                弹窗状态
                var showExpireDialog by remember { mutableStateOf(false) }
                var currentExpireTodo by remember { mutableStateOf<TodoJoinData?>(null) }
//                var remindedTodoIds = remember { mutableStateListOf<Long>() }
                var isSearchFocused by remember { mutableStateOf(false) }
//                全局共享焦点
                val searchFocusRequester = remember { FocusRequester() }
                val keyboardController = LocalSoftwareKeyboardController.current
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
                val filterTodoList =
                    remember(searchText, todoDataSource, selectCategory, selectPriority) {
                        var list = todoDataSource
                        // 1.搜索文本过滤，必须赋值回list
                        if (searchText.isNotBlank()) {
                            val keyword = searchText.trim().lowercase()
                            list = list.filter { todo ->
                                todo.title.lowercase().contains(keyword) || todo.detail.lowercase().contains(keyword)
                            }
                        }
                        // 2.分类过滤
                        if (selectCategory != "全部分类") {
                            list = list.filter { it.label == selectCategory }
                        }
                        // 3. 优先级过滤
                        if (selectPriority != "全部等级") {
                            list = list.filter { it.levelName == selectPriority }
                        }
                        return@remember list
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
                    priorityDao.getAllPriority().collect { prioList ->
                        allPriority = prioList
                    }
                }

                // 实时检测过期未完成待办
                LaunchedEffect(todoDataSource) {
                    while(true){
                        val nowTime = System.currentTimeMillis()
                        if(showExpireDialog){
                            delay(1000)
                            continue
                        }
                        val expireTodo = todoDataSource.firstOrNull { todo ->
                            // 条件：截止时间 < 当前时间 + 未完成 + 未提醒过
                            todo.endTime < nowTime
                                    && todo.isExpired == 0
                                    && todo.isFinish==0
                                    && todo.hasReminded == 0
                        }
                        if (expireTodo != null) {
                            currentExpireTodo = expireTodo
                            showExpireDialog = true
                            todoDao.markTodoReminded(expireTodo.affId)
                        }
                        delay(1000)
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
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
//                            消除点击波纹
                            indication = null,
                            interactionSource = null
                        ) {
                            keyboardController?.hide()
                            scope.launch {
                                delay(80)
                                searchFocusRequester.freeFocus()
                                onWindowFocusChanged(false)
                            }
                        },
                    color = pageBg
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        TopNavigationBar(
                            searchText = searchText,
                            onSearchChange = { searchText = it },
                            textColor = textPrimary,
                            searchFocusRequester = searchFocusRequester,
                            globalKeyboardController = keyboardController,
                            globalScope = scope,
                            onFocusChange = { newState ->
                                isSearchFocused = newState
                            },
                            isSearchFocused = isSearchFocused,
                        )
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)) {
                            LeftSideBar(
                                modifier = Modifier.width(220.dp),
                                bgColor = sideBarBg,
                                textColor = textPrimary,
                                mainColor = mainColor,
                                dividerColor = dividerColor,
                                onAddTagClick = { openAddCateDialog = true },
                                onBatchClick = {
                                    batchManageMode = !batchManageMode
                                    selectedTodoIds.clear()
                                }
                            )
                                    Column {
                                FilterBar(
                                    textColor = textPrimary,
                                    mainColor = mainColor,
                                    dividerColor = dividerColor,
                                    categoryList = allCategory,
                                    priorityList = allPriority,
                                    onCategorySelect = { selectCategory = it },
                                    onPrioritySelect = { selectPriority = it },
//                                    监听高亮
                                    currentSelectCategory = selectCategory,
                                    batchDeleteMode = batchManageMode,
                                    currentSelectPriority = selectPriority,
                                    onCateDeleteClick ={cate->
                                        checkAndDeleteCategory(cate,scope){
                                            targetDeleteCate=cate
                                            showCateWarnDialog=true
                                        }
                                    }
                                )
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                ) {
//                                    传入数据库真实数据
                                    NoteListArea(
                                        cardBg = contentCardBg,
                                        textColor = textPrimary,
                                        mainColor = mainColor,
//                                        todoList=todoDataSource,
                                        todoList = filterTodoList,
                                        selectStroke = selectStrokeColor,
                                        onDeleteTodo = { targetTodoId ->
                                            scope.launch(Dispatchers.IO) {
                                                val todo = todoDao.getTodoById(targetTodoId)
                                                todo?.let { todoDao.deleteTodoById(targetTodoId) }
                                            }
                                        },
                                        onMarkComplete = { targetTodoId ->
                                            scope.launch(Dispatchers.IO) {
                                                todoDao.markTodoFinish(targetTodoId)
                                            }
                                        },
                                        // 拖拽排序回调：新顺序列表
                                        onOrderChanged = { newSortList ->
                                            scope.launch(Dispatchers.IO) {
                                                // 按拖拽后的顺序重新赋值sortOrder
                                                val updateData = newSortList.mapIndexed { index, joinData ->
                                                    val originTodo = todoDao.getTodoById(joinData.affId)
                                                    originTodo?.copy(sortOrder = index)
                                                }.filterNotNull()
                                                todoDao.batchUpdateTodo(updateData)
                                            }
                                        },
                                        batchMode = batchManageMode,
                                        selectedIds = selectedTodoIds,
                                        onToggleSelect = { todoId ->
                                            if (selectedTodoIds.contains(todoId)) {
                                                selectedTodoIds.remove(todoId)
                                            } else {
                                                selectedTodoIds.add(todoId)
                                            }
                                        },
                                        onClickTodoItem = { todo ->
                                            targetDetailTodo = todo
                                            showDetailDialog = true
                                        }
                                    )
//                                    新建笔记按钮
                                    FloatingActionButton(
                                        onClick = {
                                            openAddDialog = true
                                        },
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .padding(end=75.dp,bottom=75.dp),
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
                            mainColor = mainColor,
                            batchMode = batchManageMode,
                            selectedCount = selectedTodoIds.size,
                            onBatchDelete = {
                                // 先复制一份选中ID快照，避免过程中集合变化
                                val idsToDelete = selectedTodoIds.toList()
                                scope.launch(Dispatchers.IO) {
//                                    println("批量删除选中ID：$idsToDelete")
                                    if (idsToDelete.isNotEmpty()) {
                                        todoDao.batchDeleteTodo(idsToDelete)
//                                        println("批量删除执行完成")
                                    }
                                    // 数据库删除完成后，再切回主线程清空UI状态
                                    scope.launch(Dispatchers.Main) {
                                        batchManageMode = false
                                        selectedTodoIds.clear()
                                    }
                                }
                            }
                        )
                    }
                    //挂载截止时间提醒弹窗
                    // 过期提醒顶部弹窗
                    if (showExpireDialog && currentExpireTodo != null) {
                        TodoExpireTopToast(
                            expireTodo = currentExpireTodo!!,
                            onDismiss = {
                                showExpireDialog = false
                                currentExpireTodo = null
                            }
                        )
                    }
                }
                // 事务详情弹窗
                if (showDetailDialog && targetDetailTodo != null) {
                    val currentTodo = targetDetailTodo
                    TodoDetailDialog(
                        todo = currentTodo!!,
                        textColor = textPrimary,
                        bgCardColor = contentCardBg,
                        categoryList = allCategory,
                        priorityList = allPriority,
                        onUpdateTodo = { title, content, cateId, prioId, endTime ->
                            scope.launch(Dispatchers.IO) {
                                val originTodo = todoDao.getTodoById(currentTodo.affId) ?: return@launch
                                val nowTime = System.currentTimeMillis()
                                val newIsExpiredInt = if (endTime < nowTime) 1 else 0

                                val safeCateId = cateId ?: originTodo.categoryId
                                val safePrioId = prioId ?: originTodo.priorityId

                                val updatedEntity = originTodo.copy(
                                    title = title,
                                    detail = content,
                                    categoryId = safeCateId,
                                    priorityId = safePrioId,
                                    endTime = endTime,
                                    isExpired = newIsExpiredInt,
                                    isFinish = 0,         // 强制置为未完成，取消文字划线
                                    hasReminded = 0       // 重置提醒，到期再次弹窗
                                )
                                todoDao.updateTodo(updatedEntity)

                                scope.launch(Dispatchers.Main) {
                                    showDetailDialog = false
                                    targetDetailTodo = null
                                }
                            }
                        },
                        onClose = {
                            showDetailDialog = false
                            targetDetailTodo = null
                        }
                    )
                }

                //挂载新增事务弹窗
                AddTodoDialog(
                    show = openAddDialog,
                    textColor = textPrimary,
                    categoryList = allCategory,//分类数据
                    priorityList = allPriority,
                    bgCardColor = contentCardBg, //跟随页面卡片底色
                    closeDialog = { openAddDialog = false },
                    saveClick = { titleInput, content, cateId, prioId, fullEndTimeMs ->
                        //IO线程插入数据库
                        scope.launch(Dispatchers.IO) {
                            val now = System.currentTimeMillis()
                            //标题固定，内容填输入文字，截止时间=当天+选定时分，分类/优先级暂时null
                            val maxSort=todoDao.getMaxSortOrder()?:0
                            val todo = TodoAffair(
                                title = titleInput,
                                detail = content,
                                startTime = now,
                                endTime = fullEndTimeMs,
                                categoryId = cateId,
                                priorityId = prioId,
                                hasReminded = 0,
                                sortOrder = maxSort+1
                            )
                            todoDao.insertTodo(todo)
                        }
                    }
                )
                // 新增自定义分类弹窗
                AddCategoryDialog(
                    show = openAddCateDialog,
                    textColor = textPrimary,
                    bgCardColor = contentCardBg,
                    closeDialog = { openAddCateDialog = false },
                    saveNewCategory = { labelName ->
                        mainVm.createCategory(labelName)
                    }
                )
                // 分类存在待办时的提醒弹窗
                if (showCateWarnDialog) {
                    androidx.compose.material3.AlertDialog(
                        onDismissRequest = { showCateWarnDialog = false },
                        title = { Text("无法删除") },
                        text = { Text("该分类下仍有待办事务，请先删除相关事务后再操作！") },
                        confirmButton = {
                            androidx.compose.material3.TextButton(onClick = { showCateWarnDialog = false }) {
                                Text("确定")
                            }
                        }
                    )
                }
            }
        }
    }
}
