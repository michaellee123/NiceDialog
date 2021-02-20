package dog.abcd.nicedialog

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
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
abstract class NiceDialogFactory<T : ViewDataBinding, J, K>(
    private val context: Context
) : Serializable {

    /**
     * 回调
     */
    private var next: (NiceDialogFragment<T>.(J) -> Unit)? = null

    /**
     * 回调
     */
    private var finish: (NiceDialogFragment<T>.(K) -> Unit)? = null

    /**
     * 在show之后此对象会创建，即config或binder回调后会创建
     */
    lateinit var dialog: NiceDialogFragment<T>

    abstract fun config(): NiceDialogConfig.() -> Unit

    abstract fun binder(): (NiceDialogFragment<T>.() -> Unit)

    /**
     * 只会回调一次
     */
    fun onDismiss(): (NiceDialogFragment<T>) -> Unit = {}

    fun create(): NiceDialog<T> {
        return NiceDialog(reflectBinding(), this)
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

    /**
     * 反射实例化binding
     */
    private fun reflectBinding(): T {
        val clazz = getRealType()
        val inflateMethod = clazz.getMethod("inflate", LayoutInflater::class.java)
        return inflateMethod.invoke(
            null,
            if (context is Activity) context.layoutInflater else LayoutInflater.from(context)
        ) as T
    }

    /**
     * 使用反射技术得到T的真实类型
     */
    private fun getRealType(): Class<*> {
        // 获取当前new的对象的泛型的父类类型
        val genericSuperclass: Type = this.javaClass.genericSuperclass!! as ParameterizedType
        val pt = genericSuperclass as ParameterizedType
        // 获取第一个类型参数的真实类型
        return pt.actualTypeArguments[0] as Class<*>
    }

}