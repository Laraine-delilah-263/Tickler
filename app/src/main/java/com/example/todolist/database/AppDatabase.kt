package com.example.todolist.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.todolist.dao.CategoryDao
import com.example.todolist.dao.PriorityDao
import com.example.todolist.dao.TodoAffairDao
import com.example.todolist.entity.Category
import com.example.todolist.entity.Priority
import com.example.todolist.entity.TodoAffair

@Database(
    entities = [TodoAffair::class, Category::class, Priority::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoAffairDao
    abstract fun categoryDao(): CategoryDao
    abstract fun priorityDao(): PriorityDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
    }
}