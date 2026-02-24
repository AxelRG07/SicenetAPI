package com.example.sicenetapi.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sicenetapi.data.local.KardexEntity
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun KardexScreen(viewModel: MainViewModel) {
    val listaKardex by viewModel.kardexLocal.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Text(
            text = "Kardex del Alumno",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        if (listaKardex.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No hay datos del Kardex localmente.",
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.sincronizarKardex() }) {
                        Text("Descargar Kardex")
                    }
                }
            }
        } else {
            val fechaFormateada = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                .format(Date(listaKardex.first().fechaSincronizacion))

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
                items(listaKardex) { materiaKardex ->
                    MateriaKardexCard(materia = materiaKardex)
                }
            }
        }
    }
}


@Composable
fun MateriaKardexCard(materia: KardexEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(0.7f)) {
                Text(
                    text = materia.materia,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Clave: ${materia.claveMateria} | Créditos: ${materia.creditos}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Periodo: ${materia.periodo}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            val califNum = materia.calificacion.toIntOrNull() ?: 0
            val colorCalif = if (califNum >= 70) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error

            Column(
                modifier = Modifier.weight(0.3f),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = materia.calificacion,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = colorCalif
                )
                Text(
                    text = "Calificación",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }
    }
}