package com.gpcasiapac.storesystems.common.presentation.compose


sealed class StringWrapper {

    data class Text(val value: String) : StringWrapper()

    class Resource(val resId: Int, vararg val args: Any) : StringWrapper() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Resource) return false

            if (resId != other.resId) return false
            if (!args.contentEquals(other.args)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = resId
            result = 31 * result + args.contentHashCode()
            return result
        }
    }


    class Span(val what: Any, val value: StringWrapper, val subString: StringWrapper) :
        StringWrapper()

    /**
     * Usage:
     * for unformatted plural strings:
     * Plural(resId = R.plural.unformatted_plural_res, quantity = quantityInt)
     *
     * for formatted plural strings (e.g. 1 day, 10 days)
     * Plural(resId = R.plural.formatted_plural_res, quantity = quantityInt, args = arrayOf(quantityInt))
     */

    class Plural(
        val resId: Int,
        val quantity: Int,
        vararg val args: Any
    ) : StringWrapper()
}