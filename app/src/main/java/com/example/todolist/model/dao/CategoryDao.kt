package com.example.todolist.model.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.todolist.model.entity.Category
import kotlinx.coroutines.flow.Flow


////分类数据访问对象，对数据库的各项操作进行封装
@Dao
interface CategoryDao {

    @Query("SELECT COUNT(*) FROM todo_affair WHERE categoryId = :cateId")
    suspend fun countTodoByCategory(cateId: Long): Int

    @Query("DELETE FROM category WHERE cataId = :cateId")
    suspend fun deleteCategory(cateId: Long)
    // 将指定待办标记为已提醒
    @Query("UPDATE todo_affair SET hasReminded = 1 WHERE affId = :todoId")
    suspend fun markTodoReminded(todoId: Long)


//    挂起查询，用于判空，一次性查询，用于按钮查询逻辑
    @Query("SELECT * FROM category")
    suspend fun getCategoryList(): List<Category>

//    插入数据
//    suspend fun仅在协程内安全执行，不会阻塞线程。
    @Insert
    suspend fun insertCategory(category: Category): Long

//    流式查询全部数据
//    Flow是数据流（用于compose观察刷新），不能用于一次性代码判空
    @Query("SELECT * FROM category")
    fun getAllCategory(): Flow<List<Category>>

//    更新数据
    @Update
    suspend fun updataCategory(newCategory: Category)

}
