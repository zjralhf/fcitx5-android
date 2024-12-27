/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * SPDX-FileCopyrightText: Copyright 2021-2023 Fcitx5 for Android Contributors
 */
package org.fcitx.fcitx5.android.input.keyboard

import android.annotation.SuppressLint
import android.content.Context
import org.fcitx.fcitx5.android.R
import org.fcitx.fcitx5.android.core.ScancodeMapping
import org.fcitx.fcitx5.android.data.theme.Theme
import org.fcitx.fcitx5.android.input.keyboard.KeyDef.Behavior
import org.fcitx.fcitx5.android.input.picker.PickerWindow
import org.fcitx.fcitx5.android.input.popup.PopupAction
import splitties.views.imageResource

@SuppressLint("ViewConstructor")
class NumberKeyboard(
    context: Context,
    theme: Theme,
) : BaseKeyboard(context, theme, Layout) {

    companion object {
        const val Name = "Number"

        val Layout: List<List<KeyDef>> = listOf(
            listOf(
                NumPadKey("+", 0xffab, 23f, 0.15f, KeyDef.Appearance.Variant.Alternative),
                NumPadKey("1", 0xffb1, 30f, 0f),
                NumPadKey("2", 0xffb2, 30f, 0f),
                NumPadKey("3", 0xffb3, 30f, 0f),
                NumPadKey("/", 0xffaf, 23f, 0.15f, KeyDef.Appearance.Variant.Alternative),
            ),
            listOf(
                NumPadKey("-", 0xffad, 23f, 0.15f, KeyDef.Appearance.Variant.Alternative),
                NumPadKey("4", 0xffb4, 30f, 0f),
                NumPadKey("5", 0xffb5, 30f, 0f),
                NumPadKey("6", 0xffb6, 30f, 0f),
                MiniSpaceKey()
            ),
            listOf(
                NumPadKey("*", 0xffaa, 23f, 0.15f, KeyDef.Appearance.Variant.Alternative),
                NumPadKey("7", 0xffb7, 30f, 0f),
                NumPadKey("8", 0xffb8, 30f, 0f),
                NumPadKey("9", 0xffb9, 30f, 0f),
                BackspaceKey()
            ),
            listOf(
                LayoutSwitchKey("ABC", TextKeyboard.Name),
                NumPadKey(",", 0xffac, 23f, 0.1f, KeyDef.Appearance.Variant.Alternative),
                LayoutSwitchKey("!?#", PickerWindow.Key.Symbol.name, 0.13333f, KeyDef.Appearance.Variant.AltForeground),
                NumPadKey("0", 0xffb0, 30f, 0.23334f),
                NumPadKey(
                    "=",
                    0xffbd,
                    23f,
                    0.13333f,
                    KeyDef.Appearance.Variant.AltForeground,
                    setOf(Behavior.Press(KeyAction.FcitxKeyAction("=")))
                ),
                NumPadKey(".", 0xffae, 23f, 0.1f, KeyDef.Appearance.Variant.Alternative),
                ReturnKey()
            )
        )
    }

    val backspace: ImageKeyView by lazy { findViewById(R.id.button_backspace) }
    val space: TextKeyView by lazy { findViewById(R.id.button_mini_space) }
    val `return`: ImageKeyView by lazy { findViewById(R.id.button_return) }

    override fun onReturnDrawableUpdate(returnDrawable: Int) {
        `return`.img.imageResource = returnDrawable
    }

    @SuppressLint("MissingSuperCall")
    override fun onPopupAction(action: PopupAction) {
        // leave empty on purpose to disable popup in NumberKeyboard
    }

    override fun onAction(action: KeyAction, source: KeyActionListener.Source) {
        if (super.panelStatus && action is KeyAction.SymAction) {
            val scancodeToChar =
                scancodeToChar(ScancodeMapping.keyCodeToScancode(action.sym.keyCode))
            if (scancodeToChar != null) {
                super.onAction(KeyAction.FcitxKeyAction(scancodeToChar), source)
                return
            }
        }
        super.onAction(action, source)
    }

    private fun scancodeToChar(scancode: Int): String? {
        return when (scancode) {
            ScancodeMapping.KEY_KP1 -> "1"
            ScancodeMapping.KEY_KP2 -> "2"
            ScancodeMapping.KEY_KP3 -> "3"
            ScancodeMapping.KEY_KP4 -> "4"
            ScancodeMapping.KEY_KP5 -> "5"
            ScancodeMapping.KEY_KP6 -> "6"
            ScancodeMapping.KEY_KP7 -> "7"
            ScancodeMapping.KEY_KP8 -> "8"
            ScancodeMapping.KEY_KP9 -> "9"
            ScancodeMapping.KEY_KP0 -> "0"
            ScancodeMapping.KEY_KPPLUS -> "+"
            ScancodeMapping.KEY_KPMINUS -> "-"
            ScancodeMapping.KEY_KPASTERISK -> "*"
            ScancodeMapping.KEY_KPSLASH -> "/"
            ScancodeMapping.KEY_KPDOT -> "."
            else -> null
        }
    }
}
