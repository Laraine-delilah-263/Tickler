package com.example.todolist.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.todolist.entity.TodoAffair
import kotlinx.coroutines.flow.Flow


//事务数据访问对象，对数据库的各项操作进行封装
@Dao
interface TodoAffairDao {

    //    插入事务
    @Insert
    suspend fun insertTodo(todo: TodoAffair): Long

    @Query("SELECT * FROM todo_affair")
    suspend fun getAllTodo():List<TodoAffair>

//    添加三表联查sql
    @Query("""
        SELECT t.*, p.levelName, c.label
        FROM todo_affair t
        LEFT JOIN priority p ON t.priorityId = p.prioId
        LEFT JOIN category c ON t.categoryId = c.cataId
        ORDER BY t.endTime ASC
    """
    )
fun queryTodoJoinAll(): Flow<List<TodoJoinData>>

}
