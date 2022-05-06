package com.jamal.myread.ui.fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * TODO Business logic code need to be in a ViewModel Class.
 */
@AndroidEntryPoint
class AlertDialogFragment: DialogFragment() {

    private val TAG = "AlertDialogFragment"
    private val getResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "GetResult is called")
        } else {
            Log.d(TAG, "RESULT_OK is false")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        return builder.apply {
            setCancelable(true)
            setTitle("Screen Overlay Permission Needed")
            setMessage("Enable 'Display over the App' from settings")
            setPositiveButton("Open Settings") { dialog, which ->
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:${requireContext().packageName}")
                )


                getResult.launch(intent)
            }
        }.create()
    }
}