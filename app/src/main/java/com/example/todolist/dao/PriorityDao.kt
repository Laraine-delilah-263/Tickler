package com.example.todolist.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.todolist.entity.Priority
import kotlinx.coroutines.flow.Flow

//优先级数据访问对象，对数据库的各项操作进行封装
@Dao
interface PriorityDao {
    @Query("SELECT * FROM priority")
    fun getAllPriority(): Flow<List<Priority>>

    @Insert
    suspend fun insertPriority(priority: Priority): Long
}