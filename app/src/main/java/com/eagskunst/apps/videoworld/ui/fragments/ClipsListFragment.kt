package com.eagskunst.apps.videoworld.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.work.*
import com.eagskunst.apps.videoworld.R
import com.eagskunst.apps.videoworld.app.models.PlayerState
import com.eagskunst.apps.videoworld.app.network.responses.clips.ClipResponse
import com.eagskunst.apps.videoworld.app.network.responses.clips.UserClipsResponse
import com.eagskunst.apps.videoworld.app.workers.VideoDownloadWorker
import com.eagskunst.apps.videoworld.databinding.FragmentClipsBinding
import com.eagskunst.apps.videoworld.progressBar
import com.eagskunst.apps.videoworld.ui.view_holders.clipInfoView
import com.eagskunst.apps.videoworld.utils.DownloadState
import com.eagskunst.apps.videoworld.utils.activityViewModel
import com.eagskunst.apps.videoworld.utils.base.BaseFragment
import com.eagskunst.apps.videoworld.utils.injector
import com.eagskunst.apps.videoworld.utils.setDivider
import com.eagskunst.apps.videoworld.viewmodels.DownloadViewModel
import com.eagskunst.apps.videoworld.viewmodels.PlayerViewModel
import com.eagskunst.apps.videoworld.viewmodels.TwitchViewModel
import timber.log.Timber

class ClipsListFragment : BaseFragment<FragmentClipsBinding>(R.layout.fragment_clips) {

    override val bindingFunction: (view: View) -> FragmentClipsBinding
        get() = FragmentClipsBinding::bind

    private val twitchViewModel: TwitchViewModel by activityViewModel {
        injector.twitchViewModel
    }
    private val downloadViewModel: DownloadViewModel by activityViewModel() {
        injector.downloadViewModel
    }
    private val playerViewModel: PlayerViewModel by activityViewModels()

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
        binding.clipsRv.setDivider(R.drawable.divider)
    }

    private fun handleDownloadAction(downloadState: Int, clip: ClipResponse) {
        when (downloadState) {
            DownloadState.NOT_DOWNLOADED -> startDownloadWork(clip)
            DownloadState.DOWNLOADING -> cancelDownloadWork(clip)
            DownloadState.DOWNLOADED -> downloadViewModel.deleteClipInFiles(clip)
        }
        binding.clipsRv.requestModelBuild()
    }

    private fun startDownloadWork(clip: ClipResponse) {
        val url = downloadViewModel.getDownloadUrlOfClip(clip)
        Timber.d("Computed video url for download: $url")

        val data = Data.Builder()
            .putString(VideoDownloadWorker.VIDEO_URL, url)
            .putString(VideoDownloadWorker.DESIRED_FILENAME, clip.getClipFilename())
            .putString(VideoDownloadWorker.CLIP_TITLE, clip.title)
            .build()
        val request = OneTimeWorkRequestBuilder<VideoDownloadWorker>()
            .setInputData(data)
            .addTag(clip.getClipFilename())
            .build()

        downloadViewModel.addVideoToDownloadList(clip)

        WorkManager.getInstance(requireContext())
            .enqueue(request)

        WorkManager.getInstance(requireContext())
            .getWorkInfosByTagLiveData(clip.getClipFilename())
            .observe(requireActivity(), Observer { worksInfo ->
                if (worksInfo == null)
                    return@Observer

                val work = worksInfo[0]
                if (work.state == WorkInfo.State.SUCCEEDED){
                    downloadViewModel.removeVideoFromDownloadList(clip)
                    downloadViewModel.updateDownloadedVideosList(clip)
                    twitchViewModel.getUserClips(twitchViewModel.currentUserId())
                }
                else if(work.state == WorkInfo.State.CANCELLED) {
                    cancelDownloadWork(clip)
                    twitchViewModel.getUserClips(twitchViewModel.currentUserId())
                }
            })

    }

    private fun cancelDownloadWork(clip: ClipResponse) {

        WorkManager.getInstance(requireContext())
            .cancelAllWorkByTag(clip.getClipFilename())

        downloadViewModel.removeVideoFromDownloadList(clip)
    }

    override fun onDetach() {
        super.onDetach()
        playerViewModel.changePlayerState(null)
    }
}
