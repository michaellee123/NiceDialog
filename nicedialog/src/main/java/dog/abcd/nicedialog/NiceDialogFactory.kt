package dog.abcd.nicedialog

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentManager
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * 对于需要多次用到的dialog，或者是内部业务稍微复杂的dialog，比如说列表选择，则继承此类进行封装
 * 注意⚠️：如果回调中涉及和生命周期相关的操作，则需要使用NiceDialog.createFactory创建对象，如果你
 * 区分不了是否需要使用NiceDialog.createFactory方法，则推荐使用createFactory，多写一点点代码能够提升可靠性，也是值得的
 * @see NiceDialog.createFactory
 * @author Michael Lee
 */
abstract class NiceDialogFactory<T : ViewDataBinding, J, K>(
    private val context: Context
) {

    /**
     * 唯一标识，通过NiceDialog.createFactory创建时传入
     * @see NiceDialog.createFactory
     */
    var tag: String? = null

    /**
     * 在多次嵌套调用时注意⚠️：如果activity重建，直接在回调中调用的supportFragmentManager会被标记位isStateSaved，
     * 此时fragmentManager会不可用，如果有这种情况，则需要在onResume中通过createFactory方法进行创建，保证manager是最新可用的那一个
     * @see NiceDialog.createFactory
     */
    var manager: FragmentManager? = null

    /**
     * 回调
     */
    protected var next: ((J) -> Unit)? = null

    /**
     * 回调
     */
    protected var finish: ((K) -> Unit)? = null

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
        return NiceDialog.create(reflectBinding(), manager, tag, this)
    }

    fun onNext(next: ((J) -> Unit)): NiceDialogFactory<T, J, K> {
        this.next = next
        return this
    }

    fun onFinish(finish: ((K) -> Unit)): NiceDialogFactory<T, J, K> {
        this.finish = finish
        return this
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