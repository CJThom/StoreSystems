```kotlin
data class FeatureDto(
    val description: Int
)

data class ProductDto(
    val name: String
)

data class ProductWithFeaturesDto(
    val productDto: ProductDto,
    val featureDtoList: List<FeatureDto>
)

data class MetaDataDto(
    val data: String
)

data class ProductsDto(
    val productWithFeaturesDtoList: List<ProductWithFeaturesDto>,
    val error: String,
    val metaDataDto: MetaDataDto
)

val productWithFeaturesDtoList: List<ProductWithFeaturesDto> = emptyList()


data class Dimensions(
    val width: Double,
)

data class FeatureEntity(
    val description: Int
)

data class Image(
    val url: String
)

data class ProductEntity(
    val name: String
)

// If 3 or less joins use "With" to combine each class
data class ProductWithFeaturesWithImagesRelation(
    val product: ProductEntity,
    val featureList: List<FeatureEntity>,
    val imageList: List<Image>
)

// If joining more than 3 classes then use the "Full" suffix
data class ProductFullRelation(
    val product: ProductEntity,
    val featureList: List<FeatureEntity>,
    val imageList: List<Image>,
    val dimensions: Dimensions
)

val productWithFeaturesWithImagesRelationList: List<ProductWithFeaturesWithImagesRelation> =
    emptyList()

val productFullRelationList: List<ProductFullRelation> = emptyList()

```

```kotlin
// Database table subset of an entity flow

// Data layer
@Entity(tableName = "products")
data class ProductEntity(
    val name: String,
    val price: Double,
    val brand: String,
)

data class ProductEntitySubset(
    val name: String,
    val price: Double
)

// Domain layer
data class Product(
    val name: String,
    val price: Double,
    val brand: String,
)

data class ProductSubset(
    val name: String,
    val price: Double
)

// Presentation layer
data class ProductStateSubset(
    val name: String,
    val price: Double
)

```

```kotlin

/////// data/network/dto
data class FeatureDto(
    val description: String
)

data class ProductDto(
    val description: String,
    val featureDtoList: List<FeatureDto>
)

/////// data/database/entity
data class FeatureEntity(
    val description: String
)

data class ProductEntity(
    val name: String,
    val featureEntityList: List<FeatureEntity>
)

/////// domain/model
data class Feature(
    val description: String
)

data class Product(
    val name: String,
    val featureList: List<Feature>
)

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

```

```kotlin
//Function naming conventions

@Dao
abstract class Dao {

    @Query("SELECT * FROM products")
    suspend fun getProductEntityList(): List<ProductEntity>

    @Query("SELECT * FROM products")
    fun getProductEntityListFlow(): Flow<List<ProductEntity>>

    @Transaction
    @Query("SELECT * FROM products")
    suspend fun getProductWithFeaturesWithImagesRelationList(): List<ProductWithFeaturesWithImagesRelation>

    @Transaction
    @Query("SELECT * FROM products")
    fun getProductWithFeaturesWithImagesRelationListFlow(): Flow<List<ProductWithFeaturesWithImagesRelation>>

    @Transaction
    @Query("SELECT * FROM products")
    suspend fun getProductFullRelationListFlow(): Flow<List<ProductFullRelation>>

    @Insert
    suspend fun insertProductEntity(productEntity: ProductEntity)

    @Insert
    suspend fun insertOrReplaceProductEntityList(productEntityList: List<ProductEntity>)

    @Update
    suspend fun updateProductEntity(productEntity: ProductEntity)

    @Delete
    suspend fun deleteProductEntity(productEntity: ProductEntity)

    @Query("DELETE FROM products")
    suspend fun deleteAllProducts()

    @Transaction
    suspend fun upsertproductWithFeaturesWithImagesRelationList(
        productWithFeaturesWithImagesRelationList: List<ProductWithFeaturesWithImagesRelation>
    ) {
        with(productWithFeaturesWithImagesRelationList){
            upsertProductEntityList(productEntityList)
            upsertFeatureList(featuresEntityList)
            upsertImageEntityList(imageEntityList)
        }
    }

    @Upsert
    fun upsertProductEntityList(productEntityList: List<ProductEntity>)

    @Upsert
    fun upsertFeatureList(featureEntityList: List<FeatureEntity>)

    @Upsert
    fun upsertImageEntityList(imageEntityList: List<ImageEntity>)

}
```

```kotlin
// Repository examples
interface Repository {

    //Use fetch prefix if calling the NetworkDataSource
    suspend fun fetchProductList(): List<Product>

    //Use Get prefix if calling from Dao/Database
    fun getProductWithFeaturesWithImagesList(): List<ProductWithFeaturesWithImages>

    //Use Get prefix if calling from Dao/Database (Flow example)
    fun getProductWithFeaturesWithImagesListFlow(): Flow<List<ProductWithFeaturesWithImages>>

    // Insert operations
    suspend fun insertProduct(product: Product)
    suspend fun insertProductList(productList: List<Product>)

    // Update operations
    suspend fun updateProduct(product: Product)
    suspend fun updateProductList(productList: List<Product>)

    // Delete operations
    suspend fun deleteProduct(product: Product)
    suspend fun deleteProductList(productList: List<Product>)
    suspend fun deleteAllProducts()

    // Upsert operations 
    suspend fun upsertProduct(product: Product)
    suspend fun upsertProductList(productList: List<Product>)

    // Combined operations
    suspend fun upsertProductWithFeaturesWithImages(
        productWithFeatures: ProductWithFeaturesWithImages
    )

    suspend fun upsertProductWithFeaturesWithImagesList(
        productWithFeaturesList: List<ProductWithFeaturesWithImages>
    )
}

```

```kotlin
interface NetworkDataSource {

    //Use get prefix if calling the NetworkDataSource
    suspend fun getProductsDto(): ProductsDto

    //GET request examples
    suspend fun getUserDto(): UserDto
    suspend fun getUserProfileDto(): UserProfileDto

    //POST request examples  
    suspend fun postCreateUserDto(userDto: UserDto): UserDto
    suspend fun postUpdateUserDto(userDto: UserDto): UserDto

    //PUT request examples
    suspend fun putUserDto(userDto: UserDto): UserDto
    suspend fun putUpdateUserProfileDto(userProfileDto: UserProfileDto): UserProfileDto

    //DELETE request examples
    suspend fun deleteUserDto(userId: String)
    suspend fun deleteUserProfileDto(userProfileId: String)

    //Query parameter examples
    suspend fun getUsersByStatusDto(status: String): List<UserDto>
    suspend fun getUsersByTypeDto(type: String, limit: Int): List<UserDto>

    //Path parameter examples
    suspend fun getUserByIdDto(userId: String): UserDto
    suspend fun getUserProfileByIdDto(profileId: String): UserProfileDto
}
```