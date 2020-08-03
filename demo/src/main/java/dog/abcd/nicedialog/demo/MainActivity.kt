package dog.abcd.nicedialog.demo

import android.annotation.SuppressLint
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gyf.immersionbar.ktx.immersionBar
import dog.abcd.nicedialog.NiceDialog
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
        btnNormal.setOnClickListener {
            NiceDialog(DialogNiceBinding.inflate(layoutInflater))
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
                }.bind {
                    binding.tvMessage.text = "Nice Dialog!"
                    binding.btnConfirm.text = "Cool!"
                    binding.btnConfirm.setOnClickListener {
                        dismiss()
                    }
                }.show(supportFragmentManager, "tag")
        }
//            btnSimple.setOnClickListener {
//                TestDialogFactory(this)
//                    .onNext {
//                        Toast.makeText(this, it, Toast.LENGTH_LONG).show()
//                    }
//                    .onFinish {
//                        Toast.makeText(this, it, Toast.LENGTH_LONG).show()
//                    }
//                    .create()
//                    .config {
//                        animatorStyleRes = R.style.NiceDialog_Animation_Zoom
//                    }
//                    .bind {
//                        binding.tvMessage.text = "${binding.tvMessage.text}!!"
//                    }
//                    .show(supportFragmentManager, "tag")
//        }
        btnSimple.setOnClickListener {
//            NiceDialog(DialogNiceBinding.inflate(layoutInflater)).config {
//                width = WindowManager.LayoutParams.MATCH_PARENT
//                height = WindowManager.LayoutParams.WRAP_CONTENT
//                gravity = Gravity.CENTER
//            }.bind {
//                binding.btnCancel.text = "NEW CANCEL"
//                binding.btnConfirm.text = "NEW NICE"
//                binding.tvMessage.text = "NICE DIALOG"
//                binding.btnConfirm.setOnClickListener {
//                    dismiss()
//                }
//            }.show(supportFragmentManager, "tag")
            AlertFactory(this).onNext {
                Toast.makeText(this,it.toString(),Toast.LENGTH_SHORT).show()
            }.create().show(supportFragmentManager,"tag")
        }
        btnDifficult.setOnClickListener {
            TestDialogFactory(this)
                .onNext {
                    //这个是TestDialogFactory中传递回来的参数
                    //紧接着再弹一个弹窗
                    TestDialogFactory(this).create().bind {
                        //利用immersionBar修改状态栏显示
                        immersionBar {
                            fitsSystemWindows(false)
                            statusBarDarkFont(true)
                        }
                        binding.tvMessage.text = it
                        binding.btnConfirm.text = "Nice!"
                        binding.btnConfirm.setOnClickListener {
                            dismiss()
                            immersionBar {
                                fitsSystemWindows(true)
                                statusBarColor(R.color.white)
                                statusBarDarkFont(true)
                            }
                            //再调用一下圆圈弹窗
                            this@MainActivity.btnSimple.callOnClick()
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
                }.bind {
                    //多次绑定view属性
                    binding.btnConfirm.text = "Second!"
                }.bind {
                    binding.btnConfirm.text = "Third!"
                }.show(supportFragmentManager, javaClass.simpleName)
        }

    }

}
