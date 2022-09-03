package com.example.tipshareapp.utils

import com.example.tipshareapp.MainActivity

fun calculateTotalPerPerson(totalBill: Double, splitBy: Int, tipPercentage: Int): Double {
    val bill = calculateTotalTip(totalBill = totalBill, tipPercentage = tipPercentage) + totalBill
    return (bill / splitBy)
}

fun calculateTotalTip(totalBill: Double, tipPercentage: Int): Double {
    return if(totalBill > 1 && totalBill.toString().isNotEmpty()) (totalBill * tipPercentage) / 100 else MainActivity.TIP_PERCENTAGE_DEFAULT_VALUE
}