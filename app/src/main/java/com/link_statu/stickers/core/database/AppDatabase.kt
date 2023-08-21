package com.link_statu.stickers.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.link_statu.stickers.core.utils.room.ListStringConverter
import com.link_statu.stickers.features.sticker.models.StickerPackEntity
import com.link_statu.stickers.features.sticker.repository.local.StickersDAO

@Database(
    entities = [
        StickerPackEntity::class,
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(value = [ListStringConverter::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun stickerEntityDao(): StickersDAO

    companion object {
        @Volatile
        private var INSTANCE: com.link_statu.stickers.core.database.AppDatabase? = null

        fun getAppDatabase(context: Context): com.link_statu.stickers.core.database.AppDatabase = com.link_statu.stickers.core.database.AppDatabase.Companion.INSTANCE
            ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                com.link_statu.stickers.core.database.AppDatabase::class.java,
                "waStickersDB"
            )
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()
            com.link_statu.stickers.core.database.AppDatabase.Companion.INSTANCE = instance
            instance
        }
    }
}
