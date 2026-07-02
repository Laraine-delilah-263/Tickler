package com.example.todolist.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolist.model.database.AppDatabase
import com.example.todolist.model.repository.TodoRepository
import com.example.todolist.model.dao.TodoJoinData
import com.example.todolist.model.entity.Category
import com.example.todolist.model.entity.Priority
import com.example.todolist.model.entity.TodoAffair
import com.example.todolist.model.repository.CategoryRepository
import com.example.todolist.model.repository.PriorityRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.combine

class MainViewModel(application: Application) : AndroidViewModel(application) {
    // Room数据库实例
    private val db = AppDatabase.getDatabase(application)
    private val todoRepo = TodoRepository(db.todoDao())
    private val categoryRepo = CategoryRepository(db.categoryDao())
    private val priorityRepo = PriorityRepository(db.priorityDao())
    private val _searchKeyword = MutableStateFlow("")
    val searchKeyword: StateFlow<String> = _searchKeyword.asStateFlow()
    private val _filterCategory = MutableStateFlow("全部分类")
    val filterCategory: StateFlow<String> = _filterCategory.asStateFlow()
    private val _filterPriority = MutableStateFlow("全部等级")
    val filterPriority: StateFlow<String> = _filterPriority.asStateFlow()
    // 页面监听数据流
    val todoFlow: Flow<List<TodoJoinData>> = todoRepo.observeTodoAll()
    val categoryFlow: Flow<List<Category>> = categoryRepo.observeAllCategory()
    val priorityFlow: Flow<List<Priority>> = priorityRepo.observeAllPriority()
    // 组合原始待办流 + 三个筛选条件，自动过滤
    val filteredTodoListFlow: Flow<List<TodoJoinData>> = combine(
        todoFlow,
        _searchKeyword,
        _filterCategory,
        _filterPriority
    ) { args: Array<*> ->
        val origin = args[0] as List<TodoJoinData>
        val keyword = args[1] as String
        val cate = args[2] as String
        val prio = args[3] as String
        var res = origin
        if (keyword.isNotBlank()) {
            val low = keyword.lowercase()
            res = res.filter { it.title.lowercase().contains(low) || it.detail.lowercase().contains(low) }
        }
        if (cate != "全部分类") res = res.filter { it.label == cate }
        if (prio != "全部等级") res = res.filter { it.levelName == prio }
        return@combine res
    }


    // 对外更新筛选条件的方法（View仅调用，不持有状态）
    fun updateSearchKeyword(text: String) {
        _searchKeyword.value = text
    }

    fun selectFilterCategory(cateName: String) {
        _filterCategory.value = cateName
    }

    fun selectFilterPriority(prioName: String) {
        _filterPriority.value = prioName
    }

    // 【核心方法：首次创建数据库初始化默认数据】
    fun initDatabaseDefaultData() {
        viewModelScope.launch {
            todoRepo.initDefaultTableData()
            categoryRepo.initDefaultCategory()
            priorityRepo.initDefaultPriority()
        }
    }

//    新增分类对外接口
    fun createCategory(labelText: String) {
        viewModelScope.launch(Dispatchers.IO) {
            categoryRepo.createNewCategory(labelText)
        }
    }

//    删除分类前置校验
    fun checkAndDeleteCategory(cataId: Long, onCannotDelete: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            // 调用分类仓库查询该分类待办数量
            val todoCount = categoryRepo.getTodoCountByCataId(cataId)
            if (todoCount > 0) {
                // 切主线程执行弹窗回调
                launch(Dispatchers.Main) {
                    onCannotDelete()
                }
            } else {
                // 无待办，执行删除
                categoryRepo.deleteCategoryById(cataId)
            }
        }
    }

//    新增待办对外接口
    fun addNewTodo(todo: TodoAffair) {
        viewModelScope.launch(Dispatchers.IO) {
            todoRepo.insertNewTodo(todo)
        }
    }

//    获取最大sort
    suspend fun getMaxSortNum(): Int? {
        return todoRepo.getMaxSortOrder()
    }

//    删除单条待办
    fun deleteSingleTodo(affId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            todoRepo.deleteSingleTodoById(affId)
        }
    }

//    标记完成待办
    fun finishTodoItem(affId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            todoRepo.markTodoFinished(affId)
        }
    }

//    编辑回填
    suspend fun getTodoEntity(affId: Long): TodoAffair? {
        return todoRepo.getTodoEntityById(affId)
    }

//    保存编辑后的待办
    fun updateTodoEntity(todo: TodoAffair) {
        viewModelScope.launch(Dispatchers.IO) {
            todoRepo.updateTodoItem(todo)
        }
    }

//    批量删除事务
    fun batchDeleteTodo(ids: List<Long>, onFinish: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            todoRepo.batchDeleteTodo(ids)
            launch(Dispatchers.Main) {
                onFinish()
            }
        }
    }

//    批量更新拖拽顺序
    fun updateTodoSort(updatedTodoList: List<TodoAffair>) {
        viewModelScope.launch(Dispatchers.IO) {
            todoRepo.batchUpdateTodoSort(updatedTodoList)
        }
    }

//    标记待办已提醒，不再重复弹窗
    fun setTodoReminded(affId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            todoRepo.markTodoReminded(affId)
        }
    }

}