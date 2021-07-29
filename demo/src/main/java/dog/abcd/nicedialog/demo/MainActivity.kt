package dog.abcd.nicedialog.demo

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
            autoStatusBarDarkModeEnable(true)
            autoNavigationBarDarkModeEnable(true)
            navigationBarColor(R.color.white)
        }
        setContentView(R.layout.activity_main)
        //注意使用回调中使用的activity对象是从fragment里面来的
        btnNormal.setOnClickListener {
            //直接弹出弹窗
            NiceDialog(DialogNiceBinding::class.java)
                .config {
                    width = WindowManager.LayoutParams.MATCH_PARENT
                    height = WindowManager.LayoutParams.WRAP_CONTENT
                    gravity = Gravity.BOTTOM
                    backgroundColor = 0x00000000
                    cancelable = true
                    animatorStyleRes = R.style.NiceDialog_Animation_SlideBottom
                }.bind {
                    binding.tvMessage.text = "Nice Dialog!"
                    binding.btnConfirm.text = "Cool!" + activity.toString()
                    binding.btnCancel.text = state?.getString("activity")
                    binding.btnConfirm.setOnClickListener {
                        dismiss()
                        Toast.makeText(context, "onClick!!!", Toast.LENGTH_SHORT).show()
                        //使用kotlin生成的控件需要使用activity标记作用域，Java的或许需要findViewById，不过我没试过
                        activity!!.btnNormal.text = "屏幕旋转看效果"
                    }
                    binding.btnCancel.setOnClickListener {
                        NiceDialog.dismiss("tagNormal")
                        //使用tag关闭弹窗
                        Toast.makeText(context, "dismiss by tag!!!", Toast.LENGTH_SHORT).show()
                    }
                }.show(supportFragmentManager, "tagNormal")
                .onDismiss {
                    //dismiss回调
                    Toast.makeText(activity, "onDismiss!!!", Toast.LENGTH_SHORT).show()
                    activity!!.btnNewActivity.text = "屏幕旋转看效果"
                }.onSaveInstanceState {
                    it.putString("activity", binding.btnConfirm.text.toString())
                }
        }

        btnSimple.setOnClickListener {
            AlertFactory(this).create().bind {
                //旋转屏幕为横向时，可以撑满全屏
                immersionBar {
                    fitsSystemWindows(false)
                }
            }.show(supportFragmentManager, "dialogSimple")
        }

        btnDifficult.setOnClickListener {
            Log.e(javaClass.simpleName, this.toString())
            TestDialogFactory(this).onNext {
                Toast.makeText(activity, "onNext", Toast.LENGTH_SHORT).show()
                requireActivity().btnNormal.callOnClick()
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
            }.show(supportFragmentManager, "dialog1")
        }

        btnNewActivity.setOnClickListener {
            val intent = Intent()
            intent.setClass(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

}
