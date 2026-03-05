package com.police.dispatch.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.police.dispatch.R
import com.police.dispatch.data.model.DispatchRecord
import com.police.dispatch.databinding.ActivityHistoryBinding
import com.police.dispatch.databinding.ItemDispatchRecordBinding
import com.police.dispatch.viewmodel.DispatchViewModel
import java.text.SimpleDateFormat
import java.util.*

class HistoryActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityHistoryBinding
    private lateinit var viewModel: DispatchViewModel
    private lateinit var adapter: DispatchRecordAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        viewModel = ViewModelProvider(this)[DispatchViewModel::class.java]
        
        setupUI()
        observeViewModel()
    }
    
    private fun setupUI() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "历史记录"
        
        adapter = DispatchRecordAdapter { record ->
            // 点击查看详情
            DispatchDetailActivity.start(this, record.id)
        }
        
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }
    
    private fun observeViewModel() {
        viewModel.allRecords.observe(this) { records ->
            if (records.isEmpty()) {
                binding.tvEmpty.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            } else {
                binding.tvEmpty.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                adapter.submitList(records)
            }
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

class DispatchRecordAdapter(
    private val onItemClick: (DispatchRecord) -> Unit
) : RecyclerView.Adapter<DispatchRecordAdapter.ViewHolder>() {
    
    private var records = listOf<DispatchRecord>()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
    
    fun submitList(newRecords: List<DispatchRecord>) {
        records = newRecords
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDispatchRecordBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(records[position])
    }
    
    override fun getItemCount() = records.size
    
    inner class ViewHolder(
        private val binding: ItemDispatchRecordBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(record: DispatchRecord) {
            binding.tvCallTime.text = dateFormat.format(Date(record.callTime))
            binding.tvIncidentType.text = record.incidentType.ifEmpty { "未分类" }
            binding.tvLocation.text = record.location.ifEmpty { "地点未知" }
            binding.tvStatus.text = record.status
            
            // 紧急程度颜色
            val urgencyColor = when (record.urgencyLevel) {
                5 -> itemView.context.getColor(R.color.red)
                4 -> itemView.context.getColor(R.color.orange)
                3 -> itemView.context.getColor(R.color.yellow)
                2 -> itemView.context.getColor(R.color.light_green)
                else -> itemView.context.getColor(R.color.green)
            }
            binding.tvUrgencyLevel.text = "${record.urgencyLevel}级"
            binding.tvUrgencyLevel.setTextColor(urgencyColor)
            
            // 状态颜色
            val statusColor = when (record.status) {
                "待处理" -> itemView.context.getColor(R.color.orange)
                "处理中" -> itemView.context.getColor(R.color.primary)
                "已完成" -> itemView.context.getColor(R.color.green)
                else -> itemView.context.getColor(R.color.gray)
            }
            binding.tvStatus.setTextColor(statusColor)
            
            binding.root.setOnClickListener {
                onItemClick(record)
            }
        }
    }
}

