package controllers

import play.api.mvc.Call

class ReverseWebJarAssets {
  def at(path: String): Call = {
    new Call("GET", "/webjars/" + path)
  }
}