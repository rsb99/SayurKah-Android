package com.reresb.sayurkah.data.local.room

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.reresb.sayurkah.R
import com.reresb.sayurkah.data.local.entity.VegetableEntity
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.concurrent.Executors

@Database(entities = [VegetableEntity::class], version = 1, exportSchema = false)
abstract class VegetableDatabase: RoomDatabase() {

    abstract fun vegetableDao(): VegetableDao

    companion object{
        private const val ERROR_MSG = "error_msg"

        @Volatile
        private var INSTANCE: VegetableDatabase? = null

        fun getInstance(context: Context): VegetableDatabase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VegetableDatabase::class.java,
                    "vegetables.db"
                ).addCallback(object : Callback(){
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        INSTANCE?.let {
                            Executors.newSingleThreadExecutor().execute {
                                starterData(context.applicationContext, it.vegetableDao())
                            }
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }

        private fun starterData(context: Context, dao: VegetableDao){
            val vegetable = loadVegetableJson(context)

            try {
                if (vegetable != null){
                    for (i in 0 until vegetable.length()){
                        val item = vegetable.getJSONObject(i)

                        dao.insertData(
                            VegetableEntity(
                                item.getInt("id"),
                                item.getString("name"),
                                item.getString("picture")
                            )
                        )
                    }
                }
            } catch (exception: JSONException){
                Log.d(ERROR_MSG, "JSONException = ${exception.message.toString()}")
            }
        }

        private fun loadVegetableJson(context: Context): JSONArray?{
            val builder = StringBuilder()
            val `in` = context.resources.openRawResource(R.raw.vegetables)
            val reader = BufferedReader(InputStreamReader(`in`))
            var line: String?

            try {
                while (reader.readLine().also { line = it } != null){
                    builder.append(line)
                }
                val json = JSONObject(builder.toString())
                return json.getJSONArray("vegetables")
            } catch (exception: IOException){
                Log.d(ERROR_MSG, "DB LOCAL IOException = ${exception.message.toString()}")
            } catch (exception: JSONException){
                Log.d(ERROR_MSG, "DB LOCAL JSONException = ${exception.message.toString()}")
            }
            return null
        }
    }

}