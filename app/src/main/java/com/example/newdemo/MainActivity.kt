package com.example.newdemo

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.newdemo.ComposablePackage.WelcomePage
import com.example.newdemo.ui.theme.NewDemoTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NewDemoTheme {
                val navController = rememberNavController()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ApiCall()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiCall(){

    var api1Data by remember { mutableStateOf<String?>(null) }
    var api2Data by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false)}

    val coroutineScope = rememberCoroutineScope()

    Scaffold {paddingValues ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
            Button(onClick = {
                isLoading = true
                coroutineScope.launch {
                    try {
                        //thread 1(UI Thread), 2(fetchApi1()), 3(fetchApi2())
                        val api1Call = async { fetchApi1() } //this call the api
                        val api2Call = async { fetchApi2() }

                        api1Data = api1Call.await()
                        api2Data = api2Call.await()

                    } catch (e: Exception){
                        e.printStackTrace()
                    } finally {
                        isLoading = false
                    }
                }
            }) {
                Text(text = "Call Apis")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if(isLoading){
                CircularProgressIndicator()
            }else{
                Text(text = "Api Result 1: ${api1Data ?: "Not Visible"}")
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Api Result 2: ${api2Data ?: "Not Visible"}")

            }
        }
        
    }

}


suspend fun fetchApi1(): String{
    delay(1000)
    return "Data of Api 1"
}

suspend fun fetchApi2(): String{
    delay(1000)
    return "Data of Api 2"
}

@Preview
@Composable
fun PreviewApiScreen(){
    ApiCall()
}

@Composable
fun MyCompose(){
    val scope = rememberCoroutineScope()
    val updatedData by remember{ mutableStateOf("Waiting...") }
    
    LaunchedEffect(Unit){
        scope.launch(Dispatchers.Main) {
            Log.d("DispacherThread", "MyCompose: ${Thread.currentThread().name}")
        }

        scope.launch(Dispatchers.IO) {
            Log.d("DispacherThread", "MyCompose: ${Thread.currentThread().name}")
        }
        scope.launch(Dispatchers.Default) {
            Log.d("DispacherThread", "MyCompose: ${Thread.currentThread().name}")
        }
        scope.launch(Dispatchers.Unconfined) {
            Log.d("DispacherThread", "MyCompose: ${Thread.currentThread().name}")
        }
    }

}

@Preview
@Composable
fun ComposePagePreview() {
    NewDemoTheme {
        MyCompose()
    }
}

@Composable
fun AnitaNavHost(navController: NavHostController){
    NavHost(navController = navController, startDestination = "login"){
        composable("login"){ LoginPage(navController)}
        composable("welcome/{username}"){ anita ->
            val username = anita.arguments?.getString("username")
            username.let {
                if (it != null) {
                    WelcomePage(username = it)
                }
            }
        }
    }
}


@Composable
fun LoginPage(navController: NavHostController) {
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val isValid = remember { mutableStateOf(true) }
    val loginSuccessful = remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Image(
            painter = painterResource(id = R.drawable.mango_mastani),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f)) // Dark overlay for background image
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Login",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(20.dp))

            // Username Field with Icon and Hint
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.7f), shape = MaterialTheme.shapes.small)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Username Icon",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    BasicTextField(
                        value = username.value,
                        onValueChange = { username.value = it },
                        textStyle = TextStyle(fontSize = 18.sp, color = Color.Black),
                        modifier = Modifier.weight(1f) // Allow TextField to take remaining space
                    )

                    // Username Hint
                    if (username.value.isEmpty()) {
                        Text(
                            text = "Username",
                            color = Color.Gray,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(start = 8.dp) // Add space between icon and hint
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Password Field with Icon and Hint
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.7f), shape = MaterialTheme.shapes.small)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "Password Icon",
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    BasicTextField(
                        value = password.value,
                        onValueChange = { password.value = it },
                        textStyle = TextStyle(fontSize = 18.sp, color = Color.Black),
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.weight(1f) // Allow TextField to take remaining space
                    )

                    // Password Hint
                    if (password.value.isEmpty()) {
                        Text(
                            text = "Password",
                            color = Color.Gray,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(start = 8.dp) // Add space between icon and hint
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Login Button with Validation
            Button(
                onClick = {
                    if (username.value == "Anita" && password.value == "Anita") {
                        isValid.value = true
                        loginSuccessful.value = true // Set login success
                        //Navigation
                        navController.navigate("welcome/${username.value}")
                    } else {
                        isValid.value = false
                        loginSuccessful.value = false
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Login")
            }

            if (loginSuccessful.value) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Login Successful!",
                    color = Color.Green,
                    fontSize = 14.sp
                )

                // Show Toast
                LaunchedEffect(Unit) {
                    Toast.makeText(
                        context,
                        "Login Successful",
                        Toast.LENGTH_SHORT
                    ).show() // Show Toast
                }
            } else if (!isValid.value) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Invalid username or password",
                    color = Color.Red,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPagePreview() {
    NewDemoTheme {
        val navController = rememberNavController()
        LoginPage(navController)
    }
}
