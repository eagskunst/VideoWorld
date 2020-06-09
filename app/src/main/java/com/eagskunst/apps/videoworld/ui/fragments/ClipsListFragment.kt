package com.eagskunst.apps.videoworld.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.eagskunst.apps.videoworld.R
import com.eagskunst.apps.videoworld.app.models.PlayerState
import com.eagskunst.apps.videoworld.app.network.responses.clips.ClipResponse
import com.eagskunst.apps.videoworld.app.network.responses.clips.UserClipsResponse
import com.eagskunst.apps.videoworld.app.workers.VideoDownloadWorker
import com.eagskunst.apps.videoworld.databinding.FragmentClipsBinding
import com.eagskunst.apps.videoworld.progressBar
import com.eagskunst.apps.videoworld.ui.view_holders.clipInfoView
import com.eagskunst.apps.videoworld.utils.DownloadState
import com.eagskunst.apps.videoworld.utils.WorkStateHandler
import com.eagskunst.apps.videoworld.utils.base.BaseFragment
import com.eagskunst.apps.videoworld.utils.isCancelled
import com.eagskunst.apps.videoworld.viewmodels.DownloadViewModel
import com.eagskunst.apps.videoworld.viewmodels.PlayerViewModel
import com.eagskunst.apps.videoworld.viewmodels.TwitchViewModel
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class ClipsListFragment : BaseFragment<FragmentClipsBinding>(R.layout.fragment_clips) {

    override val bindingFunction: (view: View) -> FragmentClipsBinding
        get() = FragmentClipsBinding::bind

    private val twitchViewModel: TwitchViewModel by sharedViewModel()
    private val downloadViewModel: DownloadViewModel by sharedViewModel()
    private val playerViewModel: PlayerViewModel by sharedViewModel()

    private val workStateHandler: WorkStateHandler by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.clipsToolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        twitchViewModel.userData.observe(viewLifecycleOwner, Observer { data ->
            if(data != null && data.dataList.isNotEmpty()){
                val streamerName = data.dataList[0].displayName
                binding.clipsToolbar.title = "$streamerName clips"
            }
        })

        twitchViewModel.userClips.observe(viewLifecycleOwner, Observer { res ->
            if (res != null) {
                downloadViewModel.updateDownloadedVideosList(res.clipResponseList)
            }
            buildRecyclerView(binding, res)
        })

        if(!twitchViewModel.clipsListExists())
            twitchViewModel.getUserClips(twitchViewModel.currentUserId())
    }

    private fun buildRecyclerView(binding: FragmentClipsBinding, res: UserClipsResponse?) {
        binding.clipsRv.withModels {
            if(res == null)
                progressBar { id("progress") }
            else {
                res.clipResponseList.forEach { clip ->
                    val downloadState = downloadViewModel.getDownloadStateForClip(clip)
                    clipInfoView {
                        id(clip.id)
                        clip(clip)
                        downloadState(downloadState)
                        viewClick { _, _, _, position ->

                            playerViewModel.changePlayerState(
                                PlayerState(res.clipResponseList, position)
                            )

                            findNavController().navigate(R.id.action_clipsFragment_to_clipFragment)
                        }

                        downloadClick { _, _, _, _ ->
                            handleDownloadAction(downloadState, clip)
                        }
                    }
                }
            }
        }
    }

    /**
     * Trigger another function depending of the downloadState and calls for a fresh model build
     * of the Epoxy recycler view.
     * @param downloadState: The view's clip's [DownloadState]
     * @param clip: The clip in the view that has been clicked.
     */
    private fun handleDownloadAction(downloadState: Int, clip: ClipResponse) {
        Timber.d("Doing click!")
        when (downloadState) {
            DownloadState.NOT_DOWNLOADED -> startDownloadWork(clip)
            DownloadState.DOWNLOADING -> cancelDownloadWork(clip)
            DownloadState.DOWNLOADED -> downloadViewModel.deleteClipInFiles(clip)
        }
        binding.clipsRv.requestModelBuild()
    }

    /**
     * Starts a [VideoDownloadWorker] from [WorkStateHandler].
     * This updates the [DownloadViewModel]'s download list when the WorkerInfo of the [VideoDownloadWorker]
     * changes.
     * This calls the [TwitchViewModel.getUserClips] function with the current user id just to
     * refresh the recycler view with the new icons for the download state buttons.
     * @param clip: The clip that's going to be downloaded
     */
    private fun startDownloadWork(clip: ClipResponse) {
        val url = downloadViewModel.getDownloadUrlOfClip(clip)
        Timber.d("Computed video URL suffix for download: $url")

        val workId = workStateHandler.startDownloadWork(clip, url)

        downloadViewModel.addVideoToDownloadingList(clip)

        workStateHandler.observeWorkById(workId, requireActivity()) { work ->
            Timber.d("Work tag : ${work.tags}. Work state: ${work.state}")
            val downloadState = work.outputData.getInt(VideoDownloadWorker.DOWNLOAD_STATE, DownloadState.DOWNLOADING)
            Timber.d("Download state: $downloadState")

            if (downloadState == DownloadState.DOWNLOADED) {
                downloadViewModel.removeVideoFromDownloadingList(clip)
                downloadViewModel.updateDownloadedVideosList(clip)
            }
            else if(work.state.isCancelled) {
                downloadViewModel.removeVideoFromDownloadingList(clip)
            }

            twitchViewModel.getUserClips(twitchViewModel.currentUserId())
        }

    }

    /**
     * Cancels the [VideoDownloadWorker] and removes the clip from the [DownloadViewModel]'s downloadingVideosList
     * @param clip: The clip which download will be canceled
     */
    private fun cancelDownloadWork(clip: ClipResponse) {
        workStateHandler.cancelDownloadWork(clip)
        downloadViewModel.removeVideoFromDownloadingList(clip)
    }

    /**
     * Resets the PlayerState
     */
    override fun onDetach() {
        super.onDetach()
        playerViewModel.changePlayerState(null)
    }
}
