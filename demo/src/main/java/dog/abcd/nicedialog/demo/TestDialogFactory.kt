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
    NiceDialogFactory<DialogNiceBinding, String, String>(context) {

    override fun config(): NiceDialogConfig.() -> Unit = {
        width = WindowManager.LayoutParams.MATCH_PARENT
        height = WindowManager.LayoutParams.WRAP_CONTENT
        gravity = Gravity.BOTTOM
    }

    override fun binder(): NiceDialogFragment<DialogNiceBinding>.() -> Unit =
        {
            binding.tvMessage.text = "Nice Factory!"
            binding.btnConfirm.text = "Confirm"
            binding.btnCancel.text = "Cancel"
            binding.btnConfirm.setOnClickListener {
                dismiss()
                next("Next!")
            }
            binding.btnCancel.setOnClickListener {
                dismiss()
                finish("Finish!")
            }
        }

}