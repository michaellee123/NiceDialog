package dog.abcd.kotlindemo

import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.WindowManager

class NiceDialogConfig {
    var width = WindowManager.LayoutParams.MATCH_PARENT
    var height = WindowManager.LayoutParams.MATCH_PARENT
    var gravity = Gravity.BOTTOM
    var backgroundDrawable = ColorDrawable(0x00000000)
    var paddingTop = 0
    var paddingBottom = 0
    var paddingLeft = 0
    var paddingRight = 0

    /**
     * 状态栏字体颜色，如果height不为MATCH_PARENT则无效
     */
    var statusBarDarkFont = false
    var fitsSystemWindows = true
    var cancelable = true
}