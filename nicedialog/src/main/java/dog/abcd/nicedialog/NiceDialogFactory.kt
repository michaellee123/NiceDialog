package dog.abcd.nicedialog

import androidx.databinding.ViewDataBinding

/**
 * 对于需要多次用到的dialog，或者是内部业务稍微复杂的dialog，比如说列表选择，则
 */
abstract class NiceDialogFactory<T : ViewDataBinding, J, K>(private val binding: T) {

    protected var next: ((J) -> Unit)? = null
    protected var finish: ((K) -> Unit)? = null

    abstract fun config(): NiceDialogConfig.() -> Unit

    abstract fun binder(): ((T, NiceDialogFragment<T>) -> Unit)

    fun create(): NiceDialog<T> {
        return NiceDialog(binding).config(config()).bind(binder())
    }

    fun onNext(next: ((J) -> Unit)): NiceDialogFactory<T, J, K> {
        this.next = next
        return this
    }

    fun onFinish(finish: ((K) -> Unit)): NiceDialogFactory<T, J, K> {
        this.finish = finish
        return this
    }

}