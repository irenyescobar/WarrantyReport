package com.ireny.warrantyreport.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.ireny.warrantyreport.LoginActivity
import com.ireny.warrantyreport.R
import com.ireny.warrantyreport.services.UserAccountManager
import com.ireny.warrantyreport.utils.customApp
import kotlinx.android.synthetic.main.fragment_settings.*


class SettingsFragment : Fragment() {

    private lateinit var notificationsViewModel: SettingsViewModel

    private val component by lazy { customApp.component }
    private val accountMannager: UserAccountManager by lazy { component.userAccountManager() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        notificationsViewModel =
            ViewModelProviders.of(this).get(SettingsViewModel::class.java)
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        signOut.setOnClickListener{
            accountMannager.googleSignInClient.signOut().addOnCompleteListener {
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.run {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra("EXIT", true)
                }
                startActivity(intent)
                requireActivity().finish()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val account = accountMannager.getUserAccount()
        if(account != null) {

            Glide.with(requireActivity())
                .load(account.photoUrl)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .transform(CircleCrop())
                .into(profileAvatar)

            profileName.text = account.displayName
            profileEmail.text = account.email

        }else{

            profileAvatar.setImageResource(R.drawable.ic_account)
            profileName.text = getString(R.string.desconnected_profile_name)
            profileEmail.text = ""
        }
    }
}