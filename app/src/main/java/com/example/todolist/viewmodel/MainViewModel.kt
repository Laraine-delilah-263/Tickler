package com.example.todolist.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolist.model.database.AppDatabase
import com.example.todolist.model.repository.TodoRepository
import com.example.todolist.model.dao.TodoJoinData
import com.example.todolist.model.entity.Category
import com.example.todolist.model.entity.Priority
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

    // 新增分类对外接口
    fun createCategory(labelText: String) {
        viewModelScope.launch(Dispatchers.IO) {
            categoryRepo.createNewCategory(labelText)
        }
    }

}