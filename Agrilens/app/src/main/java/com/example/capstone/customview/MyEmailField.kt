package com.example.capstone.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.capstone.R

class MyEmailField @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    private var mailImage: Drawable = ContextCompat.getDrawable(context, R.drawable.ic_baseline_mail_24)!!

    init {
        setCompoundDrawablesWithIntrinsicBounds(mailImage, null, null, null)
        compoundDrawablePadding = 16
        setBackgroundResource(R.drawable.rounded_edit_text)
        setPaddingRelative(20, 20, 20, 20)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                error = if (!Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
                    context.getString(R.string.invalid_email)
                } else null
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        hint = context.getString(R.string.email_hint)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }
}
