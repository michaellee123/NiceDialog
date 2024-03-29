package dog.abcd.nicedialog.demo

import android.content.Context
import android.view.Gravity
import android.view.WindowManager
import dog.abcd.nicedialog.NiceDialogConfig
import dog.abcd.nicedialog.NiceDialogFactory
import dog.abcd.nicedialog.NiceDialogFragment
import dog.abcd.nicedialog.demo.databinding.DialogAlertBinding

class AlertFactory(context: Context) : NiceDialogFactory<DialogAlertBinding, Int, Int>(context) {

    override fun config(): NiceDialogConfig.() -> Unit = {
        width = WindowManager.LayoutParams.WRAP_CONTENT
        height = WindowManager.LayoutParams.WRAP_CONTENT
        gravity = Gravity.CENTER
        animatorStyleRes = R.style.NiceDialog_Animation_Rotate
    }

    override fun binder(): (NiceDialogFragment<DialogAlertBinding>.() -> Unit) =
        {
            binding.button1.text = "1"
            binding.button2.text = "2"
            binding.button1.setOnClickListener {
                pd()
                next(1)
            }
            binding.button2.setOnClickListener {
                dismiss()
                next(2)
            }
        }

    fun pd(){
        dialog.dismiss()
    }

}