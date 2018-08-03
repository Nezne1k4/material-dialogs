package com.afollestad.materialdialogs.internal

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.R
import com.afollestad.materialdialogs.extensions.getItemSelector
import com.afollestad.materialdialogs.extensions.hasActionButtons
import com.afollestad.materialdialogs.extensions.inflate

/** @author Aidan Follestad (afollestad) */
internal class MDMultiChoiceViewHolder(
  itemView: View,
  adapter: MDMultiChoiceAdapter,
  dialog: MaterialDialog
) : RecyclerView.ViewHolder(itemView) {
  init {
    itemView.setOnClickListener {
      val newSelection = adapter.currentSelection.toMutableList()
      if (newSelection.contains(adapterPosition)) {
        newSelection.remove(adapterPosition)
      } else {
        newSelection.add(adapterPosition)
      }
      adapter.currentSelection = newSelection.toTypedArray()
      adapter.selectionChanged?.invoke(dialog, adapter.currentSelection, adapter.items)
      if (dialog.autoDismiss && !dialog.hasActionButtons()) {
        dialog.dismiss()
      }
    }
  }

  val controlView: CompoundButton = itemView.findViewById(R.id.md_control)
  val titleView: TextView = itemView.findViewById(R.id.md_title)
}

/**
 * The default list adapter for multiple choice (checkbox) list dialogs.
 *
 * @author Aidan Follestad (afollestad)
 */
internal class MDMultiChoiceAdapter(
  private var dialog: MaterialDialog,
  internal var items: Array<CharSequence>,
  initialSelection: Array<Int>,
  internal var selectionChanged: ((MaterialDialog, Array<Int>, Array<CharSequence>) -> (Boolean))?
) : RecyclerView.Adapter<MDMultiChoiceViewHolder>() {

  var currentSelection: Array<Int> = initialSelection
    set(value) {
      val previousSelection = field
      field = value
      for (previous in previousSelection) {
        if (!value.contains(previous)) {
          // This value was unselected
          notifyItemChanged(previous)
        }
      }
      for (current in value) {
        if (!previousSelection.contains(current)) {
          // This value was selected
          notifyItemChanged(current)
        }
      }
    }

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): MDMultiChoiceViewHolder {
    val listItemView: View = parent.inflate(R.layout.md_listitem_multichoice)
    return MDMultiChoiceViewHolder(listItemView, this, dialog)
  }

  override fun getItemCount(): Int {
    return items.size
  }

  override fun onBindViewHolder(
    holder: MDMultiChoiceViewHolder,
    position: Int
  ) {
    holder.controlView.isChecked = currentSelection.contains(position)
    holder.titleView.text = items[position]
    holder.itemView.background = dialog.getItemSelector(holder.itemView.context)
  }
}