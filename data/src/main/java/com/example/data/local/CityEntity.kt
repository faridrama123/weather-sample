package com.example.data.local

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "city")
data class CityEntity (

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    var id : Int,

    @ColumnInfo(name = "country")
    var country : String,


    @ColumnInfo(name = "name")
    var name : String,

    @ColumnInfo(name = "sunrise")
    var sunrise : Int,


    @ColumnInfo(name = "sunset")
    var sunset : Int,



)