package com.example.todolist.model.dao


// 联查三表返回的整合数据，不做数据库实体新增
data class TodoJoinData(
    // TodoAffair
    val affId: Long,
    val title: String,
    val detail: String,
    val startTime: Long,
    val endTime: Long,
    val isExpired: Int,
    val isFinish: Int,
    val categoryId: Long?,
    val priorityId: Long?,
    // 联查带出的关联文本
    val levelName: String?,    // 优先级名称：紧急/重要...
    val label: String?, // 分类名称
    val hasReminded: Int
)