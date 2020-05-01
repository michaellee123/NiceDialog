package dog.abcd.kotlindemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.gyf.immersionbar.ktx.immersionBar

class NiceDialogFragment<T : ViewDataBinding> : DialogFragment() {

    private lateinit var niceDialogView: NiceDialog<T>

    private lateinit var niceDialogConfig: NiceDialogConfig

    private var binder: ((T, NiceDialogFragment<T>) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return niceDialogView.binding.root
    }

    override fun onResume() {
        super.onResume()
        immersionBar {
            fitsSystemWindows(niceDialogConfig.fitsSystemWindows)
            /**
             * 这一句设置颜色没有用，现在两种解决方式：
             * 1、根布局加背景色，fitsSystemWindows设置为false，自己在布局判断状态栏高度
             * 2、设置dialog.window的背景，fitsSystemWindows设置为true
             *
             * statusBarColor(R.color.colorPrimary)
             */
            statusBarDarkFont(niceDialogConfig.statusBarDarkFont)
        }
        dialog?.window?.setBackgroundDrawable(niceDialogConfig.backgroundDrawable)
        dialog?.window?.decorView?.setPadding(
            niceDialogConfig.paddingLeft,
            niceDialogConfig.paddingTop,
            niceDialogConfig.paddingRight,
            niceDialogConfig.paddingBottom
        )
        val params: WindowManager.LayoutParams = dialog!!.window!!.attributes
        params.width = niceDialogConfig.width
        params.height = niceDialogConfig.height
        params.gravity = niceDialogConfig.gravity
        dialog!!.window!!.attributes = params
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binder?.let {
            it(niceDialogView.binding, this)
        }
    }

    fun show(
        manager: FragmentManager,
        tag: String?,
        niceDialogView: NiceDialog<T>,
        niceDialogConfig: NiceDialogConfig,
        binder: ((binding: T, dialog: NiceDialogFragment<T>) -> Unit)?
    ) {
        this.niceDialogView = niceDialogView
        this.niceDialogConfig = niceDialogConfig
        this.binder = binder
        isCancelable = niceDialogConfig.cancelable
        super.show(manager, tag)
    }

}