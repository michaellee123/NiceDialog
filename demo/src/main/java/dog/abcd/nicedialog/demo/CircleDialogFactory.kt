package dog.abcd.nicedialog.demo

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import dog.abcd.nicedialog.NiceDialogConfig
import dog.abcd.nicedialog.NiceDialogFactory
import dog.abcd.nicedialog.NiceDialogFragment
import dog.abcd.nicedialog.demo.databinding.DialogCircleBinding

class CircleDialogFactory(context: Context) :
    NiceDialogFactory<DialogCircleBinding, Unit, Unit>(context) {
    override fun config(): NiceDialogConfig.() -> Unit = {
        width = WindowManager.LayoutParams.WRAP_CONTENT
        height = WindowManager.LayoutParams.WRAP_CONTENT
        gravity = Gravity.CENTER
        cancelable = false
    }

    override fun binder(): NiceDialogFragment<DialogCircleBinding>.() -> Unit = {

    }

}