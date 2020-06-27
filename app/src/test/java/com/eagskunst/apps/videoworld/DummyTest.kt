package com.eagskunst.apps.videoworld

import com.eagskunst.apps.videoworld.app.network.responses.clips.ClipResponse
import com.eagskunst.apps.videoworld.app.network.responses.clips.UserClipsResponse
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test

/**
 * Created by eagskunst in 20/6/2020.
 */
class DummyTest {

    val clipsResponse: ClipResponse = mockk(relaxed = true)
    var xd = ":D"

    @Before
    fun setup() {
        every { clipsResponse.thumbnailUrl }.answers {
            xd = ":("
            "Hola"
        }
    }

    @Test
    fun checkMock() {
        assert(clipsResponse.thumbnailUrl == "Hola")
        assert(this.xd == ":(")
    }
}