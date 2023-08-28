package agenda.x.voz.ui.views.alarmViews

import agenda.x.voz.R
import agenda.x.voz.core.notifications.AlarmNotification
import agenda.x.voz.core.notifications.AlarmNotification.Companion.message
import agenda.x.voz.core.notifications.AlarmNotification.Companion.notificationId
import agenda.x.voz.core.notifications.AlarmNotification.Companion.title
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import agenda.x.voz.databinding.FragmentNewAlarmBinding
import agenda.x.voz.domain.model.Alarm
import agenda.x.voz.ui.viewModels.NewAlarmViewModel
import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.io.path.deleteIfExists

@AndroidEntryPoint
class NewAlarmFragment : Fragment() {
    private lateinit var binding: FragmentNewAlarmBinding
    private val newAlarmViewModel: NewAlarmViewModel by viewModels()
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private var isRecording = false
    private var isPlaying = false
    private var externalFilesDir: File? = null
    private var audioFilePath: File? = null
    private var permissionToRecordAccepted = false
    private var permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)
    private var selectedHour = 0
    private var selectedMinute = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentNewAlarmBinding.inflate(layoutInflater)
        createNotificationChannel()
        ActivityCompat.requestPermissions(requireActivity(), permissions, REQUEST_RECORD_AUDIO_PERMISSION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            val uri = Uri.fromParts("package", requireActivity().packageName, null)
            intent.data = uri
            startActivity(intent)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        externalFilesDir = requireContext().getExternalFilesDir(null)
        loadDatePicker()
        onTimeChange()
        onClickSaveButton()
        onClickCancelButton()
        onClickRecordButton()
        onClickPlayButton()
    }

    private fun onClickCancelButton() {
        binding.cancelButton.setOnClickListener {
            findNavController().navigate(R.id.action_newAlarmsFragment_to_todayAlarmsFragment)
        }
    }

    private fun loadDatePicker() {
        val currentDate = Calendar.getInstance()
        binding.dayPicker.minValue = 1
        binding.dayPicker.maxValue = 31
        binding.dayPicker.value = currentDate.get(Calendar.DAY_OF_MONTH)

        binding.monthPicker.minValue = 1
        binding.monthPicker.maxValue = 12
        binding.monthPicker.value = currentDate.get(Calendar.MONTH) + 1

        binding.yearPicker.minValue = 2023
        binding.yearPicker.maxValue = 2100
        binding.yearPicker.value = currentDate.get(Calendar.YEAR)
    }

    private fun onClickPlayButton() {
        binding.playButton.setOnClickListener {
            isPlaying = !isPlaying
            onPlay(isPlaying)
        }
    }
    private fun onClickRecordButton() {
        binding.recordButton.setOnClickListener {
            isRecording = !isRecording
            val alarmName = binding.etiquetaEditText.text.toString()
            if (alarmName.isEmpty())
                Toast.makeText(requireContext(),"Ponle un nombre a la tarea primero!",Toast.LENGTH_SHORT).show()
            else {
                onRecord(isRecording,alarmName)
            }
        }
    }

    private fun scheduleNotification(alarmToNotify: Alarm) {
        /*notificationId = alarmToNotify.id.toInt()
        title = "Falta 1 HORA para..."
        message = alarmToNotify.name*/
        val intent = Intent(requireActivity().applicationContext, AlarmNotification::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireActivity().applicationContext,
            1,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val dateInMillis = getAlarmDateInMillis()
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, Calendar.getInstance().timeInMillis + dateInMillis, pendingIntent)
    }

    private fun getAlarmDateInMillis(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, binding.dayPicker.value)
        calendar.set(Calendar.MONTH, binding.monthPicker.value)
        calendar.set(Calendar.DAY_OF_MONTH, binding.yearPicker.value)
        calendar.set(Calendar.HOUR, binding.timePicker.hour)
        calendar.set(Calendar.MINUTE, binding.timePicker.minute)
        return calendar.timeInMillis - Calendar.getInstance().timeInMillis
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel("myChannel", "channel", NotificationManager.IMPORTANCE_DEFAULT).apply {
            this.description = "Recordatorio de tareas"
        }
        val notificationManager = requireContext().getSystemService(NotificationManager::class.java)
        notificationManager?.createNotificationChannel(channel)
    }

    private fun onTimeChange() {
        binding.timePicker.setOnTimeChangedListener { _, hourOfDay, minute ->
            selectedHour = hourOfDay
            selectedMinute = minute
        }
    }
    private fun onClickSaveButton() {
        binding.saveButton.setOnClickListener {
            val alarmMap: MutableMap<String,Any> = mutableMapOf()
            alarmMap["name"] = binding.etiquetaEditText.text.toString()
            alarmMap["day"] = binding.dayPicker.value
            alarmMap["month"] = binding.monthPicker.value
            alarmMap["year"] = binding.yearPicker.value
            alarmMap["hour"] = binding.timePicker.hour
            alarmMap["minute"] = binding.timePicker.minute
            alarmMap["repeat"] = binding.repeatSwitch.isChecked
            alarmMap["complete"] = false
            alarmMap["audioFilePath"] = audioFilePath?.path ?: "prueba"
            newAlarmViewModel.saveAlarm(alarmMap)
            Toast.makeText(requireContext(),"Tarea añadida con éxito",Toast.LENGTH_SHORT).show()
            newAlarmViewModel.savedAlarm.observe(this) {
                scheduleNotification(it!!)
            }
            findNavController().navigate(R.id.action_newAlarmsFragment_to_todayAlarmsFragment)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionToRecordAccepted = if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }
        if (!permissionToRecordAccepted) onStop()
    }

    private fun onRecord(start: Boolean, alarmName: String) = if (start) {
        binding.playButton.visibility = View.GONE
        binding.playButtonLabel.visibility = View.GONE
        binding.etiquetaEditText.isEnabled = false
        binding.recordButtonLabel.text = "Grabando..."
        binding.recordButton.text = resources.getString(R.string.cuadrado)
        audioFilePath = File(externalFilesDir, alarmName)
        audioFilePath!!.toPath().deleteIfExists()
        audioFilePath!!.createNewFile()
        startRecording()
    } else {
        binding.etiquetaEditText.isEnabled = false
        if(audioFilePath != null && audioFilePath!!.exists()) {
            binding.recordButtonLabel.text = "Capturado"
            binding.playButton.visibility = View.VISIBLE
            binding.playButtonLabel.visibility = View.VISIBLE
        }
        else {
            binding.etiquetaEditText.isEnabled = true
            binding.recordButtonLabel.text = "Grabar"
            binding.playButton.visibility = View.GONE
            binding.playButtonLabel.visibility = View.GONE
        }
        binding.recordButton.text = resources.getString(R.string.red_circle)
        stopRecording()
    }

    private fun onPlay(start: Boolean) = if (start) {
        binding.playButtonLabel.text = "Reproduciendo..."
        binding.playButton.textSize = 18f
        binding.playButton.text = resources.getString(R.string.cuadrado)
        binding.recordButton.visibility = View.GONE
        binding.recordButtonLabel.visibility = View.GONE
        startPlaying()
    } else {
        binding.playButtonLabel.text = "Reproducir"
        binding.playButton.textSize = 28f
        binding.playButton.text = resources.getString(R.string.play_button)
        binding.recordButton.visibility = View.VISIBLE
        binding.recordButtonLabel.visibility = View.VISIBLE
        stopPlaying()
    }

    private fun startPlaying() {
        player = MediaPlayer().apply {
            try {
                setDataSource(audioFilePath!!.path)
                prepare()
                setOnCompletionListener {
                    this@NewAlarmFragment.isPlaying = !this@NewAlarmFragment.isPlaying
                    onPlay(this@NewAlarmFragment.isPlaying)
                }
                start()
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
            }
        }
    }

    private fun stopPlaying() {
        player?.release()
        player = null
    }

    private fun startRecording() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(audioFilePath!!.path)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
            }

            start()
        }
    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
    }

    override fun onStop() {
        super.onStop()
        recorder?.release()
        recorder = null
        player?.release()
        player = null
    }

    companion object {
        private const val LOG_TAG = "AudioRecordTest"
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
    }
}