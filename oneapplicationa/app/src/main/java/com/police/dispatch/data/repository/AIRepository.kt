package com.police.dispatch.data.repository

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.police.dispatch.data.api.ChatRequest
import com.police.dispatch.data.api.DeepSeekApiService
import com.police.dispatch.data.api.Message
import com.police.dispatch.data.model.DispatchRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * AI分析仓库 - 负责调用DeepSeek API进行智能分析
 */
class AIRepository {
    
    private val apiService = DeepSeekApiService.create()
    private val gson = Gson()
    
    /**
     * 分析语音文本，提取关键信息
     */
    suspend fun analyzeVoiceText(voiceText: String): AnalysisResult = withContext(Dispatchers.IO) {
        try {
            val prompt = buildAnalysisPrompt(voiceText)
            val request = ChatRequest(
                messages = listOf(
                    Message(role = "user", content = prompt)
                )
            )
            
            val response = apiService.chatCompletion(request)
            val content = response.choices.firstOrNull()?.message?.content ?: ""
            
            parseAnalysisResult(content)
        } catch (e: Exception) {
            e.printStackTrace()
            AnalysisResult(
                success = false,
                errorMessage = "AI分析失败: ${e.message}"
            )
        }
    }
    
    /**
     * 构建分析提示词
     */
    private fun buildAnalysisPrompt(voiceText: String): String {
        return """
你是一个专业的公安接警AI助手。请分析以下报警内容，提取关键信息并判断警情类型和紧急程度。

报警内容：
$voiceText

请按照以下JSON格式返回分析结果（只返回JSON，不要其他文字）：
{
  "location": "事发地点（详细地址）",
  "incidentTime": "事发时间",
  "involvedPersons": "涉及人员（姓名、特征等）",
  "incidentDescription": "事件描述（简洁概括）",
  "incidentType": "警情类型（从以下选择：盗窃、纠纷、求助、交通事故、火警、医疗急救、暴力事件、诈骗、失踪、其他）",
  "urgencyLevel": 紧急程度（1-5的数字，1最低5最高）,
  "urgencyReason": "紧急程度判断理由",
  "suggestedAction": "推荐处置预案（具体的处理建议和步骤）"
}

判断标准：
- 紧急程度5级：危及生命、正在发生的暴力犯罪、火灾等
- 紧急程度4级：可能危及安全、财产损失较大
- 紧急程度3级：一般治安案件、纠纷
- 紧急程度2级：咨询类、轻微求助
- 紧急程度1级：非紧急事务
        """.trimIndent()
    }
    
    /**
     * 解析分析结果
     */
    private fun parseAnalysisResult(content: String): AnalysisResult {
        return try {
            // 提取JSON部分
            val jsonStart = content.indexOf('{')
            val jsonEnd = content.lastIndexOf('}')
            
            if (jsonStart == -1 || jsonEnd == -1) {
                return AnalysisResult(
                    success = false,
                    errorMessage = "无法解析AI返回的JSON格式"
                )
            }
            
            val jsonStr = content.substring(jsonStart, jsonEnd + 1)
            val jsonObject = gson.fromJson(jsonStr, JsonObject::class.java)
            
            AnalysisResult(
                success = true,
                location = jsonObject.get("location")?.asString ?: "",
                incidentTime = jsonObject.get("incidentTime")?.asString ?: "",
                involvedPersons = jsonObject.get("involvedPersons")?.asString ?: "",
                incidentDescription = jsonObject.get("incidentDescription")?.asString ?: "",
                incidentType = jsonObject.get("incidentType")?.asString ?: "其他",
                urgencyLevel = jsonObject.get("urgencyLevel")?.asInt ?: 3,
                urgencyReason = jsonObject.get("urgencyReason")?.asString ?: "",
                suggestedAction = jsonObject.get("suggestedAction")?.asString ?: ""
            )
        } catch (e: Exception) {
            e.printStackTrace()
            AnalysisResult(
                success = false,
                errorMessage = "解析AI结果失败: ${e.message}"
            )
        }
    }
}

/**
 * AI分析结果
 */
data class AnalysisResult(
    val success: Boolean,
    val location: String = "",
    val incidentTime: String = "",
    val involvedPersons: String = "",
    val incidentDescription: String = "",
    val incidentType: String = "",
    val urgencyLevel: Int = 0,
    val urgencyReason: String = "",
    val suggestedAction: String = "",
    val errorMessage: String = ""
)

