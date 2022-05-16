package com.jamal.myread.ui.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import com.jamal.myread.R
import dagger.hilt.android.AndroidEntryPoint

private const val PACKAGE = "package:"

@AndroidEntryPoint
class AlertDialogFragment : DialogFragment() {

    private val getResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        return builder.apply {
            setCancelable(true)
            setTitle(resources.getString(R.string.screen_overlay_permission))
            setMessage(resources.getString(R.string.enable_settings_overlay))
            setPositiveButton(resources.getString(R.string.open_settings)) { _, _ ->
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse(PACKAGE + requireContext().packageName)
                )

                getResult.launch(intent)
            }
        }.create()
    }
}