package dog.abcd.nicedialog

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * 对于需要多次用到的dialog，或者是内部业务稍微复杂的dialog，比如说列表选择，则
 */
abstract class NiceDialogFactory<T : ViewDataBinding, J, K>(private val context: Context) {

    protected var next: ((J) -> Unit)? = null
    protected var finish: ((K) -> Unit)? = null

    abstract fun config(): NiceDialogConfig.() -> Unit

    abstract fun binder(): ((T, NiceDialogFragment<T>) -> Unit)

    fun create(): NiceDialog<T> {
        return NiceDialog<T>(getBinding()).config(config()).bind(binder())
    }

    fun onNext(next: ((J) -> Unit)): NiceDialogFactory<T, J, K> {
        this.next = next
        return this
    }

    fun onFinish(finish: ((K) -> Unit)): NiceDialogFactory<T, J, K> {
        this.finish = finish
        return this
    }

    private fun getBinding(): T {
        val clazz = getRealType()
        val inflateMethod = clazz.getMethod("inflate", LayoutInflater::class.java)
        return inflateMethod.invoke(
            null,
            if (context is Activity) context.layoutInflater else LayoutInflater.from(context)
        ) as T
    }

    // 使用反射技术得到T的真实类型
    private fun getRealType(): Class<*> {
        // 获取当前new的对象的泛型的父类类型
        val genericSuperclass: Type = this.javaClass.genericSuperclass!! as ParameterizedType
        val pt = genericSuperclass as ParameterizedType
        // 获取第一个类型参数的真实类型
        return pt.actualTypeArguments[0] as Class<*>
    }

}