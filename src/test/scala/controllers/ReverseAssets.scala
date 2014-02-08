package controllers

import play.api.mvc.Call

class ReverseAssets {
  def at(path: String): Call = {
    new Call("GET", "/assets/" + path)
  }
}