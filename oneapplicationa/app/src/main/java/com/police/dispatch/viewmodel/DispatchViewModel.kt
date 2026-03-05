package com.police.dispatch.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.police.dispatch.data.database.AppDatabase
import com.police.dispatch.data.model.DispatchRecord
import com.police.dispatch.data.repository.AIRepository
import com.police.dispatch.data.repository.AnalysisResult
import kotlinx.coroutines.launch

class DispatchViewModel(application: Application) : AndroidViewModel(application) {
    
    private val database = AppDatabase.getDatabase(application)
    private val dispatchRecordDao = database.dispatchRecordDao()
    private val aiRepository = AIRepository()
    
    private val _analysisResult = MutableLiveData<AnalysisResult>()
    val analysisResult: LiveData<AnalysisResult> = _analysisResult
    
    private val _saveResult = MutableLiveData<Boolean>()
    val saveResult: LiveData<Boolean> = _saveResult
    
    private val _currentRecord = MutableLiveData<DispatchRecord>()
    val currentRecord: LiveData<DispatchRecord> = _currentRecord
    
    val allRecords: LiveData<List<DispatchRecord>> = dispatchRecordDao.getAllRecords()
    
    /**
     * 分析语音文本
     */
    fun analyzeVoiceText(voiceText: String) {
        viewModelScope.launch {
            val result = aiRepository.analyzeVoiceText(voiceText)
            _analysisResult.postValue(result)
            
            // 保存当前分析结果到临时记录
            if (result.success) {
                _currentRecord.postValue(
                    DispatchRecord(
                        voiceText = voiceText,
                        location = result.location,
                        incidentTime = result.incidentTime,
                        involvedPersons = result.involvedPersons,
                        incidentDescription = result.incidentDescription,
                        incidentType = result.incidentType,
                        urgencyLevel = result.urgencyLevel,
                        suggestedAction = result.suggestedAction
                    )
                )
            }
        }
    }
    
    /**
     * 保存接警记录
     */
    fun saveDispatchRecord(
        voiceText: String,
        callerPhone: String,
        callerName: String,
        location: String,
        incidentTime: String,
        involvedPersons: String,
        incidentDescription: String
    ) {
        viewModelScope.launch {
            try {
                val current = _currentRecord.value
                val record = DispatchRecord(
                    voiceText = voiceText,
                    callerPhone = callerPhone,
                    callerName = callerName,
                    location = location,
                    incidentTime = incidentTime,
                    involvedPersons = involvedPersons,
                    incidentDescription = incidentDescription,
                    incidentType = current?.incidentType ?: "",
                    urgencyLevel = current?.urgencyLevel ?: 0,
                    suggestedAction = current?.suggestedAction ?: "",
                    status = "待处理"
                )
                
                dispatchRecordDao.insert(record)
                _saveResult.postValue(true)
            } catch (e: Exception) {
                e.printStackTrace()
                _saveResult.postValue(false)
            }
        }
    }
    
    /**
     * 更新接警记录
     */
    fun updateDispatchRecord(record: DispatchRecord) {
        viewModelScope.launch {
            try {
                dispatchRecordDao.update(record)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    /**
     * 删除接警记录
     */
    fun deleteDispatchRecord(record: DispatchRecord) {
        viewModelScope.launch {
            try {
                dispatchRecordDao.delete(record)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    /**
     * 根据ID获取记录
     */
    fun getRecordById(id: Long) {
        viewModelScope.launch {
            val record = dispatchRecordDao.getRecordById(id)
            record?.let {
                _currentRecord.postValue(it)
            }
        }
    }
}

