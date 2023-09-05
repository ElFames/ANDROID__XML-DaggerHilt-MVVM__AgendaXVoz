package agenda.x.voz.ui.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import agenda.x.voz.databinding.FragmentSettingsBinding
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    //private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSettingsBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.notificationSwitch.setOnClickListener {
            it.isSelected = !it.isSelected
            binding.notificationLabel.text = if (it.isSelected) "Notificaciones desactivadas" else "Notificaciones activas"
        }

        binding.darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
                binding.darkModeLabel.text = "Modo noche activado"
            } else {
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
                binding.darkModeLabel.text = "Modo noche desativado"
            }
        }
    }
}