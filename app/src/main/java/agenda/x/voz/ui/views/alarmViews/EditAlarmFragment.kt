package agenda.x.voz.ui.views.alarmViews

import agenda.x.voz.R
import agenda.x.voz.data.model.AlarmModel
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import agenda.x.voz.databinding.FragmentEditAlarmBinding
import agenda.x.voz.domain.model.Alarm
import agenda.x.voz.domain.model.toDomain
import agenda.x.voz.ui.viewModels.EditAlarmViewModel
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Environment
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
import kotlin.io.path.Path
import kotlin.io.path.deleteIfExists

@AndroidEntryPoint
class EditAlarmFragment : Fragment() {
    private lateinit var binding: FragmentEditAlarmBinding
    private val editAlarmViewModel: EditAlarmViewModel by viewModels()
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private var isRecording = false
    private var isPlaying = false
    private var externalFilesDir: File? = null
    private lateinit var audioFilePath: File
    private var permissionToRecordAccepted = false
    private var permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)
    private var selectedHour = 0
    private var selectedMinute = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditAlarmBinding.inflate(layoutInflater)
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
        val alarm = arguments?.getParcelable<AlarmModel>("alarm")
        editAlarmViewModel.setAlarm(alarm!!.toDomain())
        editAlarmViewModel.alarmToEditAlarm.observe(this) {
            setTimePicker(it)
            setDatePicker(it)
            binding.etiquetaEditText.setText(it.name)
            binding.repeatSwitch.isChecked = it.repeat
            audioFilePath = Path(alarm.audioFilePath).toFile()
            onClickSaveButton(it)
            onClickPlayButton()
            onClickRecordButton()
        }
        onTimeChange()
        onClickCancelButton()
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

    private fun onClickCancelButton() {
        binding.cancelButton.setOnClickListener {
            findNavController().navigate(R.id.action_editAlarmsFragment_to_todayAlarmsFragment)
        }
    }

    private fun setDatePicker(alarm: Alarm) {
        binding.dayPicker.minValue = 1
        binding.dayPicker.maxValue = 31
        binding.monthPicker.minValue = 1
        binding.monthPicker.maxValue = 12
        binding.yearPicker.minValue = 2023
        binding.yearPicker.maxValue = 2100
        binding.dayPicker.value = alarm.day?: currentDate.get(Calendar.DAY_OF_MONTH)
        binding.monthPicker.value = alarm.month?: currentDate.get(Calendar.MONTH)
        binding.yearPicker.value = alarm.year?: currentDate.get(Calendar.YEAR)
    }

    private fun setTimePicker(alarm: Alarm) {
        binding.timePicker.hour = alarm.hour
        binding.timePicker.minute = alarm.minute
    }

    private fun onTimeChange() {
        binding.timePicker.setOnTimeChangedListener { _, hourOfDay, minute ->
            selectedHour = hourOfDay
            selectedMinute = minute
        }
    }

    private fun onClickSaveButton(alarm: Alarm) {
        binding.saveButton.setOnClickListener {
            val alarmEdited = Alarm(
                alarm.id,
                binding.etiquetaEditText.text.toString(),
                binding.dayPicker.value,
                binding.monthPicker.value,
                binding.yearPicker.value,
                selectedHour,
                selectedMinute,
                binding.repeatSwitch.isChecked,
                alarm.complete,
                audioFilePath.path
            )
            editAlarmViewModel.editMyAlarm(alarmEdited)
            Toast.makeText(requireContext(),"Tarea editada con éxito",Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_editAlarmsFragment_to_todayAlarmsFragment)
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
        binding.recordButtonLabel.text = "Grabando..."
        binding.recordButton.text = resources.getString(R.string.cuadrado)
        audioFilePath = File(externalFilesDir, alarmName)
        audioFilePath.toPath().deleteIfExists()
        audioFilePath.createNewFile()
        startRecording()
    } else {
        if(audioFilePath.exists()) {
            binding.recordButtonLabel.text = "Capturado"
            binding.playButton.visibility = View.VISIBLE
            binding.playButtonLabel.visibility = View.VISIBLE
        }
        else {
            binding.recordButtonLabel.text = "Grabar"
            binding.playButton.visibility = View.GONE
            binding.playButtonLabel.visibility = View.GONE
        }
        binding.recordButton.text = resources.getString(R.string.red_circle)
        stopRecording()
    }

    private fun onPlay(start: Boolean) = if (start) {
        binding.playButtonLabel.text = "Reproduciendo..."
        binding.playButton.text = resources.getString(R.string.cuadrado)
        binding.playButton.textSize = 18f
        binding.recordButton.visibility = View.GONE
        binding.recordButtonLabel.visibility = View.GONE
        startPlaying()
    } else {
        binding.playButtonLabel.text = "Reproducir"
        binding.playButton.text = resources.getString(R.string.play_button)
        binding.playButton.textSize = 28f
        binding.recordButton.visibility = View.VISIBLE
        binding.recordButtonLabel.visibility = View.VISIBLE
        stopPlaying()
    }

    private fun startPlaying() {
        player = MediaPlayer().apply {
            try {
                setDataSource(audioFilePath.path)
                prepare()
                setOnCompletionListener {
                    this@EditAlarmFragment.isPlaying = !this@EditAlarmFragment.isPlaying
                    onPlay(this@EditAlarmFragment.isPlaying)
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
            setOutputFile(audioFilePath.path)
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
        private val currentDate = Calendar.getInstance()
        private const val LOG_TAG = "AudioRecordTest"
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
    }
}