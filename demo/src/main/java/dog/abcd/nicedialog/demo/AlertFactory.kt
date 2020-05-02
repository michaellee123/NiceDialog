package dog.abcd.nicedialog.demo

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import dog.abcd.nicedialog.NiceDialogConfig
import dog.abcd.nicedialog.NiceDialogFactory
import dog.abcd.nicedialog.NiceDialogFragment
import dog.abcd.nicedialog.demo.databinding.DialogAlertBinding

class AlertFactory(context: Context) : NiceDialogFactory<DialogAlertBinding, Int, Int>(
    DialogAlertBinding.inflate(
        LayoutInflater.from(context)
    )
) {
    override fun config(): NiceDialogConfig.() -> Unit = {
        width = WindowManager.LayoutParams.WRAP_CONTENT
        height = WindowManager.LayoutParams.WRAP_CONTENT
        gravity = Gravity.CENTER
        animatorStyleRes = R.style.NiceDialog_Animation_Rotate
    }

    override fun binder(): (DialogAlertBinding, NiceDialogFragment<DialogAlertBinding>) -> Unit =
        { binding, dialog ->
            binding.button1.text = "1"
            binding.button2.text = "2"
            binding.button1.setOnClickListener {
                dialog.dismiss()
                next?.invoke(1)
            }
            binding.button2.setOnClickListener {
                dialog.dismiss()
                next?.invoke(2)
            }
        }

    //adapter
}