package com.eagskunst.apps.videoworld.data

import com.eagskunst.apps.videoworld.app.network.responses.clips.ClipResponse
import com.eagskunst.apps.videoworld.utils.Constants
import com.eagskunst.apps.videoworld.utils.formatInt
import io.mockk.every
import io.mockk.mockk
import java.text.ParseException
import java.util.Date
import java.util.UUID
import kotlin.math.pow
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

/**
 * Created by eagskunst in 8/6/2020.
 */
@Suppress("UNCHECKED_CAST")
class ClipResponseTest {

    lateinit var clipResponse: ClipResponse

    @Rule
    @JvmField
    val exception: ExpectedException = ExpectedException.none()

    @Before
    fun setup() {
        clipResponse = mockk()
    }

    @Test
    fun assertViewsFormatted() {
        val numbers = mockViewCount().iterator()
        var viewCount = numbers.next()

        while (viewCount <= 100) {
            assertNumberRemainsTheSameInFormat(viewCount)
            viewCount = numbers.next()
        }

        while (viewCount in (1000..100000)) {
            assertFormat_containsLetter_AndAtLeastFirstDigit('K', viewCount)
            viewCount = numbers.next()
        }

        assertFormat_containsLetter_AndAtLeastFirstDigit('M', viewCount)
    }

    private fun mockViewCount(): List<Int> {
        val numbers = (0..6).map { n -> 10.0.pow(n.toDouble()).toInt() }
        every { clipResponse.viewCount } returnsMany numbers
        every { clipResponse.viewCountFormatted } returnsMany numbers.map { "Views: ${it.formatInt()}" }
        return numbers
    }

    private fun assertNumberRemainsTheSameInFormat(viewCount: Int) {
        val expected = "Views: $viewCount"
        val actual = clipResponse.viewCountFormatted

        assertThat(actual, `is`(expected))
    }

    private fun assertFormat_containsLetter_AndAtLeastFirstDigit(letter: Char, viewCount: Int) {
        val firstDigit = viewCount.toString()[0]
        val format = clipResponse.viewCountFormatted

        assert(format.contains(firstDigit) and format.contains(letter))
    }

    @Test
    fun assertDateParsing() {
        val (mocks, dates) = mockDates()

        givenSomeDates_assertValidFormatting(
            dates.subList(0, dates.size - 1) as List<String>,
            mocks as List<ClipResponse>
        )

        givenInvalidDate_assertExceptionIsThrown(mocks.last())
    }

    private fun mockDates(): List<List<Any>> {
        val dates = listOf(
            "2020-06-10T12:33:17Z",
            "2018-05-09T12:33:17Z",
            "2017-04-08T00:34:17Z",
            "2010-01-01 12:33:17"
        )

        val mocks = dates.indices.map { mockk<ClipResponse>() }

        mocks.forEachIndexed { idx, clip ->
            every { clip.createdAt } returns dates[idx]
            every { clip.dateFormatted() } answers { callOriginal() }
        }

        return listOf(mocks, dates)
    }

    private fun givenSomeDates_assertValidFormatting(dates: List<String>, mocks: List<ClipResponse>) {
        dates.forEachIndexed { idx, sDate ->

            val expected: () -> String = {
                val date = Constants.TWITCH_DATE_SDF.parse(sDate)
                Constants.GLOBAL_SDF.format(date ?: Date())
            }

            val actual = mocks[idx].dateFormatted()

            assertThat(
                actual,
                `is`(expected())
            )
        }
    }

    private fun givenInvalidDate_assertExceptionIsThrown(clip: ClipResponse) {
        exception.expect(ParseException::class.java)
        clip.dateFormatted()
    }

    @Test
    fun assertClipFilenames() {
        val filenames = (0..5).map { UUID.randomUUID().toString() }
        val clips = (0..5)
            .map { mockk<ClipResponse>() }
            .also { clips ->
                clips.forEachIndexed { idx, clip ->
                    every { clip.id } returns filenames[idx]
                    every { clip.getClipFilename() }.answers { callOriginal() }
                }
            }

        clips.forEachIndexed { idx, clip ->
            val expected = "${filenames[idx]}.mp4"
            val actual = clip.getClipFilename()

            assertThat(actual, `is`(expected))
        }
    }

    @Test
    fun assertClipFinalUrl() {
        val validClip = mockk<ClipResponse>()
        val invalidClip = mockk<ClipResponse>()

        every { validClip.thumbnailUrl } returns "https://clips-media-assets2.twitch.tv/AT-cm%7C386828697-preview-480x272.jpg"
        every { invalidClip.thumbnailUrl } returns "https://nothing.com"
        every { validClip.getClipUrl() } answers { callOriginal() }
        every { invalidClip.getClipUrl() } answers { callOriginal() }
        val regex = Regex(".*(?=-preview)")

        assertThat(
            validClip.getClipUrl(),
            `is`(regex.find(validClip.thumbnailUrl)?.value + ".mp4")
            )

        assertThat(
            invalidClip.getClipUrl(),
            `is`("null.mp4")
        )
    }
}
