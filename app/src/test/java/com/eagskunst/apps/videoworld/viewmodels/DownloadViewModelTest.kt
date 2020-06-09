package com.eagskunst.apps.videoworld.viewmodels

import com.eagskunst.apps.videoworld.app.network.responses.clips.ClipResponse
import com.eagskunst.apps.videoworld.testShared.TestValuesUtils
import com.eagskunst.apps.videoworld.utils.DownloadState
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import java.nio.file.Paths

/**
 * Created by eagskunst in 9/6/2020.
 */

class DownloadViewModelTest {

    private val dumbPath = "dumbPath" //Represent context.filesDir.path
    private val dumbUrl = "twitch.com/clip"
    lateinit var viewModel: DownloadViewModel
    private val clips = TestValuesUtils.createClipsResponses(6, thumbUrl = dumbUrl)

    @Before
    fun setup() {
        val dumbDir = File(dumbPath)
        dumbDir.mkdir()

        viewModel = DownloadViewModel(dumbPath)
        clips.forEachIndexed { index, clipResponse ->
            if (index % 2 == 0)
                File("$dumbPath/${clipResponse.getClipFilename()}").createNewFile()
        }
    }

    @Test
    fun assertFilesPaths() {
        val files = clips.map { viewModel.getClipFile(it) }
        var idx = 0
        for (file in files) {
            val expected = Paths.get("$dumbPath/${clips[idx].getClipFilename()}")?.toString()
            val actual = file.path
            idx += 1
            assertThat(actual, `is`(expected))
        }
    }

    @Test
    fun assertFinalUrlForPlayback() {
        viewModel.updateDownloadedVideosList(clips)

        clips.forEachIndexed { index, clipResponse ->
            val actual = viewModel.getClipUrl(clipResponse)

            val expected = if (index % 2 == 0) {
                viewModel.getClipFile(clipResponse).path
            } else {
                clipResponse.getClipUrl()
            }

            assertThat(actual, `is`(expected))
        }
    }

    @Test
    fun assertClipsDownloadUrl() {
        val rgx = Regex("[^/]+\$")

        clips.forEach { clipResponse ->
            val expected = rgx.find(clipResponse.getClipUrl())?.value
            val actual = viewModel.getDownloadUrlOfClip(clipResponse)

            assertThat(actual, `is`(expected))
        }
    }

    @Test
    fun assertDownloadStateForClips_JustAdding() {
        viewModel.updateDownloadedVideosList(clips)

        val downloadingClips = TestValuesUtils
            .createClipsResponses(size = 3)
            .also { clips ->
                clips.forEach { viewModel.addVideoToDownloadingList(it) }
            }

        clips.forEachIndexed { index, clip ->

            val actual = viewModel.getDownloadStateForClip(clip)

            val expected = if (index % 2 == 0) {
                DownloadState.DOWNLOADED
            } else {
                DownloadState.NOT_DOWNLOADED
            }

            assertThat(actual, `is`(expected))
        }

        downloadingClips.forEach { clip ->

            val actual = viewModel.getDownloadStateForClip(clip)
            val expected = DownloadState.DOWNLOADING

            assertThat(actual, `is`(expected))
        }
    }

    @Test
    fun assertDownloadStateForClips_AddingAndRemoving() {
        viewModel.updateDownloadedVideosList(clips)

        val downloadingClips = TestValuesUtils
            .createClipsResponses(size = 3)
            .also { clips ->
                clips.forEach { viewModel.addVideoToDownloadingList(it) }
            }

        viewModel.updateDownloadedVideosList(downloadingClips.last())

        checkClipState_AfterOperation(DownloadState.DOWNLOADED, downloadingClips.last())

        val deletedClip = clips.first()
        viewModel.deleteClipInFiles(deletedClip)

        checkClipState_AfterOperation(DownloadState.NOT_DOWNLOADED, deletedClip)

        val removedClip = downloadingClips.first()
        viewModel.removeVideoFromDownloadingList(removedClip)

        checkClipState_AfterOperation(DownloadState.NOT_DOWNLOADED, removedClip)
    }

    private fun checkClipState_AfterOperation(expected: Int, clip: ClipResponse) {
        val actual = viewModel.getDownloadStateForClip(clip)

        assertThat(actual, `is`(expected))
    }

    @After
    fun clean() {
        val dumbDir = File(dumbPath)
        dumbDir.deleteRecursively()
    }

}