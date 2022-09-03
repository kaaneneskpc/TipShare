@file:OptIn(ExperimentalComposeUiApi::class)

package com.example.tipshareapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Slider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tipshareapp.MainActivity.Companion.SPLIT_STATE_DEFAULT_VALUE
import com.example.tipshareapp.MainActivity.Companion.STEPS
import com.example.tipshareapp.components.InputField
import com.example.tipshareapp.components.RoundIconButton
import com.example.tipshareapp.ui.theme.TipShareAppTheme
import com.example.tipshareapp.utils.calculateTotalPerPerson
import com.example.tipshareapp.utils.calculateTotalTip

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TipShareAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colors.background
                ) {
                    TipShareApp()
                }
            }
        }
    }
    companion object {
        const val STEPS = 5
        const val SPLIT_STATE_DEFAULT_VALUE = 1
        const val TIP_PERCENTAGE_DEFAULT_VALUE = 0.0

    }
}



@Composable
fun TipShareApp() {
    MainContent()
}

@Composable
fun TopHeader(totalPerPerson: Double = 134.0) {
    val total = "%.2f".format(totalPerPerson)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(10.dp)
            .clip(shape = CircleShape.copy(all = CornerSize(12.dp))),
            color = Color(0xFFE9D7F7)
        ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(stringResource(id = R.string.top_Header_Tip_Name), style = MaterialTheme.typography.h5)
            Text("$$total", style = MaterialTheme.typography.h4, fontWeight = FontWeight.ExtraBold)
        }
    }
}


@Composable
fun MainContent() {
    val splitByState = remember { mutableStateOf(SPLIT_STATE_DEFAULT_VALUE) }
    val tipAmountState = remember { mutableStateOf(0.0) }
    val totalPerPersonState = remember { mutableStateOf(0.0) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        BillForm(splitByState = splitByState, tipAmountState = tipAmountState, totalPerPersonState = totalPerPersonState)
    }
}

@Composable
fun BillForm(
    modifier: Modifier = Modifier,
    range: IntRange = 1..100,
    splitByState: MutableState<Int>,
    tipAmountState: MutableState<Double>,
    totalPerPersonState: MutableState<Double>,
    onValueChange:(String) -> Unit = {}
) {
    val totalBillState = remember { mutableStateOf("") }
    val validState = remember (totalBillState.value) { totalBillState.value.trim().isNotEmpty() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val sliderPositionState = remember { mutableStateOf(0f) }
    val tipPercentage = (sliderPositionState.value * 100).toInt()


    TopHeader(totalPerPerson = totalPerPersonState.value)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(5.dp),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        border = BorderStroke(width = 1.dp, color = Color.LightGray)
    ) {
        Column(modifier = Modifier.padding(6.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start) {
            InputField(
                valueState = totalBillState ,
                labelId = stringResource(id = R.string.input_Field_Label_Text),
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions {
                    if(!validState) return@KeyboardActions
                    onValueChange(totalBillState.value.trim())
                    keyboardController?.hide()
                }
            )
            if(validState) {
                Row(modifier = Modifier.padding(3.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Split")
                    Spacer(modifier = Modifier.width(120.dp))
                    Row(modifier = Modifier.padding(horizontal = 10.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically) {
                        RoundIconButton(imageVector = Icons.Default.Remove, onClick = {
                           splitByState.value = if(splitByState.value > 1) splitByState.value - 1 else SPLIT_STATE_DEFAULT_VALUE
                            totalPerPersonState.value = calculateTotalPerPerson(totalBill = totalBillState.value.toDouble(), splitBy = splitByState.value, tipPercentage = tipPercentage)
                        })
                        Text(text = "${splitByState.value}")
                        RoundIconButton(imageVector = Icons.Default.Add, onClick = {
                            if(splitByState.value < range.last) {
                                splitByState.value = splitByState.value + 1
                                totalPerPersonState.value = calculateTotalPerPerson(totalBill = totalBillState.value.toDouble(), splitBy = splitByState.value, tipPercentage = tipPercentage)
                            }
                        })
                    }
                }
                
                //Tip Row
                Row(modifier = Modifier.padding(horizontal = 3.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Tip")
                    Spacer(modifier = Modifier.width(200.dp))
                    Text(text = "$ ${tipAmountState.value}")
                }
                Column(verticalArrangement = Arrangement.Center,
                       horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "% $tipPercentage")
                    Spacer(modifier = Modifier.height(14.dp))
                    //Slider
                    Slider(value = sliderPositionState.value, onValueChange = { newValue ->
                          sliderPositionState.value = newValue
                          tipAmountState.value = calculateTotalTip(totalBill = totalBillState.value.toDouble(), tipPercentage = tipPercentage)
                        totalPerPersonState.value = calculateTotalPerPerson(totalBill = totalBillState.value.toDouble(), splitBy = splitByState.value, tipPercentage = tipPercentage)
                    }, modifier = Modifier.padding(start = 16.dp, end = 16.dp), steps = STEPS, onValueChangeFinished = {
                    })
                }
            }
        }
    }
}








