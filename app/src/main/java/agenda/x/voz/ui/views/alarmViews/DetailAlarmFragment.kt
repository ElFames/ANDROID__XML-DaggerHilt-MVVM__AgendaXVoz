package agenda.x.voz.ui.views.alarmViews

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import agenda.x.voz.R
import agenda.x.voz.data.model.AlarmModel
import agenda.x.voz.databinding.FragmentDetailAlarmBinding
import agenda.x.voz.domain.model.Alarm
import agenda.x.voz.domain.model.toDomain
import agenda.x.voz.ui.viewModels.DetailAlarmViewModel
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.io.path.Path

@AndroidEntryPoint
class DetailAlarmFragment : Fragment() {
    private lateinit var binding: FragmentDetailAlarmBinding
    private val detailAlarmViewModel: DetailAlarmViewModel by viewModels()
    private var player: MediaPlayer? = null
    private var isPlaying = false
    private lateinit var audioFilePath: File

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDetailAlarmBinding.inflate(layoutInflater)
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

        val alarm = arguments?.getParcelable<AlarmModel>("alarm")
        detailAlarmViewModel.setAlarm(alarm!!.toDomain())

        detailAlarmViewModel.alarm.observe(this) {
            audioFilePath = Path(it.audioFilePath).toFile()
            binding.etiqueta.text = it.name
            setTime(it)
            onClickPlayButton()
            onClickDeleteButton()
            if (it.repeat) {
                hideDate()
                showDayIsRepeating(it)
            } else {
                hideRepeatLabels()
                showDate(it)
            }
        }
    }

    private fun showDate(alarm: Alarm) {
        val date = "${alarm.day} / ${alarm.month} / ${alarm.year}"
        binding.date.text = date
    }

    private fun hideRepeatLabels() {
        binding.isRepeating.visibility = View.GONE
        binding.repeatLabel.visibility = View.GONE
    }

    private fun showDayIsRepeating(alarm: Alarm) {
        currentDate.set(Calendar.DAY_OF_MONTH, alarm.day!!)
        currentDate.set(Calendar.MONTH, alarm.month!!)
        currentDate.set(Calendar.YEAR, alarm.year!!)
        val dayOfWeek = currentDate.get(Calendar.DAY_OF_WEEK)
        val dayOfWeekString = dayOfWeekToString(dayOfWeek)
        binding.isRepeating.text = dayOfWeekString
    }

    private fun hideDate() {
        binding.dateLabel.visibility = View.GONE
        binding.date.visibility = View.GONE
    }

    private fun setTime(alarm: Alarm) {
        val hour = String.format("%02d", alarm.hour)
        val minute = String.format("%02d", alarm.minute)
        val time = "$hour : $minute h"
        binding.time.text = time
    }

    private fun dayOfWeekToString(dayOfWeek: Int): String {
         return when(dayOfWeek) {
             1 -> "Domingo"
             2 -> "Lunes"
             3 -> "Martes"
             4 -> "Miercoles"
             5 -> "Jueves"
             6 -> "Viernes"
             7 -> "Sabado"
             else -> ""
         }
    }

    private fun onClickPlayButton() {
        binding.playButton.setOnClickListener {
            isPlaying = !isPlaying
            onPlay(isPlaying)
        }
    }

    private fun onPlay(start: Boolean) = if (start) {
        if (audioFilePath.exists()) {
            binding.playButton.setImageResource(R.drawable.ic_stop_button)
            startPlaying()
            player?.setOnPreparedListener {
                binding.progressBar.max = it.duration
                val updateHandler = Handler()
                val updateRunnable = object : Runnable {
                    override fun run() {
                        val currentPosition = player?.currentPosition
                        binding.progressBar.progress = currentPosition ?: 0
                        updateHandler.postDelayed(this, 100) // Actualizar cada 100ms
                    }
                }
                updateHandler.postDelayed(updateRunnable, 0)
            }
        } else {
            Toast.makeText(requireContext(),"No hay audio. Para grabar uno ves a editar tarea",Toast.LENGTH_LONG).show()
        }
    } else {
        binding.playButton.setImageResource(R.drawable.ic_play_button)
        stopPlaying()
        binding.progressBar.progress = 0
    }

    private fun startPlaying() {
        player = MediaPlayer().apply {
            try {
                setDataSource(audioFilePath.path)
                prepare()
                setOnCompletionListener {
                    this@DetailAlarmFragment.isPlaying = !this@DetailAlarmFragment.isPlaying
                    onPlay(this@DetailAlarmFragment.isPlaying)
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

    private fun onClickDeleteButton() {
        binding.deleteButton.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Eliminar Alarma")
                .setMessage("¿Estás seguro de que quieres eliminar esta alarma?")
                .setPositiveButton("Eliminar") { dialogInterface: DialogInterface, _: Int ->
                    detailAlarmViewModel.deleteCurrentAlarm()
                    Toast.makeText(requireContext(),"Tarea eliminada!",Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_detailAlarmFragment_to_todayAlarmsFragment)
                    dialogInterface.dismiss()
                }
                .setNegativeButton("Cancelar") { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                }
                .create()
                .show()
        }
    }

    override fun onStop() {
        super.onStop()
        player?.release()
        player = null
    }

    companion object {
        private var currentDate = Calendar.getInstance()
        private const val LOG_TAG = "AudioRecordTest"
    }
}