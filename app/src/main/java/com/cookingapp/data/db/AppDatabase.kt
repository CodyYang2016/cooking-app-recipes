package com.example.cookingapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.cookingapp.data.dao.CookSessionDao
import com.example.cookingapp.data.dao.PantryDao
import com.example.cookingapp.data.dao.RecipeDao
import com.example.cookingapp.data.entity.*

class Converters {
    @TypeConverter fun fromSourceType(v: SourceType) = v.name
    @TypeConverter fun toSourceType(v: String) = SourceType.valueOf(v)
    @TypeConverter fun fromUsageStatus(v: UsageStatus) = v.name
    @TypeConverter fun toUsageStatus(v: String) = UsageStatus.valueOf(v)
    @TypeConverter fun fromAdjReason(v: AdjustmentReason) = v.name
    @TypeConverter fun toAdjReason(v: String) = AdjustmentReason.valueOf(v)
}

@Database(
    entities = [
        PantryItemEntity::class,
        RecipeEntity::class,
        RecipeIngredientEntity::class,
        RecipeStepEntity::class,
        CookSessionEntity::class,
        CookIngredientUsageEntity::class,
        InventoryAdjustmentEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pantryDao(): PantryDao
    abstract fun recipeDao(): RecipeDao
    abstract fun cookSessionDao(): CookSessionDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cooking_app.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
    }
}