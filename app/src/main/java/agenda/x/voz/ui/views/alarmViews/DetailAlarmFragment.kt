package agenda.x.voz.ui.views.alarmViews

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import agenda.x.voz.R
import agenda.x.voz.data.model.AlarmModel
import agenda.x.voz.databinding.FragmentDetailAlarmBinding
import agenda.x.voz.domain.model.toDomain
import agenda.x.voz.ui.viewModels.DetailAlarmViewModel
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.viewModels
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
            val time = "${alarm.hour} : ${alarm.minute} h"
            binding.time.text = time
            onClickPlayButton()
            if (it.repeat) {
                binding.dateLabel.visibility = View.GONE
                binding.date.visibility = View.GONE
                val calendar = currentDate
                calendar.set(Calendar.DAY_OF_MONTH, it.day!!)
                calendar.set(Calendar.MONTH, it.month!!)
                calendar.set(Calendar.YEAR, it.year!!)
                val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                val dayOfWeekString = dayOfWeekToString(dayOfWeek)
                binding.isRepeating.text = dayOfWeekString
            } else {
                binding.isRepeating.visibility = View.GONE
                binding.repeatLabel.visibility = View.GONE
                val date = "${alarm.day} / ${alarm.month} / ${alarm.year}"
                binding.date.text = date
            }
        }
        //onClickBackButton()
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
            binding.playButtonLabel.text = "Reproduciendo..."
            binding.playButton.textSize = 18f
            binding.playButton.text = resources.getString(R.string.cuadrado)
            startPlaying()
        } else {
            Toast.makeText(requireContext(),"No hay audio. Para grabar uno ves a editar tarea",Toast.LENGTH_LONG).show()
        }
    } else {
        binding.playButtonLabel.text = "Reproducir"
        binding.playButton.textSize = 28f
        binding.playButton.text = resources.getString(R.string.play_button)
        stopPlaying()
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