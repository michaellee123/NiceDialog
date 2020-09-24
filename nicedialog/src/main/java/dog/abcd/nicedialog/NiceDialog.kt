package dog.abcd.nicedialog

import android.util.Log
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentManager

/**
 * 弹弹弹，弹走鱼尾纹
 * 想要弹出什么，就直接把ViewDataBinding传进来就行了
 * ⚠️注意：
 * 如果直接使用，就一定不要在bind中做和activity生命周期相关的操作，
 * 例如：对activity中对任何控件做任何操作，或引用activity、context等
 * ⚠️如果需要做这些操作，则需要使用NiceDialog.create方法进行创建
 * @see NiceDialog.create
 * @see NiceDialog.createFactory
 * @author Michael Lee
 */
class NiceDialog<T : ViewDataBinding>(var binding: T) {

    companion object {
        private const val TAG = "NiceDialog"

        private val niceDialogFactories = HashMap<String, NiceDialogFactory<*, *, *>>()

        private val niceDialogs = HashMap<String, NiceDialog<*>>()

        /**
         * 在activity的onResume中通过此方法创建NiceDialog并绑定回调监听，
         * 可以在屏幕旋转或其他后activity重建后正确回调,
         * 如果回调中不使用和activity生命周期相关的操作，则可以直接使用构造函数
         */
        @Synchronized
        fun <T : ViewDataBinding> create(
            binding: T,
            manager: FragmentManager?,
            tag: String?,
            niceDialogFactory: NiceDialogFactory<T, *, *>? = null
        ): NiceDialog<T> {
            var niceDialog = niceDialogs[tag]?.let {
                it as NiceDialog<T>
            } ?: kotlin.run {
                val newer = NiceDialog(binding)
                tag?.let {
                    niceDialogs[it] = newer
                }
                newer
            }
            niceDialog.tag = tag
            niceDialog.manager = manager
            niceDialog.niceDialogFactory = niceDialogFactory
            return niceDialog
        }

        /**
         * 在activity的onResume中通过此方法创建Factory并绑定回调监听，
         * 可以在屏幕旋转或其他后activity重建后正确回调
         */
        @Synchronized
        fun <T : NiceDialogFactory<*, *, *>> createFactory(
            creator: () -> T,
            manager: FragmentManager,
            tag: String
        ): T {
            val factory = niceDialogFactories[tag]?.let {
                Log.i(TAG, "get dialog factory:$tag")
                it as T
            } ?: kotlin.run {
                Log.i(TAG, "create dialog factory:$tag")
                val newer = creator()
                newer.tag = tag
                niceDialogFactories[tag] = newer
                Log.i(TAG, "add dialog:$tag")
                newer
            }
            factory.manager = manager
            return factory
        }

        /**
         * 获取弹窗内容（主要是为了让屏幕旋转的时候显示正确）
         */
        @Synchronized
        fun <T : ViewDataBinding> getNiceDialog(tag: String): NiceDialog<T>? {
            val nd = niceDialogs[tag]
            return try {
                nd as NiceDialog<T>
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        /**
         * 手动dismiss，如果有按钮做了关闭操作的话，就不要调用这个方法
         */
        @Synchronized
        fun dismiss(tag: String) {
            Log.i(TAG, "dismiss dialog:$tag")
            try {
                niceDialogs[tag]?.dialogFragment?.dismissAllowingStateLoss()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        /**
         * 因为需要保存状态，所以NiceDialog不再自动清空map中的数据，
         * 需要在activity关闭的时候调用这个方法清空，需要判断isFinishing
         * */
        @Synchronized
        fun removeDialog(vararg tags: String) {
            tags.forEach { tag ->
                Log.i(TAG, "remove dialog:$tag")
                niceDialogs.remove(tag)
                niceDialogFactories.remove(tag)
            }
        }

    }

    /**
     * 全局唯一标识，只能使用一种方法传入，如果用create方法传入之后，在调用show的时候就不要传，反之亦然
     * @see show
     * @see create
     */
    var tag: String? = null

    /**
     * 在多次嵌套调用时注意⚠️：如果activity重建，直接在回调中调用的supportFragmentManager会被标记位isStateSaved，
     * 此时fragmentManager会不可用，如果有这种情况，则需要在onResume中通过create方法进行创建，保证manager是最新可用的那一个
     * @see create
     */
    var manager: FragmentManager? = null

    /**
     * 通过继承NiceDialogFactory创建
     * @see NiceDialogFactory
     */
    var niceDialogFactory: NiceDialogFactory<T, *, *>? = null
        set(value) {
            field = value
            niceDialogFactory?.config()?.invoke(niceDialogConfig)
        }

    /**
     * 弹窗本体
     */
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

    /**
     * 数据绑定，或事件绑定，可以在这里面操作控件相关的所有事情，
     * 注意⚠️：这里lambda写法的this代表的是NiceDialogFragment，如果在activity中使用kotlin自带的控件，则需要加上作用域，
     * 例如：this@MainActivity.btnLogin.setxxx
     * 再注意⚠️：如果在这里面有操作到和activity生命周期相关的内容，则需要使用create方法进行创建，否则会出现不可预计的错误，或回调失效
     * @see create
     */
    var binder: (NiceDialogFragment<T>.() -> Unit) = {
        niceDialogFactory?.binder()?.invoke(this)
    }

    /**
     *
     */
    var dismissListener: ((NiceDialogFragment<T>) -> Unit) = {
        niceDialogFactory?.onDismiss()?.invoke(it)
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
    fun onDismiss(listener: (NiceDialogFragment<T>) -> Unit): NiceDialog<T> {
        val old = this.dismissListener
        this.dismissListener = {
            old(it)
            listener(it)
        }
        return this
    }

    /**
     * 显示，如果需要主动触发关闭，可以选择在这里获取到并保存对象，用于操作，也可以用tag进行操作，如果不是使用create或createFactory，
     * 则需要使用带参数的show方法
     * @see create
     * @see createFactory
     */
    @Synchronized
    fun show(): NiceDialogFragment<T> {
        if (tag == null) {
            throw IllegalArgumentException("tag must not be null")
        }
        if (manager == null) {
            throw IllegalArgumentException("fragment manager must not be null")
        }
        dismiss(tag!!)
        niceDialogs[tag!!] = this
        niceDialogFactory?.let {
            niceDialogFactories[tag!!] = it
        }
        dialogFragment = NiceDialogFragment()
        if (manager!!.isStateSaved) {
            return dialogFragment!!
        }
        dialogFragment!!.showNow(manager!!, tag!!)
        return dialogFragment!!
    }

    /**
     * 如果直接使用构造函数，则使用此方法，如果使用的create或createFactory，则使用不带参的show方法
     * @see create
     * @see createFactory
     */
    @Synchronized
    fun show(manager: FragmentManager, tag: String): NiceDialogFragment<T> {
        if (this.tag != null) {
            throw IllegalArgumentException("don't reset tag from this function")
        }
        if (this.manager != null) {
            throw IllegalArgumentException("don't reset fragment manager from this function")
        }
        this.tag = tag
        this.manager = manager
        return show()
    }

}