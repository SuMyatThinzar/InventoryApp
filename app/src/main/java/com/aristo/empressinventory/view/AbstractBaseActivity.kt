package com.aristo.empressinventory.view

import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.aristo.empressinventory.data.model.FirebaseRealtimeModel
import com.aristo.empressinventory.data.model.FirebaseRealtimeModelImpl

abstract class AbstractBaseActivity<VB: ViewBinding> : AppCompatActivity()  {

    protected lateinit var binding: VB
    protected val mFirebaseRealtimeModel: FirebaseRealtimeModel by lazy { FirebaseRealtimeModelImpl }
}