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
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    // Room数据库实例
    private val db = AppDatabase.getDatabase(application)

    private val todoRepo = TodoRepository(db.todoDao())
    private val categoryRepo = CategoryRepository(db.categoryDao())
    private val priorityRepo = PriorityRepository(db.priorityDao())

    // 页面监听数据流
    val todoFlow: Flow<List<TodoJoinData>> = todoRepo.observeTodoAll()
    val categoryFlow: Flow<List<Category>> = categoryRepo.observeAllCategory()
    val priorityFlow: Flow<List<Priority>> = priorityRepo.observeAllPriority()


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