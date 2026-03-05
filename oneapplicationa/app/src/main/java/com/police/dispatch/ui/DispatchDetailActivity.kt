package com.police.dispatch.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.police.dispatch.R
import com.police.dispatch.data.model.DispatchRecord
import com.police.dispatch.databinding.ActivityDispatchDetailBinding
import com.police.dispatch.viewmodel.DispatchViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class DispatchDetailActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityDispatchDetailBinding
    private lateinit var viewModel: DispatchViewModel
    private var currentRecord: DispatchRecord? = null
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
    
    companion object {
        private const val EXTRA_RECORD_ID = "record_id"
        
        fun start(context: Context, recordId: Long) {
            val intent = Intent(context, DispatchDetailActivity::class.java)
            intent.putExtra(EXTRA_RECORD_ID, recordId)
            context.startActivity(intent)
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDispatchDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        viewModel = ViewModelProvider(this)[DispatchViewModel::class.java]
        
        setupUI()
        loadRecord()
        observeViewModel()
    }
    
    private fun setupUI() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "接警详情"
        
        // 状态下拉框
        val statusOptions = arrayOf("待处理", "处理中", "已完成")
        val statusAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statusOptions)
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerStatus.adapter = statusAdapter
        
        // 更新按钮
        binding.btnUpdate.setOnClickListener {
            updateRecord()
        }
        
        // 删除按钮
        binding.btnDelete.setOnClickListener {
            deleteRecord()
        }
    }
    
    private fun loadRecord() {
        val recordId = intent.getLongExtra(EXTRA_RECORD_ID, -1)
        if (recordId != -1L) {
            viewModel.getRecordById(recordId)
        } else {
            Toast.makeText(this, "记录ID无效", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    
    private fun observeViewModel() {
        viewModel.currentRecord.observe(this) { record ->
            currentRecord = record
            displayRecord(record)
        }
    }
    
    private fun displayRecord(record: DispatchRecord) {
        binding.tvCallTime.text = "报警时间: ${dateFormat.format(Date(record.callTime))}"
        binding.tvCallerInfo.text = "报警人: ${record.callerName.ifEmpty { "未知" }} (${record.callerPhone.ifEmpty { "未知" }})"
        
        binding.tvVoiceText.text = record.voiceText
        binding.tvLocation.text = record.location
        binding.tvIncidentTime.text = record.incidentTime
        binding.tvInvolvedPersons.text = record.involvedPersons
        binding.tvIncidentDescription.text = record.incidentDescription
        
        binding.tvIncidentType.text = "警情类型: ${record.incidentType}"
        binding.tvUrgencyLevel.text = "紧急程度: ${record.urgencyLevel}级"
        
        // 紧急程度颜色
        val urgencyColor = when (record.urgencyLevel) {
            5 -> getColor(R.color.red)
            4 -> getColor(R.color.orange)
            3 -> getColor(R.color.yellow)
            2 -> getColor(R.color.light_green)
            else -> getColor(R.color.green)
        }
        binding.tvUrgencyLevel.setTextColor(urgencyColor)
        
        binding.tvSuggestedAction.text = record.suggestedAction
        
        // 设置状态
        val statusPosition = when (record.status) {
            "待处理" -> 0
            "处理中" -> 1
            "已完成" -> 2
            else -> 0
        }
        binding.spinnerStatus.setSelection(statusPosition)
        
        binding.etHandlerName.setText(record.handlerName)
        binding.etHandleResult.setText(record.handleResult)
        binding.etRemarks.setText(record.remarks)
        
        if (record.handleTime > 0) {
            binding.tvHandleTime.text = "处理时间: ${dateFormat.format(Date(record.handleTime))}"
        } else {
            binding.tvHandleTime.text = "处理时间: 未处理"
        }
    }
    
    private fun updateRecord() {
        val record = currentRecord ?: return
        
        val status = binding.spinnerStatus.selectedItem.toString()
        val handlerName = binding.etHandlerName.text.toString()
        val handleResult = binding.etHandleResult.text.toString()
        val remarks = binding.etRemarks.text.toString()
        
        val updatedRecord = record.copy(
            status = status,
            handlerName = handlerName,
            handleResult = handleResult,
            remarks = remarks,
            handleTime = if (status != "待处理") System.currentTimeMillis() else 0
        )
        
        lifecycleScope.launch {
            viewModel.updateDispatchRecord(updatedRecord)
            Toast.makeText(this@DispatchDetailActivity, "更新成功", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    
    private fun deleteRecord() {
        val record = currentRecord ?: return
        
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("确认删除")
            .setMessage("确定要删除这条接警记录吗？")
            .setPositiveButton("删除") { _, _ ->
                lifecycleScope.launch {
                    viewModel.deleteDispatchRecord(record)
                    Toast.makeText(this@DispatchDetailActivity, "删除成功", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

