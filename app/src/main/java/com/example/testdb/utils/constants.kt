package com.example.testdb.utils

import android.graphics.Color
import com.example.testdb.database.DatabaseRepository
import com.example.testdb.modelsDb.WorkerDb
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import org.joda.time.LocalDate


lateinit var AUTH: FirebaseAuth
lateinit var CURRENT_ID:String
lateinit var REF_DATABASE: DatabaseReference
lateinit var BOTTOM_NAV: BottomNavigationView
lateinit var REPOSITORY: DatabaseRepository
lateinit var USER: WorkerDb


const val TYPE_FIREBASE = "type_database"

const val K_WEEK = "WEEK"
const val K_MONTH = "MONTH"
const val K_YEAR = "YEAR"

const val W_ID = "id"
const val W_ID_FOR_SORT= "idForSort"
const val W_BOSS_ID = "bossId"
const val W_EMAIL = "email"
const val W_PASSWORD = "password"
const val W_F_PASSWORD = "firstPassword"
const val W_F_NAME = "fName"
const val W_S_NAME = "sName"
const val W_T_NAME = "tName"
const val W_POSITION = "position"
const val W_IS_ADMIN = "admin"
const val W_USER_INFO = "userInfo"

const val C_NAME = "name"

const val I_TIME = "time"
const val I_TYPE = "type"
const val C_CAT = "cat"
const val C_NOTE = "note"
const val C_NUMBER = "number"

const val COST = "costs"
const val INCOME = "incomes"
const val M_COST = "mutCosts"
const val M_INCOME = "mutIncomes"
const val WORKERS = "workers"
const val CATEGORIES = "categories"
const val INFO = "info"


//Утилитарные функциия
fun currDate(): String{
    return LocalDate.now().toString()
}

fun exactDate(n:Int = 0): String{
    return LocalDate.now().minusDays(n).toString()
}

fun setupPieChartUtil(pieChart: PieChart): PieChart {
    pieChart.apply {
        isDrawHoleEnabled = true
        setUsePercentValues(false)
        setEntryLabelTextSize(18f)
        setDrawEntryLabels(false)
        setEntryLabelColor(Color.BLACK)
        centerText = "Траты"
        setCenterTextSize(24f)
        description?.isEnabled = false
        extraBottomOffset = 35f
    }

    val l: Legend = pieChart.legend
    l.apply {
        verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        textSize = 18f
        isWordWrapEnabled = true
        orientation = Legend.LegendOrientation.HORIZONTAL
        yOffset = 10f
        setDrawInside(true)
        mNeededHeight = 100f
        isEnabled = true
    }
    return pieChart
}

fun drawEmptyCircleUtil(curPieChart: PieChart):  PieChart{
    curPieChart.data = PieData(PieDataSet(arrayListOf(PieEntry(1f, "")), "Пусто")
        .apply { colors = listOf(Color.GRAY) }).apply { setDrawValues(false) }
    curPieChart.invalidate()
    return curPieChart
}
