package com.example.homewokoutapp.components

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.PlayerSurface

@Composable
fun WorkoutVideoPlayer(videoResId: Int?, currentIndex: Int, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val exoPlayer = remember(videoResId, currentIndex) {  // Key-ek a frissítéshez
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ONE  // Loop
        }
    }

    DisposableEffect(videoResId, currentIndex) {
        if (videoResId != null) {
            val uri = Uri.parse("android.resource://${context.packageName}/$videoResId")
            val mediaItem = MediaItem.fromUri(uri)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.seekTo(0)
            exoPlayer.playWhenReady = true
        }
        onDispose {
            exoPlayer.release()
        }
    }

    Box(modifier = modifier.size(200.dp)) {
        PlayerSurface(
            player = exoPlayer,
            modifier = Modifier.matchParentSize()
        )
    }
}