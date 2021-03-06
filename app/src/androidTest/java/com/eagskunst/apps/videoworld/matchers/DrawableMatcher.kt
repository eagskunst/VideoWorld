package com.eagskunst.apps.videoworld.matchers

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.drawable.toBitmap
import com.agoda.kakao.common.utilities.getResourceColor
import com.agoda.kakao.common.utilities.getResourceDrawable
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher


/**
 * Created by eagskunst in 1/7/2020.
 */
class DrawableMatcher(
    @DrawableRes private val resId: Int = -1,
    private val drawable: Drawable? = null,
    @ColorRes private val tintColorId: Int? = null,
    private val toBitmap: ((drawable: Drawable) -> Bitmap)? = null
) : TypeSafeMatcher<View>(View::class.java) {

    override fun describeTo(desc: Description) {
        desc.appendText("with drawable id $resId or provided instance")
    }

    override fun matchesSafely(view: View?): Boolean {
        if (view !is ImageView && drawable == null) {
            return false
        }

        if (resId < 0 && drawable == null) {
            return (view as ImageView).drawable == null
        }

        return view?.let { imageView ->
            var expectedDrawable: Drawable? = drawable ?: getResourceDrawable(resId)

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && expectedDrawable != null) {
                expectedDrawable = DrawableCompat.wrap(expectedDrawable).mutate()
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                tintColorId?.let { tintColorId ->
                    val tintColor = getResourceColor(tintColorId)
                    expectedDrawable?.apply {
                        setTintList(ColorStateList.valueOf(tintColor))
                        setTintMode(PorterDuff.Mode.SRC_IN)
                    }
                }
            }

            if (expectedDrawable == null) {
                return false
            }

            val convertDrawable = (imageView as ImageView).drawable
            val bitmap = toBitmap?.invoke(convertDrawable) ?: convertDrawable.toBitmap()

            val otherBitmap = toBitmap?.invoke(expectedDrawable) ?: expectedDrawable.toBitmap()

            if (bitmap.height == otherBitmap.height && bitmap.width == otherBitmap.width) {
                for (i in 0 until bitmap.width) {
                    for (j in 0 until bitmap.height) {
                        if (bitmap.getPixel(i,j) != otherBitmap.getPixel(i, j)) {
                            return false
                        }
                    }
                }
            }

            return true
        } ?: false
    }
}
