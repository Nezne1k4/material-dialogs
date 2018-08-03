package com.afollestad.materialdialogs

import android.app.Dialog
import android.content.Context
import android.support.annotation.StringRes
import android.support.annotation.StyleRes
import android.widget.LinearLayout
import android.widget.TextView
import com.afollestad.materialdialogs.R.layout
import com.afollestad.materialdialogs.extensions.addContentScrollView
import com.afollestad.materialdialogs.extensions.getString
import com.afollestad.materialdialogs.extensions.inflate
import com.afollestad.materialdialogs.extensions.setText
import com.afollestad.materialdialogs.extensions.setWindowConstraints
import com.afollestad.materialdialogs.internal.MDRecyclerView
import com.afollestad.materialdialogs.internal.MDRootView
import com.afollestad.materialdialogs.internal.MDScrollView

typealias DialogCallback = (MaterialDialog) -> Unit

internal fun assertOneSet(
  a: Int,
  b: Any?
) {
  if (a == 0 && b == null) {
    throw IllegalArgumentException("You must specify a resource ID or literal value.")
  }
}

enum class Theme(@StyleRes val styleRes: Int) {
  LIGHT(R.style.MD_Light),
  DARK(R.style.MD_Dark)
}

/** @author Aidan Follestad (afollestad) */
class MaterialDialog(
  context: Context,
  internal var theme: Theme = Theme.LIGHT
) : Dialog(context, theme.styleRes) {

  internal val view: MDRootView = inflate(context, R.layout.md_dialog_base)
  internal var autoDismiss = true

  internal val mainFrame: LinearLayout = view.findViewById(R.id.md_frame_main)
  internal var textViewMessage: TextView? = null
  internal var contentScrollView: MDScrollView? = null
  internal var contentScrollViewFrame: LinearLayout? = null
  internal var contentRecyclerView: MDRecyclerView? = null

  init {
    setContentView(view)
    setWindowConstraints()
    view.theme = theme
  }

  fun title(@StringRes textRes: Int = 0, text: CharSequence? = null): MaterialDialog {
    assertOneSet(textRes, text)
    setText(R.id.md_text_title, textRes, text)
    return this
  }

  fun message(@StringRes textRes: Int = 0, text: CharSequence? = null): MaterialDialog {
    addContentScrollView()
    addContentMessageView(textRes, text)
    return this
  }

  fun positiveButton(
    @StringRes positiveRes: Int = 0,
    positiveText: CharSequence? = null,
    click: ((MaterialDialog) -> (Unit))? = null
  ): MaterialDialog {
    setText(
        R.id.md_button_positive, positiveRes, positiveText,
        fallback = android.R.string.ok, click = click
    )
    return this
  }

  fun negativeButton(
    @StringRes negativeRes: Int = 0,
    negativeText: CharSequence? = null,
    click: ((MaterialDialog) -> (Unit))? = null
  ): MaterialDialog {
    setText(
        R.id.md_button_negative, negativeRes, negativeText,
        fallback = android.R.string.cancel, click = click
    )
    return this
  }

  fun neutralButton(
    @StringRes neutralRes: Int = 0,
    neutralText: CharSequence? = null,
    click: ((MaterialDialog) -> (Unit))? = null
  ): MaterialDialog {
    assertOneSet(neutralRes, neutralText)
    setText(R.id.md_button_neutral, neutralRes, neutralText, click = click)
    return this
  }

  fun dismissOnActionButtonClicks(dismiss: Boolean): MaterialDialog {
    this.autoDismiss = dismiss
    return this
  }

  fun debugMode(debugMode: Boolean = true): MaterialDialog {
    view.debugMode = debugMode
    return this
  }

  inline fun onShow(crossinline callback: DialogCallback): MaterialDialog {
    setOnShowListener { callback.invoke(this@MaterialDialog) }
    return this
  }

  inline fun onDismiss(crossinline callback: DialogCallback): MaterialDialog {
    setOnDismissListener { callback.invoke(this@MaterialDialog) }
    return this
  }

  inline fun onCancel(crossinline callback: DialogCallback): MaterialDialog {
    setOnCancelListener { callback.invoke(this@MaterialDialog) }
    return this
  }

  inline fun show(func: MaterialDialog.() -> Unit) {
    this.func()
    super.show()
  }

  private fun addContentMessageView(@StringRes res: Int, text: CharSequence?) {
    if (this.textViewMessage == null) {
      this.textViewMessage = inflate(
          context, layout.md_dialog_stub_message, this.contentScrollViewFrame!!
      )
      this.contentScrollViewFrame!!.addView(this.textViewMessage)
    }
    assertOneSet(res, text)
    this.textViewMessage!!.text = text ?: getString(res)
  }
}