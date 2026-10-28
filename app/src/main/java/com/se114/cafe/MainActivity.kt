package com.se114.cafe

//import android.R
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import com.se114.cafe.feature.auth.LoginScreen
import com.se114.cafe.navigation.AppNavGraph

//@Composable
//fun GreetingScreen(){
//    var count by remember { mutableIntStateOf(0)}
//    Column(modifier = Modifier.fillMaxSize().padding(10.dp).background(color = Color.Yellow),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center) {
//        Box(
//            modifier = Modifier.fillMaxWidth().background(color = Color.Blue)
//        ){
//            Text("Tieu de")
//        }
//        Spacer(modifier = Modifier.height(30.dp))
//
//        Text("$count")
//
//        Spacer(modifier = Modifier.height(30.dp))
//        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)){
//            Button(onClick = {
//                count++;
//            }) {
//                Text(text = "plus")
//            }
//
//            Button(onClick = {
//                if (count > 0) {
//                    count--; }
//            }){
//                Text(text = "minus")
//            }
//        }
//    }
//}

@Composable
fun Login(){
    var email by remember { mutableStateOf("")}
    var password by remember { mutableStateOf("")}
    var message by remember { mutableStateOf("")}
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.DarkGray)
                .height(30.dp)
                .padding(8.dp)
        ){
            Text("Cafe")
        }
        OutlinedTextField(
            value = email,
            onValueChange = { email = it},
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it},
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Button(
            onClick = {
                message = if (email.isNotBlank() && password.isNotBlank()){
                    "OK"
                }
                else{
                    "Check it!"
                }
            }
        ) {
            Text("Login")
        }
        Text(text = message)
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingScreenPreview() {
    Login()
}

class MainActivity : ComponentActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        setContent {
            Login()
        }
    }
}