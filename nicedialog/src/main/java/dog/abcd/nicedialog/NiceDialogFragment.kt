package dog.abcd.nicedialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

class NiceDialogFragment<T : ViewDataBinding> : DialogFragment() {

    lateinit var binding: T

    private lateinit var niceDialogConfig: NiceDialogConfig

    private var dismissListener: (() -> Unit)? = null

    private var binder: (NiceDialogFragment<T>.() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val parent = binding.root.parent
        if (parent is ViewGroup) {
            parent.removeAllViews()
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        confirmConfig()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tag?.let { NiceDialog.removeDialog(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binder?.let {
            it(this)
        }
    }

    fun show(
        manager: FragmentManager,
        tag: String?,
        binding: T,
        niceDialogConfig: NiceDialogConfig,
        binder: (NiceDialogFragment<T>.() -> Unit)?
    ) {
        this.binding = binding
        this.niceDialogConfig = niceDialogConfig
        this.binder = binder
        isCancelable = niceDialogConfig.cancelable
        super.show(manager, tag)
    }

    private fun confirmConfig() {
        dialog?.window?.setBackgroundDrawable(niceDialogConfig.backgroundDrawable)
        dialog?.window?.decorView?.setPadding(
            niceDialogConfig.paddingLeft,
            niceDialogConfig.paddingTop,
            niceDialogConfig.paddingRight,
            niceDialogConfig.paddingBottom
        )
        val params = dialog?.window?.attributes
        params?.width = niceDialogConfig.width
        params?.height = niceDialogConfig.height
        params?.gravity = niceDialogConfig.gravity
        dialog?.window?.attributes = params
        if (niceDialogConfig.animatorStyleRes != 0) {
            dialog?.window?.setWindowAnimations(niceDialogConfig.animatorStyleRes)
        }
    }

    fun onDismiss(onDismiss: () -> Unit) {
        this.dismissListener = onDismiss
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismissListener?.invoke()
    }
}