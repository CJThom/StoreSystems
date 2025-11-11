
```kotlin
import kotlinx.coroutines.flow.Flow


class CreateMessageUseCase(
    private val messengerRepository: MessengerRepository
) {

    suspend operator fun invoke(
        messageText: String
    ): UseCaseResult {

        val sendChatMessage = SendChatMessage(
            message = messageText
        )

        val result = messengerRepository.createMessage(sendChatMessage = sendChatMessage)

        when (result) {
            is DataResult.Success -> {
                return UseCaseResult.Success(sendChatMessage = sendChatMessage)
            }

            is DataResult.Error.Client -> {
                return UseCaseResult.Error.Client
            }

            is DataResult.Error.Network -> {
                return UseCaseResult.Error.Network
            }

        }
    }

    sealed interface UseCaseResult {
        data class Success(val sendChatMessage: SendChatMessage) : UseCaseResult
        sealed class Error(val message: String) : UseCaseResult {
            data object Client : Error("Unknown client error")
            data object Network : Error("Unknown network error")
        }
    }

}


```

```kotlin
import io.ktor.http.ContentType
import kotlinx.coroutines.flow.Flow

class GetChatMessageListFlowUseCase(
    private val messengerRepository: MessengerRepository,
) {

    operator fun invoke(chatId: String): Flow<List<Message>> {
        return messengerRepository.getMessageListFlow(chatId = chatId)
    }

}
```
