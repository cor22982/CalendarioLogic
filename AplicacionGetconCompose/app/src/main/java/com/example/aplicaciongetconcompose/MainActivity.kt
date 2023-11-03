package com.example.aplicaciongetconcompose

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aplicaciongetconcompose.ui.theme.AplicacionGetconComposeTheme

class MainActivity : ComponentActivity() {
    private var currentActivity: Activity? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        currentActivity = this
        super.onCreate(savedInstanceState)
        setContent {
            GetEvent(currentActivity as MainActivity)
        }
    }
}

@Composable
fun GetEvent(activity: MainActivity) {

    val lanzador = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { resultado ->
    }
    val contexto = LocalContext.current
    var eventosee by remember { mutableStateOf("Nombre/Fecha/Hora") }
    // Compose the UI
    Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {

        Text("OBTENER EVENTO", style = TextStyle(
            fontSize = 40.sp, fontWeight = FontWeight.Bold)
        )

        Button(
            onClick = {
                val calendarioGetandSet = CalendarioLogic(contexto)
                activity?.let { calendarioGetandSet.getResultsFromApi(it) }

            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "GETEVENT")
        }

        Button(
            onClick = {
                try {
                    val navegacion = Intent(contexto, ToSeet::class.java)
                    lanzador.launch(navegacion)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "GOTOSET")
        }

        Text(eventosee, style = TextStyle(
            fontSize = 20.sp, fontWeight = FontWeight.Bold)
        )



    }
}
