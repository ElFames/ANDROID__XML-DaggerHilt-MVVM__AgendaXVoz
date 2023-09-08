package agenda.x.voz.core.api

import javax.inject.Inject

class AgendaAPI @Inject constructor(
    private val apiInterface: ApiInterface
) {
    suspend fun getLatestVersion(s: String): Int {
        val response = apiInterface.getLatestVersion("/latestVersion")
        return if (response.isSuccessful) response.body() ?: 0 else 0
    }
}