package agenda.x.voz.ui.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import agenda.x.voz.databinding.FragmentSettingsBinding
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.core.content.edit
import androidx.navigation.fragment.findNavController

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var myPreferences: SharedPreferences
    //private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSettingsBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDarkModeButton()
        onClickNotificationSwitch()
        onClickDarkModeSwitch()
    }

    private fun onClickDarkModeSwitch() {
        binding.darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
                myPreferences.edit {
                    putBoolean("dark_mode", true)
                    apply()
                }
                binding.darkModeLabel.text = "Modo noche activado"
            } else {
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
                myPreferences.edit {
                    putBoolean("dark_mode", false)
                    apply()
                }
                binding.darkModeLabel.text = "Modo noche desativado"
            }
        }
    }

    private fun onClickNotificationSwitch() {
        binding.notificationSwitch.setOnClickListener {
            it.isSelected = !it.isSelected
            binding.notificationLabel.text = if (it.isSelected) "Notificaciones desactivadas" else "Notificaciones activas"
        }
    }

    private fun setDarkModeButton() {
        myPreferences = requireActivity().getSharedPreferences("MyDarkModePreferences", Context.MODE_PRIVATE)
        val darkMode = myPreferences.getBoolean("dark_mode",false)
        binding.darkModeSwitch.isChecked = darkMode
        if (darkMode) binding.darkModeLabel.text = "Modo noche activado" else binding.darkModeLabel.text = "Modo noche desativado"
    }
}