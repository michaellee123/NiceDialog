package dog.abcd.nicedialog

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import java.io.Serializable

/**
 * 弹弹弹，弹走鱼尾纹
 * 想要弹出什么，就直接把ViewDataBinding传进来就行了
 * ⚠️注意：
 * 如果需要在回调中用到生命周期相关的对象，例如Activity，Context或者一些Activity中的控件，
 * 一定不要直接用this@Activity这种写法，需要用回调中给过去的NiceDialogFragment.getActivity()进行操作。
 * 具体问题复现可以尝试：修改手机显示比例、允许屏幕旋转等
 *
 * @see NiceDialogFragment.getActivity
 * @author Michael Lee
 */
open class NiceDialog<T : ViewBinding>(
    val clazz: Class<T>,
    val niceDialogFactory: NiceDialogFactory<T, *, *>? = null
) : Serializable {

    companion object {

        private const val TAG = "NiceDialog"

        private val dialogs = HashMap<String, NiceDialogFragment<*>>()

        /**
         * 手动dismiss，如果有按钮做了关闭操作的话，就不要调用这个方法
         */
        @Synchronized
        fun dismiss(tag: String) {
            Log.i(TAG, "dismiss dialog:$tag")
            try {
                dialogs[tag]?.dismissAllowingStateLoss()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        /**
         * 非特殊情况，请不要主动调用这个方法
         * */
        @Synchronized
        fun removeDialog(vararg tags: String) {
            tags.forEach { tag ->
                Log.i(TAG, "remove dialog:$tag")
                dialogs.remove(tag)
            }
        }

        fun getDialog(tag: String): NiceDialogFragment<*>? = dialogs[tag]

    }

    /**
     * 弹窗本体
     */
    @Transient
    var dialogFragment: NiceDialogFragment<T>? = null
        set(value) {
            field = value
            field?.let {
                niceDialogFactory?.dialog = it
            }
        }

    /**
     * 弹窗配置
     */
    val niceDialogConfig = NiceDialogConfig()

    init {
        //从Factory中获取配置
        niceDialogFactory?.config()?.invoke(niceDialogConfig)
    }

    /**
     * 数据绑定，或事件绑定，可以在这里面操作控件相关的所有事情，
     * 注意⚠️：这里lambda写法的this代表的是NiceDialogFragment，如果在activity中使用kotlin自带的控件，则需要加上作用域，
     * 例如：activity.btnLogin.setxxx
     * 再注意⚠️：activity使用回调中等this去获取
     * @see NiceDialogFragment.getActivity
     */
    @Transient
    var binder: (NiceDialogFragment<T>.() -> Unit) = {
        niceDialogFactory?.binder()?.invoke(this)
    }

    /**
     * dismiss回调，注意事项同上
     */
    @Transient
    var dismissListener: (NiceDialogFragment<T>.() -> Unit) = {
        niceDialogFactory?.onDismiss()?.invoke(this)
    }

    @Transient
    var onSaveInstanceStateListener: NiceDialogFragment<T>.(Bundle) -> Unit = {
        niceDialogFactory?.onSaveInstanceState()?.invoke(this, it)
    }

    /**
     * 弹窗配置，可以多次设置，属性以最后一次设置的为准
     */
    fun config(setter: NiceDialogConfig.() -> Unit): NiceDialog<T> {
        setter(niceDialogConfig)
        return this
    }

    /**
     * 绑定数据，可以多次操作，最后的操作会覆盖之前的操作
     * @see binder
     */
    fun bind(binder: NiceDialogFragment<T>.() -> Unit): NiceDialog<T> {
        val old = this.binder
        this.binder = {
            old(this)
            binder(this)
        }
        return this
    }

    /**
     * dismiss监听，可以多次设置，会先回调先设置的
     * @see dismissListener
     */
    fun onDismiss(listener: NiceDialogFragment<T>.() -> Unit): NiceDialog<T> {
        val old = this.dismissListener
        this.dismissListener = {
            old(this)
            listener(this)
        }
        return this
    }

    fun onSaveInstanceState(listener: NiceDialogFragment<T>.(Bundle) -> Unit): NiceDialog<T> {
        val old = this.onSaveInstanceStateListener
        this.onSaveInstanceStateListener = {
            old(it)
            listener(it)
        }
        return this
    }

    /**
     * 在单次使用时，manager传activity中的就行。在嵌套调用时，manager传回调中对象获取的。
     * tag必传，多个相同tag的弹窗只会显示最后一个，之前的会全部被自动dismiss
     * @see AppCompatActivity.getSupportFragmentManager
     * @see NiceDialogFragment.getFragmentManager
     */
    @Synchronized
    fun show(manager: FragmentManager, tag: String): NiceDialogFragment<T> {
        if (tag.isNullOrEmpty()) {
            throw IllegalArgumentException("tag must not be null")
        }
        dismiss(tag)
        dialogFragment = NiceDialogFragment.create(this)
        dialogs[tag] = dialogFragment!!
        if (manager.isStateSaved) {
            return dialogFragment!!
        }
        dialogFragment!!.showNow(manager, tag)
        return dialogFragment!!
    }

}