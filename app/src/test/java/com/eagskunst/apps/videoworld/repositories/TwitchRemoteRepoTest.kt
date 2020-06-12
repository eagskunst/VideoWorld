package com.eagskunst.apps.videoworld.repositories

import com.eagskunst.apps.videoworld.TestCoroutineRule
import com.eagskunst.apps.videoworld.app.network.api.ClipsApi
import com.eagskunst.apps.videoworld.app.network.api.TwitchAuthApi
import com.eagskunst.apps.videoworld.app.network.api.UserApi
import com.eagskunst.apps.videoworld.app.network.responses.auth.AuthTokenResponse
import com.eagskunst.apps.videoworld.app.network.responses.clips.UserClipsResponse
import com.eagskunst.apps.videoworld.app.network.responses.user.UserDataResponse
import com.eagskunst.apps.videoworld.app.repositories.TwitchRemoteRepository
import com.eagskunst.apps.videoworld.testShared.TestValuesUtils
import com.eagskunst.apps.videoworld.utils.RemoteErrorEmitter
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

/**
 * Created by eagskunst in 10/6/2020.
 */
@ExperimentalCoroutinesApi
class TwitchRemoteRepoTest {

    @MockK
    lateinit var userApi: UserApi
    @MockK
    lateinit var clipsApi: ClipsApi
    @MockK
    lateinit var authApi: TwitchAuthApi
    @RelaxedMockK
    lateinit var validResponse: UserDataResponse
    @RelaxedMockK
    lateinit var validClipsResponse: UserClipsResponse
    @MockK
    lateinit var validAuthResponse: AuthTokenResponse
    @MockK
    lateinit var errorResponse: Response<*>
    @RelaxedMockK
    lateinit var remoteErrorEmitter: RemoteErrorEmitter
    @Rule
    @JvmField
    val coroutineRule = TestCoroutineRule()

    private val validUsername = "Rubius"
    val invalidUsername = TestValuesUtils.getRandomString()
    private val validUserId = "RubiusID"
    private val invalidUserId = TestValuesUtils.getRandomString()

    @InjectMockKs
    lateinit var twitchRemoteRepo: TwitchRemoteRepository


    @Before
    fun setup() {
        MockKAnnotations.init(this)

        every { errorResponse.message() } returns "Error"
        every { errorResponse.code() } returns 400
        every { errorResponse.errorBody() } returns null
        every { validAuthResponse.accessToken } returns "Valid token"

        coEvery { userApi.getUserByUsername(validUsername) } returns validResponse
        coEvery { userApi.getUserByUsername(invalidUsername) } throws HttpException(errorResponse)
        coEvery { userApi.getUserByUsername("") } throws HttpException(errorResponse)

        coEvery { clipsApi.getClipsByUserId(validUserId) } returns validClipsResponse
        coEvery { clipsApi.getClipsByUserId(invalidUserId) } throws HttpException(errorResponse)
        coEvery { clipsApi.getClipsByUserId("") } throws HttpException(errorResponse)

        coEvery { authApi.getAuthToken() } returns validAuthResponse andThenThrows HttpException(errorResponse)
    }

    @Test
    fun testResponses_UserApiCalls() {
        runBlocking {
            //CheckValid
            var expected: UserDataResponse? = validResponse
            var actual = twitchRemoteRepo.getUserByName(validUsername, remoteErrorEmitter)

            assertEquals(expected, actual)

            //Check invalid
            expected = null
            actual = twitchRemoteRepo.getUserByName(invalidUsername, remoteErrorEmitter)

            assertEquals(expected, actual)

            //Check empty
            expected = null
            actual = twitchRemoteRepo.getUserByName("", remoteErrorEmitter)

            assertEquals(expected, actual)
        }
    }

    @Test
    fun testResponses_ClipsApiCalls() {
        runBlocking {
            //CheckValid
            twitchRemoteRepo.getAuthToken(remoteErrorEmitter)
            coVerify { authApi.getAuthToken() }
            twitchRemoteRepo.getAuthToken(remoteErrorEmitter)
            coVerify { authApi.getAuthToken() }
            confirmVerified(authApi)
        }
    }

    @Test
    fun testResponses_AuthApi() {
        runBlocking {
            //CheckValid
            var expected: UserClipsResponse? = validClipsResponse
            var actual = twitchRemoteRepo.getUserClips(validUserId, remoteErrorEmitter)

            assertEquals(expected, actual)

            //Check invalid
            expected = null
            actual = twitchRemoteRepo.getUserClips(invalidUserId, remoteErrorEmitter)

            assertEquals(expected, actual)

            //Check empty
            expected = null
            actual = twitchRemoteRepo.getUserClips("", remoteErrorEmitter)

            assertEquals(expected, actual)
        }
    }

}