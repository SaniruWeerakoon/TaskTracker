package com.example.tasktracker.fragments

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.tasktracker.R
import com.example.tasktracker.databinding.FragmentSettingsBinding
import com.google.firebase.auth.FirebaseAuth

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var navController: NavController


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        registerEvents()
    }

    private fun logout() {
        firebaseAuth.signOut()
        navController.navigate(R.id.action_settingsFragment_to_signInFragment)

    }

    private fun init(view: View) {
        navController = Navigation.findNavController(view)
        firebaseAuth = FirebaseAuth.getInstance()
        binding.darkModeSwitch.isChecked = isDarkModeEnabled()
        binding.adaptiveBrightnessSwitch.isChecked = isAdaptiveBrightnessEnabled()
    }

    private fun registerEvents() {
        binding.btnNavBack.setOnClickListener {
            navController.navigate(R.id.action_settingsFragment_to_homeFragment)
        }
        binding.btnLogout.setOnClickListener {
            binding.btnLogout.isEnabled = false
            logout()
            binding.btnLogout.isEnabled = true
        }

        binding.darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked != isDarkModeEnabled()) {
                Log.d(TAG, "Dark Mode is ${if (isChecked) "enabled" else "disabled"}")
                setDarkMode(isChecked)
            }
        }

        binding.adaptiveBrightnessSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Log.d(TAG, "registerEvents: Adaptive Brightness is enabled")
                enableAdaptiveBrightness()
            } else {
                Log.d(TAG, "registerEvents: Adaptive Brightness is disabled")
                disableAdaptiveBrightness()
            }
        }
    }

    private fun isDarkModeEnabled(): Boolean {
        return resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }

    private fun setDarkMode(enabled: Boolean) {
        val mode =
            if (enabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(mode)
        requireActivity().recreate()
    }

    private fun isAdaptiveBrightnessEnabled(): Boolean {
        return Settings.System.getInt(
            requireContext().contentResolver,
            Settings.System.SCREEN_BRIGHTNESS_MODE,
            Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
        ) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
    }

    private fun enableAdaptiveBrightness() {
        if (!Settings.System.canWrite(requireContext())) {
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
            intent.data = Uri.parse("package:" + requireContext().packageName)
            startActivity(intent)
        } else {
            Settings.System.putInt(
                requireContext().contentResolver,
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
            )
        }
    }

    private fun disableAdaptiveBrightness() {
        Settings.System.putInt(
            requireContext().contentResolver,
            Settings.System.SCREEN_BRIGHTNESS_MODE,
            Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
        )
    }


}