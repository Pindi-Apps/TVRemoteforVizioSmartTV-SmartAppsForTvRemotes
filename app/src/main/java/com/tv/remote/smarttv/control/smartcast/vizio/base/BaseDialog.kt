package com.tv.remote.smarttv.control.smartcast.vizio.base

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.tv.remote.smarttv.control.smartcast.vizio.R

abstract class BaseDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        setStyle(STYLE_NORMAL, R.style.GeneralDialogStyle)
        val builder = AlertDialog.Builder(requireContext(), R.style.GeneralDialogStyle)
        val inflater = requireActivity().layoutInflater
        val dialog = inflater.inflate(getLayoutId(), null)
        initViews(dialog)
        actions()
        builder.setView(dialog)
        return builder.create()
    }

    abstract fun initViews(view: View)
    abstract fun actions()
    abstract fun getLayoutId(): Int

}