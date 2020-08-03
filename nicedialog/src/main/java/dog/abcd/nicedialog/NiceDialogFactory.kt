package dog.abcd.nicedialog

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * 对于需要多次用到的dialog，或者是内部业务稍微复杂的dialog，比如说列表选择，则继承此类进行封装
 */
abstract class NiceDialogFactory<T : ViewDataBinding, J, K>(private val context: Context) {

    protected var next: ((J) -> Unit)? = null
    protected var finish: ((K) -> Unit)? = null

    protected lateinit var dialog: NiceDialogFragment<T>

    abstract fun config(): NiceDialogConfig.() -> Unit

    abstract fun binder(): (NiceDialogFragment<T>.() -> Unit)

    fun create(): NiceDialog<T> {
        return NiceDialog(reflectBinding()).config(config()).bind {
            this@NiceDialogFactory.dialog = this
            binder()(this)
        }
    }

    fun onNext(next: ((J) -> Unit)): NiceDialogFactory<T, J, K> {
        this.next = next
        return this
    }

    fun onFinish(finish: ((K) -> Unit)): NiceDialogFactory<T, J, K> {
        this.finish = finish
        return this
    }

    private fun reflectBinding(): T {
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