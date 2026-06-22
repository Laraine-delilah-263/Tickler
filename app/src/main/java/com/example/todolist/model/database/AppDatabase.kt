package com.example.todolist.model.database

import android.content.Context

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.todolist.model.dao.CategoryDao
import com.example.todolist.model.dao.PriorityDao
import com.example.todolist.model.dao.TodoAffairDao
import com.example.todolist.model.entity.Category
import com.example.todolist.model.entity.Priority
import com.example.todolist.model.entity.TodoAffair

//用于定义数据库中的关键信息，包括数据库版本号，包含实体类和提供Dao层的访问实例
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
        //    数据库实例
        private var db: AppDatabase?=null


        @Synchronized
//        Synchronized线程安全注解。它保证了 getDatabase 这个方法同一时间只能被一个线程调用。
//        防止多线程环境下两个线程同时发现 db 是空的，各自创建新的数据库实例，破坏单例的唯一性。
        fun getDatabase(context: Context):AppDatabase{
            db?.let {
                return it
            }
            return Room.databaseBuilder(context.applicationContext,
                AppDatabase::class.java, "todo_database")
//                .fallbackToDestructiveMigration()//没有配置迁移就销毁旧表重建
                //                允许在主线程中进行数据库操作
                .allowMainThreadQueries()
                .build().apply{
                    db=this
                }
        }
    }
}
