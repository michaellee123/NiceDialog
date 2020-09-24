package dog.abcd.nicedialog.demo

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gyf.immersionbar.ImmersionBar
import com.gyf.immersionbar.ktx.immersionBar
import dog.abcd.nicedialog.NiceDialog
import dog.abcd.nicedialog.demo.databinding.DialogNiceBinding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var dialogFactory1: TestDialogFactory? = null

    var dialogFactory2: TestDialogFactory? = null

    var dialog: NiceDialog<DialogNiceBinding>? = null

    lateinit var niceDialog2: NiceDialog<DialogNiceBinding>


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        immersionBar {
            fitsSystemWindows(true)
            statusBarColor(R.color.white)
            autoStatusBarDarkModeEnable(true)
            autoNavigationBarDarkModeEnable(true)
            navigationBarColor(R.color.white)
        }
        setContentView(R.layout.activity_main)
        //注意看部分失效的情况
        btnNormal.setOnClickListener {
            //直接弹出弹窗
            NiceDialog(DialogNiceBinding.inflate(layoutInflater))
                .config {
                    width = WindowManager.LayoutParams.MATCH_PARENT
                    height = WindowManager.LayoutParams.WRAP_CONTENT
                    gravity = Gravity.BOTTOM
                    backgroundDrawable = ColorDrawable(0x00000000)
                    cancelable = true
                    animatorStyleRes = R.style.NiceDialog_Animation_SlideBottom
                }.bind {
                    binding.tvMessage.text = "Nice Dialog!"
                    binding.btnConfirm.text = "Cool!"
                    binding.btnConfirm.setOnClickListener {
                        dismiss()
                        Toast.makeText(context, "onClick!!!", Toast.LENGTH_SHORT).show()
                        //使用kotlin生成的控件需要使用this@MainActivity标记作用域
                        //直接弹出的在屏幕旋转或者其他activity重建的情况下不会生效
                        this@MainActivity.btnNormal.text = "如果屏幕旋转之后不会生效"
                    }
                    binding.btnCancel.setOnClickListener {
                        NiceDialog.dismiss("tagNormal")
                        //使用tag关闭弹窗
                        Toast.makeText(context, "dismiss by tag!!!", Toast.LENGTH_SHORT).show()
                    }
                }.show(supportFragmentManager, "tagNormal")
                .onDismiss {
                    //dismiss回调
                    Toast.makeText(this, "onDismiss!!!", Toast.LENGTH_SHORT).show()
                    //在屏幕旋转或者其他activity重建的情况下不会生效
                    this@MainActivity.btnNewActivity.text = "如果屏幕旋转之后不会生效"
                }
        }

        //使用onResume中创建的dialog
        btnSimple.setOnClickListener {
            dialog?.show()
        }

        //使用onResume中创建的dialogFactory
        btnDifficult.setOnClickListener {
            Log.e(javaClass.simpleName, this.toString())
            dialogFactory1?.create()?.config {
                //多次链式的调用修改config
                height = WindowManager.LayoutParams.WRAP_CONTENT
            }?.config {
                width = WindowManager.LayoutParams.WRAP_CONTENT
            }?.bind {
                //多次绑定view属性
                binding.btnConfirm.text = "Second!"
            }?.bind {
                binding.btnConfirm.text = "Third!"
            }?.show()
        }

        btnNewActivity.setOnClickListener {
            val intent = Intent()
            intent.setClass(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        initDialog()
    }

    fun initDialog() {
        dialogFactory1 =
            NiceDialog.createFactory(
                { TestDialogFactory(this) },
                supportFragmentManager,
                "dialog1"
            )
        dialogFactory1!!.onNext {
            //这个是TestDialogFactory中传递回来的参数
            //紧接着再弹一个弹窗
            niceDialog2.bind {
                //在原有的基础上继续操作控件
                binding.tvMessage.text = it
            }.show()
        }.onFinish {
            //取消的回调
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
        dialogFactory2 =
            NiceDialog.createFactory(
                { TestDialogFactory(this) },
                supportFragmentManager,
                "dialog2"
            )
        niceDialog2 = dialogFactory2!!.create().config {
            height = WindowManager.LayoutParams.MATCH_PARENT
            backgroundDrawable = ColorDrawable(resources.getColor(R.color.colorPrimaryDark))
        }.bind {
            //利用immersionBar修改状态栏显示
            immersionBar {
                fitsSystemWindows(true)
                statusBarDarkFont(false)
                navigationBarDarkIcon(false)
            }
            binding.btnConfirm.text = "Nice!"
            binding.btnConfirm.setOnClickListener {
                dismiss()
                //再调用一下基础按钮点击事件（旋转屏幕后无法显示新弹窗）
                this@MainActivity.btnNormal.callOnClick()
                //调用简单封装按钮点击事件（旋转屏幕后可以显示新弹窗）
                this@MainActivity.btnSimple.callOnClick()
            }
        }
        dialog = NiceDialog.create(
            DialogNiceBinding.inflate(layoutInflater),
            supportFragmentManager,
            "tagSimple"
        ).config {
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            gravity = Gravity.CENTER
        }.bind {
            binding.btnCancel.text = "NEW CANCEL"
            binding.btnConfirm.text = "NEW NICE"
            binding.tvMessage.text = "NICE DIALOG"
            binding.btnConfirm.setOnClickListener {
                dismiss()
                //屏幕旋转后可以继续弹出新的dialog
                niceDialog2.show()
            }
            binding.btnCancel.setOnClickListener {
                dismiss()
                //屏幕旋转后不可以弹出新的dialog
                NiceDialog.createFactory({
                    AlertFactory(this@MainActivity)
                }, supportFragmentManager, "alert").onNext {
                    Toast.makeText(this@MainActivity, it.toString(), Toast.LENGTH_SHORT).show()
                }.create().show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
            NiceDialog.removeDialog("dialog1", "dialog2", "tagSimple", "tagNormal")
        }
    }
}
