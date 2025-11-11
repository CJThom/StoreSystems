package com.gpcasiapac.storesystems.core.identity.data.network.source

import com.gpcasiapac.storesystems.common.kotlin.DataResult
import com.gpcasiapac.storesystems.core.identity.data.network.dto.LoginRequestDto
import com.gpcasiapac.storesystems.core.identity.data.network.dto.LoginResponseDto
import com.gpcasiapac.storesystems.core.identity.data.network.dto.TokenDto
import com.gpcasiapac.storesystems.core.identity.data.network.dto.UserDto
import com.gpcasiapac.storesystems.common.networking.json.loadJsonResource
import kotlinx.coroutines.delay

class MockIdentityNetworkDataSourceImpl : IdentityNetworkDataSource {


    override suspend fun login(loginRequest: LoginRequestDto): DataResult<LoginResponseDto> {
        delay(1000)
        if (loginRequest.username.isBlank() || loginRequest.password.isBlank()) {
            return DataResult.Error.Network.HttpError(
                code = 400,
                message = "Invalid credentials"
            )
        }
        return try {
            val response: LoginResponseDto = loadJsonResource("mock/identity/login.json")
            DataResult.Success(response)
        } catch (t: Throwable) {
            DataResult.Error.Client.Mapping("Failed to load mock login response from JSON", t)
        }
    }

}
