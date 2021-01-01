package com.eagskunst.apps.videoworld.data

import com.eagskunst.apps.videoworld.app.models.PlayerState
import com.eagskunst.apps.videoworld.app.network.responses.clips.ClipResponse
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

/**
 * Created by eagskunst in 8/6/2020.
 */
class PlayerStateTest {

    @MockK
    lateinit var notEmptyList: List<ClipResponse>

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        every { notEmptyList.size } returns 5
    }

    @Test
    fun checkMaxPosition_SameAsListPosition() {
        val state = PlayerState(notEmptyList, 0)
        assertThat(state.maxPosition, `is`(notEmptyList.size))
    }

    @Test
    fun checkMaxPosition_forEmptyList_isZero() {
        val state = PlayerState(emptyList(), 0)
        assertThat(state.maxPosition, `is`(emptyList<Any>().size))
    }
}
