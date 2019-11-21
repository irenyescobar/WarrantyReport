package com.ireny.warrantyreport.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.ireny.warrantyreport.R

class SettingsFragment : Fragment() {

    private lateinit var notificationsViewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        notificationsViewModel =
            ViewModelProviders.of(this).get(SettingsViewModel::class.java)
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }
}