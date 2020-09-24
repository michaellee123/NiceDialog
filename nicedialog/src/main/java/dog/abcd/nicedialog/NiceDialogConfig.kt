package dog.abcd.nicedialog

import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.WindowManager

/**
 * 弹窗配置
 */
class NiceDialogConfig {
    /**
     * 最外层宽度
     */
    var width = WindowManager.LayoutParams.MATCH_PARENT

    /**
     * 最外层高度
     */
    var height = WindowManager.LayoutParams.MATCH_PARENT

    /**
     * 弹窗整体位置
     */
    var gravity = Gravity.BOTTOM

    /**
     * 弹窗背景（要设置padding才能看见），
     * fitsSystemWindows设置为true时充当状态栏背景,
     * fitsSystemWindows设置为false时状态栏背景就是布局文件最外层的背景
     */
    var backgroundDrawable = ColorDrawable(0x00000000)
    var paddingTop = 0
    var paddingBottom = 0
    var paddingLeft = 0
    var paddingRight = 0
    var cancelable = true

    /**
     * 动画style id
     */
    var animatorStyleRes = 0

    /**
     * 窗口内容透明度
     */
    var alpha = 1f

    /**
     * 背景透明度
     */
    var dimAmount = -1f

}