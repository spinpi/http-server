package com.spinpi.http.inject

import com.google.inject.{Guice, Injector, Module}

import scala.collection.mutable.ArrayBuffer

trait InjectApp {
  private val modules: ArrayBuffer[Module] = ArrayBuffer[Module]()
  private var _injector: Injector          = _

  def registerModules(modules: Module*): Unit = {
    this.modules ++= modules
  }

  def injector: Injector = {
    if (_injector == null) {
      _injector = Guice.createInjector(modules: _*)
    }
    _injector
  }
}
