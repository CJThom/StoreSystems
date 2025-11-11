
```kotlin

//{
//    "title": null,
//    "orderNumber": null
//}

data class ItemDto(
    val title: String?, // Not important (Nice for UI)
    val orderNumber: String?, // Critical
)

data class Item(
    val title: String?,
    val orderNumber: String?,
    val error: String?
)

data class ItemState(
    val title: String?,
    val orderNumber: String?,
    val error: String?
)

fun ItemDto.toDomain(): Item {
   return try{
        Item(
            title = this.title,
            orderNumber = requireNotNull(this.orderNumber){ "Order Number is null" },
            error = null
        )
    } catch(exception :Exception){
        Item(
            title = this.title,
            orderNumber = this.orderNumber,
            error = exception.message
        )
    }
}

fun Item.toState(): ItemState {
    return ItemState(
        title = this.title ?: "-",
        orderNumber = this.orderNumber ?: "-",
        error = this.error
    )
}

```

```kotlin
data class Item(
    val title: String,
    val orderNumber: String,
    val error: String?
)

data class ItemState(
    val title: String,
    val orderNumber: String,
    val longTitle: String,
    val isError: Boolean,
    val error: String?
)

internal fun Item.toDisplay(flavourText: String): ItemState {
    return ItemState(
        title = this.title,
        longTitle = "{this.title} $flavourText",
        orderNumber = this.orderNumber,
        isError = isError(error = this.error),
        error = this.error
    )
}

private fun isError(error: String?): Boolean {
    return error != null
}

internal fun List<Item>.toDisplay(flavourText: String): List<ItemState> {
    return map { it.toDisplay(flavourText = flavourText) }
}
```