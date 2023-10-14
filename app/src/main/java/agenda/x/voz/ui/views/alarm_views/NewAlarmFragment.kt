package agenda.x.voz.ui.views.alarm_views

import agenda.x.voz.R
import agenda.x.voz.core.notifications.MyAlarmManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import agenda.x.voz.databinding.FragmentNewAlarmBinding
import agenda.x.voz.domain.model.Alarm
import agenda.x.voz.ui.viewModels.NewAlarmViewModel
import android.Manifest
import android.app.*
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.media.RingtoneManager
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
import java.text.SimpleDateFormat
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
    private var filesDir: File? = null
    private var audioFilePath: File? = null
    private var permissionToRecordAccepted = false
    private var permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)
    private var selectedHour = 0
    private var selectedMinute = 0
    private var selectedYear = 0
    private var selectedMonth = 0
    private var selectedDay = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentNewAlarmBinding.inflate(layoutInflater)
        createNotificationChannel()
        ActivityCompat.requestPermissions(requireActivity(), permissions, REQUEST_RECORD_AUDIO_PERMISSION)
        //requestManageAudio()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        filesDir = requireContext().filesDir

        selectedYear = arguments?.getInt("selected_year", 0) ?: 0
        selectedMonth = arguments?.getInt("selected_month", 0) ?: 0
        selectedDay = arguments?.getInt("selected_day", 0) ?: 0

        onClickListeners()

    }

    private fun onClickListeners() {
        onTimeChange()
        onClickSaveButton()
        onClickCancelButton()
        onClickRecordButton()
        onClickPlayButton()
        onClickRepeatWeekSwitch()
        onClickRepeatDaySwitch()
    }

    private fun onClickCancelButton() {
        binding.cancelButton.setOnClickListener {
            findNavController().popBackStack()
        }
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
        if (!isDateTimePassed(alarmToNotify, -1))
            MyAlarmManager.notification1HourBefore(alarmToNotify, requireActivity(), getAlarmDate(),binding.timePicker.hour - 1)
        if (!isDateTimePassed(alarmToNotify, 0))
            MyAlarmManager.notificationInTimePassed(alarmToNotify, requireActivity(), getAlarmDate())
    }

    private fun getAlarmDate(): Calendar {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, binding.timePicker.hour)
        calendar.set(Calendar.MINUTE, binding.timePicker.minute)
        return calendar
    }

    private fun isDateTimePassed(alarm: Alarm, amount: Int): Boolean {
        val date = "${alarm.day}/${alarm.month}/${alarm.year}"
        val time = "${alarm.hour + (amount)}:${alarm.minute}"
        val dateTimePattern = "dd/MM/yyyy HH:mm"
        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat(dateTimePattern, Locale.getDefault())
        val dateTime = dateFormat.parse("$date $time") ?: return false
        return dateTime.before(currentDate)
    }

    private fun createNotificationChannel() {
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val channel = NotificationChannel("myChannel", "channel", NotificationManager.IMPORTANCE_DEFAULT).apply {
            this.description = "Recordatorio de tareas"
        }
        channel.setSound(soundUri, AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).build())
        val notificationManager = requireContext().getSystemService(NotificationManager::class.java)
        notificationManager?.createNotificationChannel(channel)
    }

    private fun onTimeChange() {
        binding.timePicker.setOnTimeChangedListener { _, hourOfDay, minute ->
            selectedHour = hourOfDay
            selectedMinute = minute
        }
    }
    private fun onClickRepeatWeekSwitch() {
        binding.repeatSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.repeatDaySwitch.isChecked = false
            }
        }
    }
    private fun onClickRepeatDaySwitch() {
        binding.repeatDaySwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                binding.repeatSwitch.isChecked = false
        }
    }
    private fun onClickSaveButton() {
        binding.saveButton.setOnClickListener {
            if (binding.etiquetaEditText.text.toString().isEmpty()) {
                Toast.makeText(requireContext(),"Debes poner una etiqueta o pequeña descripción!",Toast.LENGTH_SHORT).show()
            } else {
                val alarmMap: MutableMap<String,Any> = mutableMapOf()
                alarmMap["name"] = binding.etiquetaEditText.text.toString()
                alarmMap["day"] = selectedDay
                alarmMap["month"] = selectedMonth
                alarmMap["year"] = selectedYear
                alarmMap["hour"] = binding.timePicker.hour
                alarmMap["minute"] = binding.timePicker.minute
                alarmMap["repeat"] = binding.repeatSwitch.isChecked
                alarmMap["repeat_day"] = binding.repeatDaySwitch.isChecked
                alarmMap["complete"] = false
                alarmMap["audioFilePath"] = audioFilePath?.path ?: "prueba"
                newAlarmViewModel.saveAlarm(alarmMap)
                newAlarmViewModel.savedAlarm.observe(viewLifecycleOwner) {
                    Toast.makeText(requireContext(),"Tarea añadida con éxito",Toast.LENGTH_SHORT).show()
                    scheduleNotification(it!!)
                    findNavController().navigate(R.id.action_newAlarmsFragment_to_calendarFragment)
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
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
        audioFilePath = File(filesDir, alarmName)
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