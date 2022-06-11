package com.example.seaselmobile.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.seaselmobile.R

class NavigationButtonView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var textView: TextView
    private val imageView: ImageView

    var buttonText: String? = ""
        set(value) {
            field = value
            textView.text = value
        }

    var buttonImage: Drawable? = null
        set(value) {
            field = value
            imageView.setImageDrawable(value)
        }

    var buttonTextColor: Int = Color.BLACK
        set(value) {
            field = value
            textView.setTextColor(value)
        }

    init {
        inflate(context, R.layout.view_navigation_button, this)
        textView = findViewById(R.id.navigationButtonText)
        imageView = findViewById(R.id.navigationButtonImage)
        context.theme.obtainStyledAttributes(attrs, R.styleable.NavigationButtonView, 0, 0)
            .apply {
                buttonTextColor =
                    getColor(R.styleable.NavigationButtonView_buttonTextColor, Color.BLACK)
                buttonText = getString(R.styleable.NavigationButtonView_buttonText)
                buttonImage = getDrawable(R.styleable.NavigationButtonView_buttonImage)
                recycle()
            }

    }
}