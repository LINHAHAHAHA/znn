package com.police.dispatch.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.police.dispatch.data.model.DispatchRecord

@Dao
interface DispatchRecordDao {
    
    @Query("SELECT * FROM dispatch_records ORDER BY callTime DESC")
    fun getAllRecords(): LiveData<List<DispatchRecord>>
    
    @Query("SELECT * FROM dispatch_records WHERE id = :id")
    suspend fun getRecordById(id: Long): DispatchRecord?
    
    @Query("SELECT * FROM dispatch_records WHERE status = :status ORDER BY callTime DESC")
    fun getRecordsByStatus(status: String): LiveData<List<DispatchRecord>>
    
    @Insert
    suspend fun insert(record: DispatchRecord): Long
    
    @Update
    suspend fun update(record: DispatchRecord)
    
    @Delete
    suspend fun delete(record: DispatchRecord)
    
    @Query("DELETE FROM dispatch_records")
    suspend fun deleteAll()
}

