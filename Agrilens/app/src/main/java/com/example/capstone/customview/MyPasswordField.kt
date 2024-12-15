package com.example.capstone.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.capstone.R

class MyPasswordField @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs), View.OnTouchListener {

    private var lockImage: Drawable =
        ContextCompat.getDrawable(context, R.drawable.ic_baseline_lock_24)!!
    private var eyeImage: Drawable =
        ContextCompat.getDrawable(context, R.drawable.ic_visibility)!!
    private var eyeOffImage: Drawable =
        ContextCompat.getDrawable(context, R.drawable.ic_baseline_eye_off_24)!!
    private var showPassword = false

    init {
        // Mengatur properti awal
        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        transformationMethod = PasswordTransformationMethod.getInstance()
        compoundDrawablePadding = 16

        setButtonDrawables()
        setBackgroundResource(R.drawable.rounded_edit_text)
        setPaddingRelative(20, 20, 20, 20)
        setOnTouchListener(this)

        // Validasi real-time untuk jumlah karakter
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validatePasswordLength(s)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Mengatur hint dan alignment
        hint = context.getString(R.string.password_hint)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    private fun setButtonDrawables() {
        // Mengatur ikon lock di kiri dan ikon mata di kanan
        setCompoundDrawablesWithIntrinsicBounds(
            lockImage, null, if (showPassword) eyeImage else eyeOffImage, null
        )
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        // Deteksi klik pada ikon visibilitas password
        if (event.action == MotionEvent.ACTION_UP &&
            event.x >= width - paddingEnd - eyeImage.intrinsicWidth
        ) {
            togglePasswordVisibility()
            return true
        }
        return false
    }

    private fun togglePasswordVisibility() {
        // Mengubah status visibilitas password
        showPassword = !showPassword
        transformationMethod = if (showPassword) {
            HideReturnsTransformationMethod.getInstance()
        } else {
            PasswordTransformationMethod.getInstance()
        }
        setButtonDrawables()
        setSelection(text?.length ?: 0) // Memastikan kursor tetap di akhir teks
    }

    private fun validatePasswordLength(input: CharSequence?) {
        // Menampilkan error jika password kurang dari 8 karakter
        if (input != null && input.length < 8) {
            if (error == null) {
                error = context.getString(R.string.invalid_password)
            }
        } else {
            error = null
        }
    }

    override fun setError(error: CharSequence?, icon: Drawable?) {
        super.setError(error, null) // Tidak menggunakan ikon error bawaan
        // Menyesuaikan posisi error agar tidak menutupi ikon mata
        if (error != null) {
            setCompoundDrawablesWithIntrinsicBounds(
                lockImage, null, if (showPassword) eyeImage else eyeOffImage, null
            )
        }
    }
}
