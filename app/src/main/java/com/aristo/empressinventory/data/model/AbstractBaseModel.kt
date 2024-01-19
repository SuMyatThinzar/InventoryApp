package com.aristo.admin.data.model

import com.aristo.empressinventory.network.FirebaseRealtimeApi
import com.aristo.empressinventory.network.FirebaseRealtimeApiImpl

abstract class AbstractBaseModel {
    val mFirebaseRealtimeApi : FirebaseRealtimeApi = FirebaseRealtimeApiImpl
}