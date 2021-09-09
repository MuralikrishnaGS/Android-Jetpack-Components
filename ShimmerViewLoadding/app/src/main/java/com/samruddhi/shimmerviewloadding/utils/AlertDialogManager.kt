package com.samruddhi.shimmerviewloadding.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.widget.TextView
import com.samruddhi.shimmerviewloadding.R

class AlertDialogManager {

    private var mAlertDialog: AlertDialog? = null
    private var mAlertDialogs: MutableSet<AlertDialog?>? = null

    fun createDialogWithOneBtn(
        context: Context,
        title: String,
        message: String
    ) {
        createDialogWithOneBtn(
            context,
            title,
            message,
            context.getString(R.string.shared_btn_ok),
            null
        )
    }

    fun createDialogWithOneBtn(
        context: Context,
        title: String,
        message: String,
        positiveBtnText: String,
        OnPositiveBtnClickListener: DialogInterface.OnClickListener?
    ) {
        createDialogWithOneBtn(
            context,
            title,
            message,
            positiveBtnText,
            OnPositiveBtnClickListener,
            null,
            null
        )
    }

    fun createDialogWithOneBtn(
        context: Context,
        title: String,
        message: String,
        positiveBtnText: String,
        onPositiveBtnClickListener: DialogInterface.OnClickListener?,
        titleContentDescription: String?,
        messageContentDescription: String?
    ) {
        Log.d(
            TAG,
            "create one button dialog with title: " + title + " message: " + message +
                    " button text:" + positiveBtnText +
                    " title accessibility text:" + titleContentDescription +
                    " message accessibility text:" + messageContentDescription
        )
        createDialog(
            context, title, message, positiveBtnText, null, onPositiveBtnClickListener, null, null,
            titleContentDescription, messageContentDescription
        )
    }

    fun createDialogWithTwoBtns(
        context: Context, title: String, message: String,
        positiveBtnText: String,
        negativeBtnText: String,
        OnPositiveBtnClickListener: DialogInterface.OnClickListener?,
        OnNegativeBtnClickListener: DialogInterface.OnClickListener?
    ) {
        Log.d(
            TAG,
            "create two buttons dialog with title: " + title + " message: " + message +
                    " button text:" + positiveBtnText + "," + negativeBtnText
        )
        createDialog(
            context,
            title,
            message,
            positiveBtnText,
            negativeBtnText,
            OnPositiveBtnClickListener,
            OnNegativeBtnClickListener,
            null,
            null,
            null
        )
    }

    fun dismissActiveDialog() {
        if (mAlertDialogs != null && mAlertDialogs!!.contains(mAlertDialog)) {
            mAlertDialogs!!.remove(mAlertDialog)
            mAlertDialog!!.dismiss()
        }
    }

    fun hasAlertDialogs(): Boolean {
        return mAlertDialogs != null && mAlertDialogs!!.isNotEmpty()
    }

    private fun createDialog(
        context: Context, title: String, message: String,
        positiveBtnText: String,
        negativeBtnText: String?,
        OnPositiveBtnClickListener: DialogInterface.OnClickListener?,
        OnNegativeBtnClickListener: DialogInterface.OnClickListener?,
        OnCancelListener: DialogInterface.OnCancelListener?,
        titleContentDescription: String?, messageContentDescription: String?
    ) {
        mAlertDialog = buildDialog(
            context,
            title,
            message,
            positiveBtnText,
            negativeBtnText,
            OnPositiveBtnClickListener,
            OnNegativeBtnClickListener,
            OnCancelListener
        )

        if (!(context as Activity).isFinishing) {
            mAlertDialog!!.show()
        }
        setAccessibilityText(context, titleContentDescription, messageContentDescription)
    }

    private fun buildDialog(
        context: Context,
        title: String,
        message: String,
        positiveBtnText: String,
        negativeBtnText: String?,
        OnPositiveBtnClickListener: DialogInterface.OnClickListener?,
        OnNegativeBtnClickListener: DialogInterface.OnClickListener?,
        OnCancelListener: DialogInterface.OnCancelListener?
    ): AlertDialog {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title).setMessage(message)
            .setPositiveButton(positiveBtnText, OnPositiveBtnClickListener)
            .setNegativeButton(negativeBtnText, OnNegativeBtnClickListener)
            .setCancelable(false)
            .setOnCancelListener(OnCancelListener)
            .setOnDismissListener { dialog: DialogInterface? ->
                if (mAlertDialogs != null) {
                    mAlertDialogs!!.remove(mAlertDialog)
                }
            }
        val alertDialog = builder.create()
        alertDialog.setCanceledOnTouchOutside(false)
        return alertDialog
    }

    private fun setAccessibilityText(
        context: Context,
        title: String?,
        message: String?
    ) {
        if (title != null) {
            val alertTitle =
                context.resources.getIdentifier("alertTitle", "id", "android")
            val titleView = mAlertDialog!!.findViewById<TextView>(alertTitle)
            if (titleView != null) {
                titleView.contentDescription = title
            }
        }
        if (message != null) {
            val bodyView = mAlertDialog!!.findViewById<TextView>(android.R.id.message)
            if (bodyView != null) {
                bodyView.contentDescription = message
            }
        }
    }

    companion object {
        private val TAG = AlertDialogManager::class.java.simpleName
    }
}