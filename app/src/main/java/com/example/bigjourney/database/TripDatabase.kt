package com.example.bigjourney.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.bigjourney.model.Trip

@Database(entities = [Trip::class], version = 1, exportSchema = true)
@TypeConverters(Converters::class)
abstract class TripDatabase : RoomDatabase(){

    companion object{
        const val NAME = "Trip_DB"
    }

    abstract fun getTripDao() : TripDao
    


}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Προσθήκη της νέας στήλης στη βάση
        db.execSQL("ALTER TABLE Trip ADD COLUMN new_column TEXT DEFAULT 'undefined'")
    }
}

val MIGRATION_2_1 = object : Migration(2, 1) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Αφαίρεση της στήλης ή άλλης αλλαγής που έχει γίνει
        // Πχ, να αφαιρέσουμε την στήλη 'new_column' αν την προσθέσαμε
        db.execSQL("CREATE TABLE Trip_new AS SELECT tid, location, start_date, end_date FROM Trip")
        db.execSQL("DROP TABLE Trip")
        db.execSQL("ALTER TABLE Trip_new RENAME TO Trip")
    }
}

