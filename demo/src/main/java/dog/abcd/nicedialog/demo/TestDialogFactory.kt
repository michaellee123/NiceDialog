package dog.abcd.nicedialog.demo

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import dog.abcd.nicedialog.NiceDialogConfig
import dog.abcd.nicedialog.NiceDialogFactory
import dog.abcd.nicedialog.NiceDialogFragment
import dog.abcd.nicedialog.demo.databinding.DialogNiceBinding

class TestDialogFactory(context: Context) :
    NiceDialogFactory<DialogNiceBinding, String, String>(
        DialogNiceBinding.inflate(LayoutInflater.from(context))
    ) {

    override fun config(): NiceDialogConfig.() -> Unit = {
        width = WindowManager.LayoutParams.MATCH_PARENT
        gravity = Gravity.BOTTOM
    }

    override fun binder(): (DialogNiceBinding, NiceDialogFragment<DialogNiceBinding>) -> Unit =
        { binding, dialog ->
            binding.tvMessage.text = "Nice Factory!"
            binding.btnConfirm.setOnClickListener {
                dialog.dismiss()
                next?.invoke("Next!")
            }
            binding.tvMessage.setOnClickListener {
                dialog.dismiss()
                finish?.invoke("Finish!")
            }
        }

}