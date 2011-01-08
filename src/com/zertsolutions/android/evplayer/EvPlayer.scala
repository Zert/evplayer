package com.zertsolutions.android.evplayer

import _root_.android.app.Activity
import _root_.android.media.AudioManager
import _root_.android.media.MediaPlayer
import _root_.android.media.MediaPlayer.OnBufferingUpdateListener
import _root_.android.media.MediaPlayer.OnCompletionListener
import _root_.android.media.MediaPlayer.OnPreparedListener
import _root_.android.media.MediaPlayer.OnVideoSizeChangedListener
import _root_.android.os.Bundle
import _root_.android.util.Log
import _root_.android.content.Context
import _root_.android.content.SharedPreferences
import _root_.android.view.View
import _root_.android.view.SurfaceHolder
import _root_.android.view.SurfaceView
import _root_.android.widget.Toast
import _root_.android.widget.EditText
import _root_.android.widget.Button


class EvPlayer extends Activity
with View.OnClickListener
with OnBufferingUpdateListener
with OnCompletionListener
with OnPreparedListener
with OnVideoSizeChangedListener
with SurfaceHolder.Callback {

  private val TAG = "EvPlayer"
  private val PREFS_NAME = "evplayer"

  private var mVideoWidth: Int = _
  private var mVideoHeight: Int = _
  private var mMediaPlayer: MediaPlayer = _
  private var mPreview: SurfaceView = _
  private var holder: SurfaceHolder = _
  private var path: String = _
  private var extras: Bundle = _
  private var mUri: EditText = _
  private var mUriButton: Button = _

  private var mIsVideoSizeKnown = false
  private var mIsVideoReadyToBePlayed = false

  override def onCreate(icicle: Bundle) : Unit = {
    super.onCreate(icicle)
    setContentView(R.layout.main)

    val settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    EvPlayerStorage(settings)

    mUri = findViewById(R.id.edtInput).asInstanceOf[EditText]
    mUri.setText(EvPlayerStorage.getUri)
    mUriButton = findViewById(R.id.button).asInstanceOf[Button]
    mUriButton.setOnClickListener(this)
  }

  override def onStop() = {
    Log.d(TAG, "**** onStop()")
    super.onStop()
    EvPlayerStorage.commit()
  }

  private def hideInput() = {
    mUri.setVisibility(View.GONE)
    mUriButton.setVisibility(View.GONE)
  }

  private def playVideo() = {
    doCleanUp()
    try {
      path = EvPlayerStorage.getUri()
      Log.d(TAG, "Play: '" + path + "'")
      if (path == "") {
        Toast.makeText(
          EvPlayer.this,
          "Please set any valid video stream URI.",
          Toast.LENGTH_LONG).show()
      } else {
        mMediaPlayer = new MediaPlayer()
        mMediaPlayer.setDataSource(path)
        mMediaPlayer.setDisplay(holder)
        mMediaPlayer.prepare()
        mMediaPlayer.setOnBufferingUpdateListener(this)
        mMediaPlayer.setOnCompletionListener(this)
        mMediaPlayer.setOnPreparedListener(this)
        mMediaPlayer.setOnVideoSizeChangedListener(this)
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
      }
    } catch {
      case e => {
        Log.e(TAG, "error: " + e.getMessage(), e)
      }
    }
  }

  def onBufferingUpdate(arg0: MediaPlayer, percent: Int) = {
    Log.d(TAG, "onBufferingUpdate percent:" + percent)
  }

  def onCompletion(arg0: MediaPlayer) = {
    Log.d(TAG, "onCompletion called")
  }

  def onVideoSizeChanged(mp: MediaPlayer, width: Int, height: Int) = {
    Log.v(TAG, "onVideoSizeChanged called")
    if (width == 0 || height == 0) {
      Log.e(TAG, "invalid video width(" + width + ") or height(" + height + ")")
    } else {
      mIsVideoSizeKnown = true
      mVideoWidth = width
      mVideoHeight = height
      if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
        startVideoPlayback()
      }
    }
  }

  def onPrepared(mediaplayer: MediaPlayer) = {
    Log.d(TAG, "onPrepared called")
    mIsVideoReadyToBePlayed = true
    if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
      startVideoPlayback()
    }
  }

  def surfaceChanged(surfaceholder: SurfaceHolder, i: Int, j: Int, k: Int) = {
    Log.d(TAG, "surfaceChanged called")
  }

  def surfaceDestroyed(surfaceholder: SurfaceHolder) = {
    Log.d(TAG, "surfaceDestroyed called")
  }


  def surfaceCreated(holder: SurfaceHolder) = {
    Log.d(TAG, "surfaceCreated called")
    playVideo()
  }


  def onClick(v: View) = {
    Log.d("EvPlayer VIEW", "onClick " + v.getId() +
          "URI: " + mUri.getText().toString())
    EvPlayerStorage.setUri(mUri.getText().toString())
    EvPlayerStorage.commit()
    hideInput()
    doPlayBack()
  }

  override def onPause() = {
    super.onPause()
    releaseMediaPlayer()
    doCleanUp()
  }

  override def onDestroy() = {
    super.onDestroy()
    releaseMediaPlayer()
    doCleanUp()
  }

  private def releaseMediaPlayer() = {
    if (mMediaPlayer != null) {
      mMediaPlayer.release()
      mMediaPlayer = null
    }
  }

  private def doCleanUp() = {
    mVideoWidth = 0
    mVideoHeight = 0
    mIsVideoReadyToBePlayed = false
    mIsVideoSizeKnown = false
  }

  private def doPlayBack() = {
    mPreview = findViewById(R.id.surface).asInstanceOf[SurfaceView]
    mPreview.setVisibility(View.VISIBLE)
    holder = mPreview.getHolder()
    holder.addCallback(this)
    holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
    extras = getIntent().getExtras()
  }

  private def startVideoPlayback() = {
    Log.v(TAG, "startVideoPlayback")
    holder.setFixedSize(mVideoWidth, mVideoHeight)
    mMediaPlayer.start()
  }
}
