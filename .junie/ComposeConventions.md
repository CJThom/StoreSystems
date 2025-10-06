

// ViewModel ViewState to Compose components

```kotlin
/////// presentation/model
data class FeatureState(
    val description: String
)

data class ProductState(
    val name: String,
    val featureStateList: List<FeatureState>
)

//////// presentation/component/*
data class FeatureParams(
    val description: String
)

data class ProductParams(
    val name: String,
    val featureParamsList: List<FeatureParams>
)

fun FeatureState.toParams(): FeatureParams {
    return FeatureParams(description)
}

fun List<FeatureState>.toParams(): List<FeatureParams> {
    return map { it.toParams() }
}
```
```kotlin
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

object ScreenContract {
    data class State(
        val name: String, val productStateList: List<ProductState>
    )
}

// Screen
@Composable
fun Screen(
    viewState: ScreenContract.State,
) {
    ListContent(
        productStateList = viewState.productStateList,
    )
}

// Screen child component
@Composable
private fun ListContent(
    productStateList: List<ProductState>
) {
    Column() {
        productStateList.forEach { productState ->
            ProductCard(
                name = productState.name,
                featureParamsList = productState.featureStateList.toParams()
            )
        }
    }
}

// Reusable component
@Composable
fun ProductCard(
    name: String, featureParamsList: List<FeatureParams>
) {

    Column {
        Text(text = name)
        featureParamsList.forEach { featureParams ->
            Text(text = featureParams.description)
        }
    }

}
```