package com.reresb.sayurkah.data.local.room

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.reresb.sayurkah.data.local.entity.VegetableEntity

@Dao
interface VegetableDao {

    @Query("SELECT * FROM table_vegetables ORDER BY name ASC")
    fun getVegetables(): DataSource.Factory<Int, VegetableEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertData(vararg vegetableEntity: VegetableEntity)

}