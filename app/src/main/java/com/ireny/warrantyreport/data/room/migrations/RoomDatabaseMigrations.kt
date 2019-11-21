package com.ireny.warrantyreport.data.room.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE Report ADD COLUMN code TEXT DEFAULT NULL")
        database.execSQL(
            "ALTER TABLE Report ADD COLUMN  code_generated_at INTEGER DEFAULT NULL")
    }
}