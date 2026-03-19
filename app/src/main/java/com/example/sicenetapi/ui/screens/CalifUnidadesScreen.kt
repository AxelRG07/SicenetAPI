package com.example.sicenetapi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sicenetapi.data.local.CalifUnidadesEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MateriaUnidadesCard(materia: CalifUnidadesEntity) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {

            Text(
                text = materia.materia,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Grupo: ${materia.grupo} | Unidades: ${materia.unidadesActivas}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(12.dp))

            val calificaciones = listOf(
                materia.c1, materia.c2, materia.c3, materia.c4, materia.c5,
                materia.c6, materia.c7, materia.c8, materia.c9, materia.c10,
                materia.c11, materia.c12, materia.c13
            )

            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (i in 0 until materia.unidadesActivas) {
                    val califTexto = calificaciones[i]
                    val califNum = califTexto.toIntOrNull() ?: 0

                    val colorFondo = when {
                        califNum == 0 -> Color.LightGray.copy(alpha = 0.5f)
                        califNum >= 70 -> Color(0xFFE8F5E9)
                        else -> Color(0xFFFFEBEE)
                    }
                    val colorTexto = when {
                        califNum == 0 -> Color.DarkGray
                        califNum >= 70 -> Color(0xFF2E7D32)
                        else -> MaterialTheme.colorScheme.error
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(colorFondo)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "U${i + 1}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                        Text(
                            text = if (califNum == 0) "-" else califTexto,
                            fontWeight = FontWeight.Bold,
                            color = colorTexto
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CalifUnidadesScreen(viewModel: MainViewModel) {
    val listaUnidades by viewModel.califUnidadesLocal.collectAsState()

    LaunchedEffect(listaUnidades) {
        if (listaUnidades.isNullOrEmpty()) {
            viewModel.sincronizarCalifUnidades()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Calificaciones Parciales",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        if (listaUnidades.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "No hay datos parciales locales.", color = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.sincronizarCalifUnidades() }) {
                        Text("Descargar Parciales")
                    }
                }
            }
        } else {
            val fechaFormateada = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                .format(Date(listaUnidades.first().fechaSincronizacion))

            Text(
                text = "Última actualización: $fechaFormateada",
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(listaUnidades) { unidadesEntity ->
                    MateriaUnidadesCard(materia = unidadesEntity)
                }
            }
        }
    }
}