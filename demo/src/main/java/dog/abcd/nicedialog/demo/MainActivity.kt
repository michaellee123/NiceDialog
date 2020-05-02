package dog.abcd.nicedialog.demo

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Toast
import com.gyf.immersionbar.ktx.immersionBar
import dog.abcd.nicedialog.NiceDialog
import dog.abcd.nicedialog.demo.databinding.DialogAlertBinding
import dog.abcd.nicedialog.demo.databinding.DialogNiceBinding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        immersionBar {
            fitsSystemWindows(true)
            statusBarColor(R.color.white)
            statusBarDarkFont(true)
        }
        setContentView(R.layout.activity_main)
//        btnNormal.setOnClickListener {
//            NiceDialog<DialogNiceBinding>(DialogNiceBinding.inflate(LayoutInflater.from(this)))
//                .config {
//                    width = WindowManager.LayoutParams.MATCH_PARENT
//                    height = WindowManager.LayoutParams.WRAP_CONTENT
//                    paddingLeft = 48
//                    paddingRight = 48
//                    paddingBottom = 48
//                    gravity = Gravity.BOTTOM
//                    animatorStyleRes = R.style.NiceDialog_Animation_SlideBottom
//                }.bind { binding, dialog ->
//                    binding.tvMessage.text = "Nice Dialog!"
//                    binding.btnConfirm.text = "Nice!"
//                    binding.btnConfirm.setOnClickListener {
//                        dialog.dismiss()
//                    }
//                }.show(supportFragmentManager, "normal")
//        }
        btnNormal.setOnClickListener {
            NiceDialog<DialogNiceBinding>(DialogNiceBinding.inflate(LayoutInflater.from(this)))
                .config {
                    width = WindowManager.LayoutParams.MATCH_PARENT
                    height = WindowManager.LayoutParams.WRAP_CONTENT
                    gravity = Gravity.BOTTOM
                    backgroundDrawable = ColorDrawable(0x00000000)
                    paddingTop = 0
                    paddingBottom = 48
                    paddingLeft = 48
                    paddingRight = 48
                    cancelable = true
                    animatorStyleRes = R.style.NiceDialog_Animation_SlideBottom
                }.bind { binding, dialog ->
                    binding.tvMessage.text = "Nice Dialog!"
                    binding.btnConfirm.text = "Cool!"
                    binding.btnConfirm.setOnClickListener {
                        dialog.dismiss()
                    }
                }.show(supportFragmentManager, "tag")


//            AlertFactory(this).onNext {
//                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
//            }.create().bind { binding, dialog ->
//                binding.button1.text = "BUTTON1"
//            }.config {
//                backgroundDrawable = ColorDrawable(resources.getColor(R.color.colorPrimary))
//                gravity = Gravity.BOTTOM
//                animatorStyleRes = R.style.NiceDialog_Animation_SlideBottom
//            }.show(supportFragmentManager, "demo")
        }

//        btnSimple.setOnClickListener {
//        CircleDialogFactory(this).create().show(supportFragmentManager, "circle").onDismiss {
//            btnNormal.callOnClick()
//        }
//            Handler().postDelayed(
//                {
//                    NiceDialog.dismiss("circle")
//                }, 3000
//            )
//        }

        btnSimple.setOnClickListener {
            TestDialogFactory(this)
                .onNext {
                    Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                }
                .onFinish {
                    Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                }
                .create()
                .config {
                    animatorStyleRes = R.style.NiceDialog_Animation_Zoom
                }
                .bind { binding, _ ->
                    binding.tvMessage.text = "${binding.tvMessage.text}!!"
                }
                .show(supportFragmentManager, "tag")
        }

        btnDifficult.setOnClickListener {
            TestDialogFactory(this)
                .onNext {
                    //这个是TestDialogFactory中传递回来的参数
                    //紧接着再弹一个弹窗
                    TestDialogFactory(this).create().bind { binding, dialog ->
                        //利用immersionBar修改状态栏显示
                        dialog.immersionBar {
                            fitsSystemWindows(false)
                            statusBarDarkFont(true)
                        }
                        binding.tvMessage.text = it
                        binding.btnConfirm.text = "Nice!"
                        binding.btnConfirm.setOnClickListener {
                            dialog.dismiss()
                            immersionBar {
                                fitsSystemWindows(true)
                                statusBarColor(R.color.white)
                                statusBarDarkFont(true)
                            }
                            //再调用一下圆圈弹窗
                            btnSimple.callOnClick()
                        }
                    }.show(supportFragmentManager, javaClass.simpleName)
                }.onFinish {
                    //取消的回调
                    Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                }.create().config {
                    //多次链式的调用修改config
                    height = WindowManager.LayoutParams.WRAP_CONTENT
                }.config {
                    width = WindowManager.LayoutParams.WRAP_CONTENT
                }.bind { binding, _ ->
                    //多次绑定view属性
                    binding.btnConfirm.text = "Second!"
                }.bind { binding, _ ->
                    binding.btnConfirm.text = "Third!"
                }.show(supportFragmentManager, javaClass.simpleName)
        }

    }

}
