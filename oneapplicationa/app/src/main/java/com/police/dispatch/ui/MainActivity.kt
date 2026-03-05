package com.police.dispatch.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.police.dispatch.R
import com.police.dispatch.databinding.ActivityMainBinding
import com.police.dispatch.viewmodel.DispatchViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: DispatchViewModel
    private var speechRecognizer: SpeechRecognizer? = null
    private var isRecording = false
    
    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        viewModel = ViewModelProvider(this)[DispatchViewModel::class.java]
        
        setupUI()
        setupSpeechRecognizer()
        observeViewModel()
        checkPermissions()
    }
    
    private fun setupUI() {
        // 开始录音按钮
        binding.btnStartRecord.setOnClickListener {
            if (isRecording) {
                stopRecording()
            } else {
                startRecording()
            }
        }
        
        // 分析按钮
        binding.btnAnalyze.setOnClickListener {
            val text = binding.etVoiceText.text.toString()
            if (text.isNotEmpty()) {
                analyzeText(text)
            } else {
                Toast.makeText(this, "请先输入或录音报警内容", Toast.LENGTH_SHORT).show()
            }
        }
        
        // 保存接警单
        binding.btnSave.setOnClickListener {
            saveDispatchRecord()
        }
        
        // 查看历史记录
        binding.btnHistory.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
        
        // 清空表单
        binding.btnClear.setOnClickListener {
            clearForm()
        }
    }
    
    private fun setupSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
            speechRecognizer?.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    binding.tvRecordStatus.text = "请开始说话..."
                }
                
                override fun onBeginningOfSpeech() {
                    binding.tvRecordStatus.text = "正在录音中..."
                }
                
                override fun onRmsChanged(rmsdB: Float) {}
                
                override fun onBufferReceived(buffer: ByteArray?) {}
                
                override fun onEndOfSpeech() {
                    binding.tvRecordStatus.text = "录音结束，正在识别..."
                }
                
                override fun onError(error: Int) {
                    val errorMsg = when (error) {
                        SpeechRecognizer.ERROR_AUDIO -> "音频错误"
                        SpeechRecognizer.ERROR_CLIENT -> "客户端错误"
                        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "权限不足"
                        SpeechRecognizer.ERROR_NETWORK -> "网络错误"
                        SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "网络超时"
                        SpeechRecognizer.ERROR_NO_MATCH -> "无法识别"
                        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "识别器忙"
                        SpeechRecognizer.ERROR_SERVER -> "服务器错误"
                        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "语音超时"
                        else -> "未知错误"
                    }
                    binding.tvRecordStatus.text = "识别失败: $errorMsg"
                    isRecording = false
                    updateRecordButton()
                }
                
                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        val text = matches[0]
                        val currentText = binding.etVoiceText.text.toString()
                        binding.etVoiceText.setText(if (currentText.isEmpty()) text else "$currentText $text")
                        binding.tvRecordStatus.text = "识别成功"
                    }
                    isRecording = false
                    updateRecordButton()
                }
                
                override fun onPartialResults(partialResults: Bundle?) {}
                
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        } else {
            Toast.makeText(this, "您的设备不支持语音识别", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun startRecording() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                REQUEST_RECORD_AUDIO_PERMISSION
            )
            return
        }
        
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "zh-CN")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
        
        speechRecognizer?.startListening(intent)
        isRecording = true
        updateRecordButton()
    }
    
    private fun stopRecording() {
        speechRecognizer?.stopListening()
        isRecording = false
        updateRecordButton()
    }
    
    private fun updateRecordButton() {
        if (isRecording) {
            binding.btnStartRecord.text = "停止录音"
            binding.btnStartRecord.setBackgroundColor(getColor(R.color.red))
        } else {
            binding.btnStartRecord.text = "开始录音"
            binding.btnStartRecord.setBackgroundColor(getColor(R.color.primary))
        }
    }
    
    private fun analyzeText(text: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnAnalyze.isEnabled = false
        
        lifecycleScope.launch {
            viewModel.analyzeVoiceText(text)
        }
    }
    
    private fun observeViewModel() {
        viewModel.analysisResult.observe(this) { result ->
            binding.progressBar.visibility = View.GONE
            binding.btnAnalyze.isEnabled = true
            
            if (result.success) {
                // 填充分析结果到表单
                binding.etLocation.setText(result.location)
                binding.etIncidentTime.setText(result.incidentTime)
                binding.etInvolvedPersons.setText(result.involvedPersons)
                binding.etIncidentDescription.setText(result.incidentDescription)
                binding.tvIncidentType.text = "警情类型: ${result.incidentType}"
                binding.tvUrgencyLevel.text = "紧急程度: ${result.urgencyLevel}级"
                binding.etSuggestedAction.setText(result.suggestedAction)
                
                // 根据紧急程度设置颜色
                val color = when (result.urgencyLevel) {
                    5 -> getColor(R.color.red)
                    4 -> getColor(R.color.orange)
                    3 -> getColor(R.color.yellow)
                    2 -> getColor(R.color.light_green)
                    else -> getColor(R.color.green)
                }
                binding.tvUrgencyLevel.setTextColor(color)
                
                Toast.makeText(this, "AI分析完成", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, result.errorMessage, Toast.LENGTH_LONG).show()
            }
        }
        
        viewModel.saveResult.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "接警单保存成功", Toast.LENGTH_SHORT).show()
                clearForm()
            } else {
                Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun saveDispatchRecord() {
        val voiceText = binding.etVoiceText.text.toString()
        val location = binding.etLocation.text.toString()
        val incidentTime = binding.etIncidentTime.text.toString()
        val involvedPersons = binding.etInvolvedPersons.text.toString()
        val incidentDescription = binding.etIncidentDescription.text.toString()
        val callerPhone = binding.etCallerPhone.text.toString()
        val callerName = binding.etCallerName.text.toString()
        
        if (voiceText.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "请至少填写报警内容和地点", Toast.LENGTH_SHORT).show()
            return
        }
        
        lifecycleScope.launch {
            viewModel.saveDispatchRecord(
                voiceText = voiceText,
                callerPhone = callerPhone,
                callerName = callerName,
                location = location,
                incidentTime = incidentTime,
                involvedPersons = involvedPersons,
                incidentDescription = incidentDescription
            )
        }
    }
    
    private fun clearForm() {
        binding.etVoiceText.setText("")
        binding.etCallerPhone.setText("")
        binding.etCallerName.setText("")
        binding.etLocation.setText("")
        binding.etIncidentTime.setText("")
        binding.etInvolvedPersons.setText("")
        binding.etIncidentDescription.setText("")
        binding.etSuggestedAction.setText("")
        binding.tvIncidentType.text = "警情类型: 待分析"
        binding.tvUrgencyLevel.text = "紧急程度: 待分析"
        binding.tvRecordStatus.text = "点击开始录音按钮"
    }
    
    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                REQUEST_RECORD_AUDIO_PERMISSION
            )
        }
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "录音权限已授予", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "需要录音权限才能使用语音识别功能", Toast.LENGTH_LONG).show()
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer?.destroy()
    }
}

