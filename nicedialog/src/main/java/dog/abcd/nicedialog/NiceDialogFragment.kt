package dog.abcd.nicedialog

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment

class NiceDialogFragment<T : ViewDataBinding> : DialogFragment() {

    lateinit var binding: T

    private var niceDialog: NiceDialog<T>? = null

    /**
     * 根据tag从NiceDialog取实例
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        try {
            niceDialog = NiceDialog.getNiceDialog(tag!!)!!
            binding = niceDialog!!.binding
            niceDialog?.dialogFragment = this
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        isCancelable = niceDialog!!.niceDialogConfig.cancelable
        val parent = binding.root.parent
        if (parent is ViewGroup) {
            parent.removeAllViews()
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        confirmConfig()
    }

    override fun onDetach() {
        super.onDetach()
        if (!isStateSaved) {
            Log.i(javaClass.simpleName, "onDismiss callback")
            niceDialog?.dismissListener?.invoke(this)
        }
    }

    /**
     * bind
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(javaClass.simpleName, "onViewCreated")
        niceDialog?.binder?.invoke(this)
    }

    private fun confirmConfig() {
        dialog?.window?.setBackgroundDrawable(niceDialog?.niceDialogConfig?.backgroundDrawable)
        dialog?.window?.decorView?.setPadding(
            niceDialog?.niceDialogConfig?.paddingLeft?.let { it } ?: kotlin.run { 0 },
            niceDialog?.niceDialogConfig?.paddingTop?.let { it } ?: kotlin.run { 0 },
            niceDialog?.niceDialogConfig?.paddingRight?.let { it } ?: kotlin.run { 0 },
            niceDialog?.niceDialogConfig?.paddingBottom?.let { it } ?: kotlin.run { 0 }
        )
        val params = dialog?.window?.attributes
        params?.width = niceDialog?.niceDialogConfig?.width?.let { it }
            ?: kotlin.run { WindowManager.LayoutParams.MATCH_PARENT }
        params?.height = niceDialog?.niceDialogConfig?.height?.let { it }
            ?: kotlin.run { WindowManager.LayoutParams.MATCH_PARENT }
        params?.gravity =
            niceDialog?.niceDialogConfig?.gravity?.let { it } ?: kotlin.run { Gravity.BOTTOM }
        params?.alpha = niceDialog?.niceDialogConfig?.alpha?.let { it } ?: kotlin.run { 1f }
        if (niceDialog?.niceDialogConfig?.dimAmount?.let { it } ?: kotlin.run { 0.6f } in 0.0..1.0) {
            params?.dimAmount =
                niceDialog?.niceDialogConfig?.dimAmount?.let { it } ?: kotlin.run { 0.6f }
        }
        dialog?.window?.attributes = params
        if (niceDialog?.niceDialogConfig?.animatorStyleRes != 0) {
            niceDialog?.niceDialogConfig?.animatorStyleRes?.let {
                dialog?.window?.setWindowAnimations(it)
            }
        }
    }

    /**
     * 注意⚠️：如果是和activity生命周期相关的操作，需要放到NiceDialog.onDismiss或NiceDialogFactory.onDismiss里面去
     * @see NiceDialog.onDismiss
     * @see NiceDialogFactory.onDismiss
     */
    fun onDismiss(onDismiss: (NiceDialogFragment<T>) -> Unit) {
        val old = niceDialog?.dismissListener
        niceDialog?.dismissListener = {
            old?.invoke(this)
            onDismiss(this)
        }
    }

}