package com.gpcasiapac.storesystems.feature.collect.domain.model

sealed interface SearchSuggestion {
    val kind: SuggestionKind
    val text: String
}

data class CustomerNameSuggestion(
    override val text: String,
    val customerType: CustomerType,
) : SearchSuggestion {
    override val kind: SuggestionKind = SuggestionKind.CUSTOMER_NAME
}

data class InvoiceNumberSuggestion(override val text: String) : SearchSuggestion {
    override val kind: SuggestionKind = SuggestionKind.INVOICE_NUMBER
}

data class WebOrderNumberSuggestion(override val text: String) : SearchSuggestion {
    override val kind: SuggestionKind = SuggestionKind.WEB_ORDER_NUMBER
}

data class SalesOrderNumberSuggestion(override val text: String) : SearchSuggestion {
    override val kind: SuggestionKind = SuggestionKind.SALES_ORDER_NUMBER
}

data class PhoneSuggestion(override val text: String) : SearchSuggestion {
    override val kind: SuggestionKind = SuggestionKind.PHONE
}
