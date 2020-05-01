package dog.abcd.kotlindemo

import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentManager

class NiceDialog<T : ViewDataBinding>(val binding: T) {

    private val niceDialogConfig = NiceDialogConfig()
    private var binder: ((T, NiceDialogFragment<T>) -> Unit)? = null

    fun config(set: (NiceDialogConfig) -> Unit): NiceDialog<T> {
        set(niceDialogConfig)
        return this
    }

    fun bind(binder: (binding: T, dialog: NiceDialogFragment<T>) -> Unit): NiceDialog<T> {
        this.binder = binder
        return this
    }

    fun show(manager: FragmentManager, tag: String?): NiceDialogFragment<T> {
        val dialogFragment = NiceDialogFragment<T>()
        dialogFragment.show(manager, tag, this, niceDialogConfig, binder)
        return dialogFragment
    }
}