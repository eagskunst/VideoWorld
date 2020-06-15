package com.eagskunst.apps.videoworld.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.eagskunst.apps.videoworld.TestCoroutineRule
import com.eagskunst.apps.videoworld.app.di.SESSION_TOKEN
import com.eagskunst.apps.videoworld.app.network.responses.clips.UserClipsResponse
import com.eagskunst.apps.videoworld.app.network.responses.user.UserDataResponse
import com.eagskunst.apps.videoworld.app.repositories.TwitchRepository
import com.eagskunst.apps.videoworld.testShared.TestValuesUtils
import com.eagskunst.apps.videoworld.testShared.getOrAwaitValue
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runners.MethodSorters

/**
 * Created by eagskunst in 14/6/2020.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class TwitchViewModelTest {

    @MockK
    lateinit var twitchRepository: TwitchRepository

    @RelaxedMockK
    lateinit var validResponse: UserDataResponse

    @RelaxedMockK
    lateinit var validClipsResponse: UserClipsResponse

    lateinit var viewModel: TwitchViewModel

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val coroutineRule = TestCoroutineRule()

    private val validUsername = "Rubius"
    val invalidUsername = TestValuesUtils.getRandomString()
    private val validUserId = "FakeId"
    private val invalidUserId = TestValuesUtils.getRandomString()


    @Before
    fun setup() {
        MockKAnnotations.init(this)

        coEvery { twitchRepository.authUser(any()) }.answers {
            SESSION_TOKEN = "ValidToken"
        }

        coEvery { twitchRepository.getUserByName(validUsername, any()) } returns validResponse
        coEvery { twitchRepository.getUserByName(invalidUsername, any()) } returns null
        coEvery { twitchRepository.getUserClips(validUserId, any()) } returns validClipsResponse
        coEvery { twitchRepository.getUserClips(invalidUserId, any()) } returns null

        every { validResponse.dataList[0].id } returns "FakeId"

        viewModel = TwitchViewModel(twitchRepository)
    }

    @Test
    fun assertSessionTokenChange_AfterCreatingNewInstance() {
        runBlocking { delay(1500) }
        assertEquals("ValidToken", SESSION_TOKEN)

        verifyRepoInvocations { scope ->
            twitchRepository.authUser(scope.any())
        }
    }

    @Test
    fun userDataLiveDataUpdates_AfterGettingUserByInput() {
        checkCurrentUserId("")
        checkUserData_invalidUserName()
        checkCurrentUserId("")
        checkUserData_validUserName()
        checkCurrentUserId("FakeId")

        verifyRepoInvocations { scope ->
            twitchRepository.authUser(scope.any())
            twitchRepository.getUserByName(invalidUsername, scope.any())
            twitchRepository.getUserByName(validUsername, scope.any())
        }
    }

    @Test
    fun checkUserClipsLiveData_AfterUsingSomeIds() {
        checkIfClipsExists(false)
        checkUserClips_invalidUserId(invalidUserId)
        checkIfClipsExists(false)
        checkUserClips_validUserId()
        checkIfClipsExists(true)

        //Test for id == currentUserId and userClips != null
        viewModel.getUserByInput(validUsername)
        checkUserClips_validUserId()
        checkIfClipsExists(true)

        //Reset test
        checkUserClips_invalidUserId("")
        checkIfClipsExists(false)

        verifyRepoInvocations { scope ->
            twitchRepository.authUser(scope.any())
            twitchRepository.getUserClips(invalidUserId, scope.any())
            twitchRepository.getUserClips(validUserId, scope.any())
            twitchRepository.getUserByName(validUsername, scope.any())
        }
    }

    private fun checkUserClips_validUserId() {
        viewModel.getUserClips(validUserId)
        val expected: UserClipsResponse? = validClipsResponse
        val actual: UserClipsResponse? = viewModel.userClips.getOrAwaitValue()
        assertThat(actual, `is`(expected))
    }


    private fun checkUserClips_invalidUserId(invalidId: String) {
        viewModel.getUserClips(invalidId)
        val expected: UserClipsResponse? = null
        val actual: UserClipsResponse? = viewModel.userClips.getOrAwaitValue()
        assertEquals(expected, actual)
    }

    private fun checkIfClipsExists(expected: Boolean) {
        assert(viewModel.clipsListExists() == expected)
    }

    private inline fun verifyRepoInvocations(crossinline functions: suspend (MockKMatcherScope) -> Unit) {
        coVerify {
            functions(this)
        }
        confirmVerified(twitchRepository)
    }


    private fun checkUserData_validUserName() {
        viewModel.getUserByInput(validUsername)
        val expected: UserDataResponse? = validResponse
        val actual: UserDataResponse? = viewModel.userData.getOrAwaitValue()
        assertThat(actual, `is`(expected))
    }

    private fun checkUserData_invalidUserName() {
        viewModel.getUserByInput(invalidUsername)
        val expected: UserDataResponse? = null
        val actual: UserDataResponse? = viewModel.userData.getOrAwaitValue()
        assertEquals(expected, actual)
    }

    private fun checkCurrentUserId(expected: String) {
        val actual = viewModel.currentUserId()
        assertThat(actual, `is`(expected))
    }


}