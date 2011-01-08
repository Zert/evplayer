package com.zertsolutions.android.evplayer;

import _root_.android.util.Log
import _root_.android.content.SharedPreferences

object EvPlayerStorage {
  val LOG_TAG = "EvPlayer"

  var settings: SharedPreferences = _

  var uri: String = _

  def apply(s: SharedPreferences) {
    this.settings = s
    this.uri = settings.getString("uri", "rtsp://localhost/")
  }

  def setUri(value: String) = {
    Log.d(LOG_TAG, "GetURI: " + value)
    this.uri = value
  }

  def getUri(): String = {
    return uri
  }

  def commit(): Boolean = {
    val editor = settings.edit()
    editor.putString("uri", uri)
    editor.commit()
  }
}
