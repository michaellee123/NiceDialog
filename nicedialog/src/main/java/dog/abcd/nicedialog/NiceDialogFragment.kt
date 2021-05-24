package dog.abcd.nicedialog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding

class NiceDialogFragment<T : ViewBinding>() : DialogFragment() {

    constructor(niceDialog: NiceDialog<T>) : this() {
        this.niceDialog = niceDialog
    }

    lateinit var binding: T

    lateinit var niceDialog: NiceDialog<T>

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("niceDialog", niceDialog)
    }

    /**
     * 根据tag从NiceDialog取实例
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!this::niceDialog.isInitialized && savedInstanceState != null) {
            niceDialog = savedInstanceState.getSerializable("niceDialog") as NiceDialog<T>
        }

        binding = niceDialog.binding
        niceDialog.dialogFragment = this
        isCancelable = niceDialog.niceDialogConfig.cancelable
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

    /**
     * 从这里回调dismiss会更准确，在屏幕旋转的时候不会回调
     */
    override fun onDetach() {
        super.onDetach()
        if (!isStateSaved) {
            Log.i(javaClass.simpleName, "onDismiss callback")
            niceDialog.dismissListener.invoke(this)
        }
    }

    /**
     * bind
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(javaClass.simpleName, "onViewCreated")
        niceDialog.binder.invoke(this)
    }

    /**
     * remove tag
     */
    override fun onDestroyView() {
        super.onDestroyView()
        tag?.let {
            if (NiceDialog.getDialog(it) === this) {
                NiceDialog.removeDialog(it)
            }
        }
    }

    private fun confirmConfig() {
        dialog?.window?.setBackgroundDrawable(niceDialog.niceDialogConfig.backgroundDrawable)
        dialog?.window?.decorView?.setPadding(
            niceDialog.niceDialogConfig.paddingLeft,
            niceDialog.niceDialogConfig.paddingTop,
            niceDialog.niceDialogConfig.paddingRight,
            niceDialog.niceDialogConfig.paddingBottom
        )
        val params = dialog?.window?.attributes
        params?.width = niceDialog.niceDialogConfig.width
        params?.height = niceDialog.niceDialogConfig.height
        params?.gravity =
            niceDialog.niceDialogConfig.gravity
        params?.alpha = niceDialog.niceDialogConfig.alpha
        if (niceDialog.niceDialogConfig.dimAmount in 0.0..1.0) {
            params?.dimAmount =
                niceDialog.niceDialogConfig.dimAmount
        }
        dialog?.window?.attributes = params
        if (niceDialog.niceDialogConfig.animatorStyleRes != 0) {
            dialog?.window?.setWindowAnimations(niceDialog.niceDialogConfig.animatorStyleRes)
        }
    }

    /**
     * @see NiceDialog.onDismiss
     * @see NiceDialogFactory.onDismiss
     */
    fun onDismiss(onDismiss: NiceDialogFragment<T>.() -> Unit) {
        val old = niceDialog.dismissListener
        niceDialog.dismissListener = {
            old.invoke(this)
            onDismiss(this)
        }
    }

}