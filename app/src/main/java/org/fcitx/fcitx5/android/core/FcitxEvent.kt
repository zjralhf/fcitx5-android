/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * SPDX-FileCopyrightText: Copyright 2021-2023 Fcitx5 for Android Contributors
 */
package org.fcitx.fcitx5.android.core

sealed class FcitxEvent<T>(open val data: T) {

    data class Candidate(val label: String, val text: String, val comment: String)

    abstract val eventType: EventType

    data class CandidateListEvent(override val data: Data) :
        FcitxEvent<CandidateListEvent.Data>(data) {

        override val eventType = EventType.Candidate

        data class Data(
            val total: Int = -1,
            val candidates: Array<String> = emptyArray(),
            val currentPage: Int = -1
        ) {

            override fun toString(): String =
                "total=$total, candidates=[${candidates.joinToString(limit = 5)}], currentPage: $currentPage"

            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as Data

                if (total != other.total) return false
                if (!candidates.contentEquals(other.candidates)) return false

                return true
            }

            override fun hashCode(): Int {
                var result = total
                result = 31 * result + candidates.contentHashCode()
                return result
            }
        }
    }

    data class CommitStringEvent(override val data: Data) :
        FcitxEvent<CommitStringEvent.Data>(data) {

        override val eventType = EventType.Commit

        data class Data(val text: String, val cursor: Int)
    }

    data class ClientPreeditEvent(override val data: FormattedText) :
        FcitxEvent<FormattedText>(data) {

        override val eventType = EventType.ClientPreedit

        override fun toString(): String = "ClientPreeditEvent('$data', ${data.cursor})"
    }

    data class InputPanelEvent(override val data: Data) : FcitxEvent<InputPanelEvent.Data>(data) {

        override val eventType = EventType.InputPanel

        data class Data(
            val preedit: FormattedText,
            val auxUp: FormattedText,
            val auxDown: FormattedText
        ) {
            constructor() : this(FormattedText.Empty, FormattedText.Empty, FormattedText.Empty)
        }
    }

    data class ReadyEvent(override val data: Unit = Unit) : FcitxEvent<Unit>(data) {

        override val eventType = EventType.Ready

        override fun toString(): String = "ReadyEvent"
    }

    data class KeyEvent(override val data: Data) : FcitxEvent<KeyEvent.Data>(data) {

        override val eventType = EventType.Key

        data class Data(
            val sym: KeySym,
            val states: KeyStates,
            val unicode: Int,
            val up: Boolean,
            val timestamp: Int
        )
    }

    data class IMChangeEvent(override val data: InputMethodEntry) :
        FcitxEvent<InputMethodEntry>(data) {
        override val eventType: EventType
            get() = EventType.Change
    }

    data class StatusAreaEvent(override val data: Data) : FcitxEvent<StatusAreaEvent.Data>(data) {

        override val eventType = EventType.StatusArea

        data class Data(val actions: Array<Action>, val im: InputMethodEntry) {
            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as Data

                if (!actions.contentEquals(other.actions)) return false
                if (im != other.im) return false

                return true
            }

            override fun hashCode(): Int {
                var result = actions.contentHashCode()
                result = 31 * result + im.hashCode()
                return result
            }
        }
    }

    data class DeleteSurroundingEvent(override val data: Data) :
        FcitxEvent<DeleteSurroundingEvent.Data>(data) {

        override val eventType = EventType.DeleteSurrounding

        data class Data(val before: Int, val after: Int)
    }

    data class PagedCandidateEvent(override val data: Data) :
        FcitxEvent<PagedCandidateEvent.Data>(data) {

        override val eventType = EventType.PagedCandidate

        enum class LayoutHint(value: Int) {
            NotSet(0), Vertical(1), Horizontal(2);

            companion object {
                private val Types = entries.toTypedArray()
                fun of(value: Int) = Types[value]
            }
        }

        data class Data(
            val candidates: Array<Candidate>,
            val cursorIndex: Int,
            val layoutHint: LayoutHint,
            val hasPrev: Boolean,
            val hasNext: Boolean,
            val currentPage: Int = -1
        ) {
            companion object {
                @Suppress("BooleanLiteralArgument")
                val Empty = Data(emptyArray(), -1, LayoutHint.NotSet, false, false, -1)
            }

            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as Data

                if (!candidates.contentEquals(other.candidates)) return false
                if (cursorIndex != other.cursorIndex) return false
                if (layoutHint != other.layoutHint) return false
                if (hasPrev != other.hasPrev) return false
                if (hasNext != other.hasNext) return false

                return true
            }

            override fun hashCode(): Int {
                var result = candidates.contentHashCode()
                result = 31 * result + cursorIndex
                result = 31 * result + layoutHint.hashCode()
                result = 31 * result + hasPrev.hashCode()
                result = 31 * result + hasNext.hashCode()
                return result
            }
        }
    }

    data class UnknownEvent(override val data: Array<Any>) : FcitxEvent<Array<Any>>(data) {

        override val eventType = EventType.Unknown

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as UnknownEvent

            if (!data.contentEquals(other.data)) return false

            return true
        }

        override fun hashCode(): Int {
            return data.contentHashCode()
        }
    }

    enum class EventType {
        Candidate,
        Commit,
        ClientPreedit,
        InputPanel,
        Ready,
        Key,
        Change,
        StatusArea,
        DeleteSurrounding,
        PagedCandidate,
        Unknown
    }

    companion object {

        private val Types = EventType.entries.toTypedArray()

        @Suppress("UNCHECKED_CAST")
        fun create(type: Int, params: Array<Any>) =
            when (Types[type]) {
                EventType.Candidate -> CandidateListEvent(
                    CandidateListEvent.Data(
                        params[0] as Int,
                        params[1] as Array<String>,
                        params[2] as Int
                    )
                )
                EventType.Commit -> CommitStringEvent(
                    CommitStringEvent.Data(
                        params[0] as String,
                        params[1] as Int
                    )
                )
                EventType.ClientPreedit -> ClientPreeditEvent(params[0] as FormattedText)
                EventType.InputPanel -> InputPanelEvent(
                    InputPanelEvent.Data(
                        params[0] as FormattedText,
                        params[1] as FormattedText,
                        params[2] as FormattedText
                    )
                )
                EventType.Ready -> ReadyEvent()
                EventType.Key -> KeyEvent(
                    KeyEvent.Data(
                        KeySym(params[0] as Int),
                        KeyStates.of(params[1] as Int),
                        params[2] as Int,
                        params[3] as Boolean,
                        params[4] as Int
                    )
                )
                EventType.Change -> IMChangeEvent(params[0] as InputMethodEntry)
                EventType.StatusArea -> StatusAreaEvent(
                    StatusAreaEvent.Data(
                        params[0] as Array<Action>,
                        params[1] as InputMethodEntry
                    )
                )
                EventType.DeleteSurrounding -> (params[0] as IntArray).let {
                    DeleteSurroundingEvent(DeleteSurroundingEvent.Data(it[0], it[1]))
                }
                EventType.PagedCandidate -> if (params.isEmpty()) {
                    PagedCandidateEvent(PagedCandidateEvent.Data.Empty)
                } else {
                    PagedCandidateEvent(
                        PagedCandidateEvent.Data(
                            params[0] as Array<Candidate>,
                            params[1] as Int,
                            PagedCandidateEvent.LayoutHint.of(params[2] as Int),
                            params[3] as Boolean,
                            params[4] as Boolean,
                            params[5] as Int
                        )
                    )
                }
                else -> UnknownEvent(params)
            }
    }
}
