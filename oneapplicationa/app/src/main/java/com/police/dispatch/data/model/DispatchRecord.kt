package com.police.dispatch.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * 接警记录数据模型
 */
@Entity(tableName = "dispatch_records")
data class DispatchRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // 基本信息
    val callTime: Long = System.currentTimeMillis(),
    val callerPhone: String = "",
    val callerName: String = "",
    
    // 语音转文字内容
    val voiceText: String = "",
    val voiceDuration: Int = 0, // 秒
    
    // AI提取的关键信息
    val location: String = "",
    val incidentTime: String = "",
    val involvedPersons: String = "",
    val incidentDescription: String = "",
    
    // AI分析结果
    val incidentType: String = "", // 警情类型：盗窃、纠纷、求助等
    val urgencyLevel: Int = 0, // 紧急程度：1-5级
    val suggestedAction: String = "", // 推荐处置预案
    
    // 处理状态
    val status: String = "待处理", // 待处理、处理中、已完成
    val handlerName: String = "",
    val handleTime: Long = 0,
    val handleResult: String = "",
    
    // 备注
    val remarks: String = ""
)

/**
 * 警情类型枚举
 */
enum class IncidentType(val typeName: String, val description: String) {
    THEFT("盗窃", "财物被盗案件"),
    DISPUTE("纠纷", "民事纠纷、争执"),
    HELP("求助", "群众求助事项"),
    TRAFFIC("交通事故", "道路交通事故"),
    FIRE("火警", "火灾报警"),
    MEDICAL("医疗急救", "需要医疗救助"),
    VIOLENCE("暴力事件", "打架斗殴等暴力行为"),
    FRAUD("诈骗", "电信诈骗、网络诈骗"),
    MISSING("失踪", "人员失踪"),
    OTHER("其他", "其他类型警情")
}

/**
 * 紧急程度枚举
 */
enum class UrgencyLevel(val level: Int, val description: String, val color: String) {
    LEVEL_1(1, "一般", "#4CAF50"),
    LEVEL_2(2, "较轻", "#8BC34A"),
    LEVEL_3(3, "中等", "#FFC107"),
    LEVEL_4(4, "紧急", "#FF9800"),
    LEVEL_5(5, "特急", "#F44336")
}

