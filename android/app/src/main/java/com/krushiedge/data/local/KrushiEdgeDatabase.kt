package com.krushiedge.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.krushiedge.data.local.dao.AdvisoryDao
import com.krushiedge.data.local.dao.FarmDao
import com.krushiedge.data.local.dao.FieldDao
import com.krushiedge.data.local.dao.MandiDao
import com.krushiedge.data.local.dao.OfflineSyncDao
import com.krushiedge.data.local.dao.ScanRecordDao
import com.krushiedge.data.local.dao.UserDao
import com.krushiedge.data.local.dao.WeatherDao
import com.krushiedge.data.local.entity.AdvisoryEntity
import com.krushiedge.data.local.entity.FarmEntity
import com.krushiedge.data.local.entity.FieldEntity
import com.krushiedge.data.local.entity.MandiPriceEntity
import com.krushiedge.data.local.entity.OfflineSyncQueueEntity
import com.krushiedge.data.local.entity.ScanRecordEntity
import com.krushiedge.data.local.entity.UserEntity
import com.krushiedge.data.local.entity.WeatherCacheEntity

@Database(
    entities = [
        FarmEntity::class,
        FieldEntity::class,
        ScanRecordEntity::class,
        AdvisoryEntity::class,
        OfflineSyncQueueEntity::class,
        MandiPriceEntity::class,
        UserEntity::class,
        WeatherCacheEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class KrushiEdgeDatabase : RoomDatabase() {
    abstract fun farmDao(): FarmDao
    abstract fun fieldDao(): FieldDao
    abstract fun scanRecordDao(): ScanRecordDao
    abstract fun advisoryDao(): AdvisoryDao
    abstract fun offlineSyncDao(): OfflineSyncDao
    abstract fun mandiDao(): MandiDao
    abstract fun userDao(): UserDao
    abstract fun weatherDao(): WeatherDao
}
