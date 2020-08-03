package dog.abcd.nicedialog

import android.util.Log
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentManager

/**
 * 弹弹弹，弹走鱼尾纹
 * 想要弹出什么，就直接把ViewDataBinding传进来就行了
 */
class NiceDialog<T : ViewDataBinding>(private val binding: T) {

    companion object {
        private const val TAG = "NiceDialog"

        private val dialogs = HashMap<String, NiceDialogFragment<*>>()

        /**
         * 手动dismiss，如果有按钮做了关闭操作的话，就不要调用这个方法
         */
        fun dismiss(tag: String) {
            Log.i(TAG, "dismiss dialog:$tag")
            dialogs.remove(tag)?.dismiss()
        }

        /**
         * 在NiceDialogFragment被调用dismiss之后会移除这个弹窗，正常情况下请不要调用
         */
        fun removeDialog(tag: String): NiceDialogFragment<*>? {
            Log.i(TAG, "remove dialog:$tag")
            return dialogs.remove(tag)
        }
    }

    private val niceDialogConfig = NiceDialogConfig()

    private var binder: (NiceDialogFragment<T>.() -> Unit)? = null

    /**
     * 弹窗配置，可以多次设置，属性以最后一次设置的为准
     */
    fun config(set: NiceDialogConfig.() -> Unit): NiceDialog<T> {
        set(niceDialogConfig)
        return this
    }

    /**
     * 绑定数据，可以多次操作，最后的操作会覆盖之前的操作
     */
    fun bind(binder: NiceDialogFragment<T>.() -> Unit): NiceDialog<T> {
        val old = this.binder
        this.binder = {
            old?.let { o ->
                o(this)
            }
            binder(this)
        }
        return this
    }

    /**
     * 显示，如果需要主动触发关闭，可以选择在这里获取到并保存对象，用于操作
     */
    fun show(manager: FragmentManager, tag: String?): NiceDialogFragment<T> {
        tag?.let { dismiss(it) }
        val dialogFragment = NiceDialogFragment<T>()
        dialogFragment.show(manager, tag, binding, niceDialogConfig, binder)
        tag?.let { dialogs.put(it, dialogFragment) }
        return dialogFragment
    }
}