package com.example.todolist.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.todolist.model.entity.TodoAffair
import kotlinx.coroutines.flow.Flow


//事务数据访问对象，对数据库的各项操作进行封装
@Dao
interface TodoAffairDao {
    @Update
    suspend fun updateTodo(todo: TodoAffair)
    @Query("SELECT MAX(sortOrder) FROM todo_affair")
    suspend fun getMaxSortOrder(): Int?
    @Update
    suspend fun batchUpdateTodo(todoList: List<TodoAffair>)
    // 批量删除：根据affId集合删除多条待办
    @Query("DELETE FROM todo_affair WHERE affId IN (:idList)")
    suspend fun batchDeleteTodo(idList: List<Long>)
    // 将待办标记为已弹出过期提醒
    @Query("UPDATE todo_affair SET hasReminded = 1 WHERE affId = :id")
    suspend fun markTodoReminded(id: Long)

    @Query("UPDATE todo_affair SET isFinish = 1 WHERE affId = :id")
    suspend fun markTodoFinish(id: Long)

    // 根据主键查询单条事务（闹钟广播读取）
    @Query("SELECT * FROM todo_affair WHERE affId = :id")
    suspend fun getTodoById(id: Long): TodoAffair?

    // 标记事务已过期，防止重复通知
    @Query("UPDATE todo_affair SET isExpired = 1 WHERE affId = :id")
    suspend fun markTodoExpired(id: Long)

    // 根据实体删除事务（左滑删除用）
    @Delete
    suspend fun deleteTodoByEntity(todo: TodoAffair)

//    删除单条事务
    @Query("DELETE FROM todo_affair WHERE affId = :id")
    suspend fun deleteTodoById(id: Long)

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
        ORDER BY sortOrder  ASC
    """
    )
    fun queryTodoJoinAll(): Flow<List<TodoJoinData>>

}
