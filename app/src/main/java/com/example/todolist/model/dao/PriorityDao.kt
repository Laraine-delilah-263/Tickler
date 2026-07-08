package com.example.todolist.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.todolist.model.entity.Priority
import kotlinx.coroutines.flow.Flow

//优先级数据访问对象，对数据库的各项操作进行封装
@Dao
interface PriorityDao {

    //初始化插入全部优先级
    @Insert
    suspend fun insertAll(priorityList:List<Priority>)

    //    挂起查询，查询全部优先级
    @Query("SELECT * FROM priority")
    suspend fun getPriorityList(): List<Priority>

    //    查询全部数据
    @Query("SELECT * FROM priority")
    fun getAllPriority(): Flow<List<Priority>>

    //    插入数据
    @Insert
    suspend fun insertPriority(priority: Priority): Long
}
