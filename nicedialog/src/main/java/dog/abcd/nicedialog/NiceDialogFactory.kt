package dog.abcd.nicedialog

import android.content.Context
import android.os.Bundle
import androidx.viewbinding.ViewBinding
import java.io.Serializable
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * 对于需要多次用到的dialog，或者是内部业务稍微复杂的dialog，比如说列表选择，则继承此类进行封装
 * 注意⚠️：回调中使用Activity、Context等对象时，优先从回调中的this去尝试获取，具体原因
 * @see NiceDialog
 *
 * @author Michael Lee
 */
abstract class NiceDialogFactory<T : ViewBinding, J, K>() :
    Serializable {

    /**
     * 回调
     */
    @Transient
    private var next: (NiceDialogFragment<T>.(J) -> Unit)? = null

    /**
     * 回调
     */
    @Transient
    private var finish: (NiceDialogFragment<T>.(K) -> Unit)? = null

    /**
     * 在show之后此对象会创建，即config或binder回调后会创建
     */
    @Transient
    lateinit var dialog: NiceDialogFragment<T>

    abstract fun config(): NiceDialogConfig.() -> Unit

    abstract fun binder(): (NiceDialogFragment<T>.() -> Unit)

    /**
     * 只会回调一次
     */
    fun onDismiss(): (NiceDialogFragment<T>) -> Unit = {}

    fun onSaveInstanceState(): NiceDialogFragment<T>.(Bundle) -> Unit = {}

    fun create(): NiceDialog<T> {
        return NiceDialog(getBindingRealType()!!, this)
    }

    /**
     * 从调用处进行注册
     */
    fun onNext(next: (NiceDialogFragment<T>.(J) -> Unit)): NiceDialogFactory<T, J, K> {
        this.next = next
        return this
    }

    /**
     * 从调用处进行注册
     */
    fun onFinish(finish: (NiceDialogFragment<T>.(K) -> Unit)): NiceDialogFactory<T, J, K> {
        this.finish = finish
        return this
    }

    /**
     * 从封装的Factory中调用
     */
    fun next(result: J) {
        next?.invoke(dialog, result)
    }

    /**
     * 从封装的Factory中调用
     */
    fun finish(result: K) {
        finish?.invoke(dialog, result)
    }

    private fun getBindingRealType(): Class<T>? {
        val genericSuperclass: Type = this.javaClass.genericSuperclass!! as ParameterizedType
        val pt = genericSuperclass as ParameterizedType
        var clazz: Class<T>? = null
        for (type in pt.actualTypeArguments) {
            val cls = type as Class<T>
            if (interfaceContainsViewBinding(cls)) {
                clazz = cls
            }
        }
        return clazz
    }

    private fun interfaceContainsViewBinding(clazz: Class<*>): Boolean {
        val result = clazz.interfaces.contains(ViewBinding::class.java)
        return clazz.superclass?.let {
            interfaceContainsViewBinding(it) or result
        } ?: kotlin.run {
            result
        }
    }


}