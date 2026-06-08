package com.example.todolist.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.todolist.entity.TodoAffair
import kotlinx.coroutines.flow.Flow


//事务数据访问对象，对数据库的各项操作进行封装
@Dao
interface TodoAffairDao {
    @Query("SELECT * FROM todo_affair ORDER BY startTime DESC")
    fun getAllTodoList(): Flow<List<TodoAffair>>

    //    插入事务
    @Insert
    suspend fun insertTodo(todo: TodoAffair): Long


    @Query("DELETE FROM todo_affair WHERE affId = :id")
    suspend fun deleteTodo(id: Long)
}

