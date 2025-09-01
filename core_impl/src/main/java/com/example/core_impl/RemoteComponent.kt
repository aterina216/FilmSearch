package com.example.core_impl

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [RemoteModule::class])
interface RemoteComponent : RemoteProvider