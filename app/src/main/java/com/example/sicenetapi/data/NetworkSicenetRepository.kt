package com.example.sicenetapi.data

import android.util.Log
import com.example.sicenetapi.data.local.AlumnoDao
import com.example.sicenetapi.data.local.CalifFinalEntity
import com.example.sicenetapi.data.local.CargaAcademicaEntity
import com.example.sicenetapi.data.local.KardexEntity
import com.example.sicenetapi.network.SicenetApi
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

class NetworkSicenetRepository(
    private val api: SicenetApi,
    private val alumnoDao: AlumnoDao
) : SicenetRepository {

     override suspend fun login(matricula: String, contrasenia: String): Result<String> {
        val soapXml = """
            <?xml version="1.0" encoding="utf-8"?>
            <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
              <soap:Body>
                <accesoLogin xmlns="http://tempuri.org/">
                  <strMatricula>$matricula</strMatricula>
                  <strContrasenia>$contrasenia</strContrasenia>
                  <tipoUsuario>ALUMNO</tipoUsuario>
                </accesoLogin>
              </soap:Body>
            </soap:Envelope>
        """.trimIndent()

        return try {
            val response = api.login(soapXml)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error Login: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProfile(): Result<Alumno> {
        val soapXml = """
            <?xml version="1.0" encoding="utf-8"?>
            <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
              <soap:Body>
                <getAlumnoAcademicoWithLineamiento xmlns="http://tempuri.org/" />
              </soap:Body>
            </soap:Envelope>
        """.trimIndent()

        return try {
            val response = api.getProfile(soapXml)
            if (response.isSuccessful && response.body() != null) {

                val xml = response.body()!!

                val alumno = extraerDatos(xml)
                Log.d("datosAlumno", xml)

                Result.success(alumno)
            } else {
                Result.failure(Exception("Error Perfil: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCargaAcademica(): Result<List<CargaAcademicaEntity>> {
        val soapXml = """
            <?xml version="1.0" encoding="utf-8"?>
            <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
              <soap:Body>
                <getCargaAcademicaByAlumno xmlns="http://tempuri.org/" />
              </soap:Body>
            </soap:Envelope>
        """.trimIndent()

        return try {
            val response = api.getCargaAcademica(soapXml)
            if (response.isSuccessful && response.body() != null) {
                val xml = response.body()!!
                val jsonString = xml
                    .substringAfter("<getCargaAcademicaByAlumnoResult>")
                    .substringBefore("</getCargaAcademicaByAlumnoResult>")

                val jsonArray = JSONArray(jsonString)
                val listaMaterias = mutableListOf<CargaAcademicaEntity>()
                val timestamp = System.currentTimeMillis()

                for (i in 0 until jsonArray.length()) {
                    val jsonMateria = jsonArray.getJSONObject(i)

                    listaMaterias.add(
                        CargaAcademicaEntity(
                            materia = jsonMateria.optString("Materia", "Sin nombre"),
                            docente = jsonMateria.optString("Docente", "Sin asignar"),
                            grupo = jsonMateria.optString("Grupo", "-"),
                            creditos = jsonMateria.optString("CreditosMateria", "0"),
                            fechaSincronizacion = timestamp
                        )
                    )
                }

//                val cargaAcademica = xml
//                Log.d("datosCargaAcademica", xml)

                Result.success(listaMaterias)
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("ErrorCargaAcademica", errorBody ?: "Error body nulo")
                Result.failure(Exception("Error Carga Académica: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getKardex(lineamiento: Int): Result<List<KardexEntity>> {
        val soapXml = """
            <?xml version="1.0" encoding="utf-8"?>
            <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
              <soap:Body>
                <getAllKardexConPromedioByAlumno xmlns="http://tempuri.org/">
                  <aluLineamiento>$lineamiento</aluLineamiento>
                </getAllKardexConPromedioByAlumno>
              </soap:Body>
            </soap:Envelope>
        """.trimIndent()

        Log.d("KardexRequest", "Enviando este XML:\n$soapXml")

        return try {
            val response = api.getKardex(soapXml)
            if (response.isSuccessful && response.body() != null) {
                val xml = response.body()!!

                if (xml.contains("<html", ignoreCase = true)) {
                    Log.e("KardexError", "El servidor devolvió un HTML en lugar del Kardex.")
                    return Result.failure(Exception("Servidor devolvió HTML"))
                }

                val regex = "\\{.*\\}".toRegex(RegexOption.DOT_MATCHES_ALL)
                val matchResult = regex.find(xml)

                if (matchResult == null) {
                    Log.e("KardexError", "No se encontró un JSON válido en el XML")
                    return Result.failure(Exception("JSON no encontrado"))
                }

                //Log.d("KardexJSON", matchResult.toString())

                val jsonString = matchResult.value

                val rootObject = org.json.JSONObject(jsonString)

                val jsonArray = rootObject.optJSONArray("lstKardex") ?: org.json.JSONArray()

                val listaKardex = mutableListOf<KardexEntity>()
                val timestamp = System.currentTimeMillis()

                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)

                    val periodoFormateado = "${obj.optString("P1", "")} ${obj.optString("A1", "")}".trim()

                    listaKardex.add(
                        KardexEntity(
                            claveMateria = obj.optString("ClvOfiMat", ""),
                            materia = obj.optString("Materia", ""),
                            calificacion = obj.optString("Calif", "0"),
                            periodo = periodoFormateado,
                            creditos = obj.optString("Cdts", "0"),
                            fechaSincronizacion = timestamp
                        )
                    )
                }

                Result.success(listaKardex)
            } else {
                Result.failure(Exception("Error Kardex: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCalifFinal(modEducativo: Int): Result<List<CalifFinalEntity>> {
        val soapXml = """
            <?xml version="1.0" encoding="utf-8"?>
            <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
              <soap:Body>
                <getAllCalifFinalByAlumnos xmlns="http://tempuri.org/">
                  <bytModEducativo>$modEducativo</bytModEducativo>
                </getAllCalifFinalByAlumnos>
              </soap:Body>
            </soap:Envelope>
        """.trimIndent()

        return try {
            val response = api.getCalifFinal(soapXml)
            if (response.isSuccessful && response.body() != null) {
                val xml = response.body()!!

                if (xml.contains("<html", ignoreCase = true)) {
                    return Result.failure(Exception("Servidor devolvió HTML"))
                }

                val regex = "\\[.*\\]".toRegex(RegexOption.DOT_MATCHES_ALL)
                val matchResult = regex.find(xml)

                if (matchResult == null) {
                    return Result.failure(Exception("JSON no encontrado"))
                }

                val jsonString = matchResult.value
                val jsonArray = org.json.JSONArray(jsonString)
                val listaCalif = mutableListOf<CalifFinalEntity>()
                val timestamp = System.currentTimeMillis()

                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    listaCalif.add(
                        CalifFinalEntity(
                            materia = obj.optString("materia", ""),
                            grupo = obj.optString("grupo", ""),
                            calificacion = obj.optString("calif", "0"),
                            acreditacion = obj.optString("acred", ""),
                            observaciones = obj.optString("Observaciones", ""),
                            fechaSincronizacion = timestamp
                        )
                    )
                }
                Result.success(listaCalif)

            } else {
                Result.failure(Exception("Error califFinal: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCalifUnidades(): Result<String> {
        val soapXml = """
            <?xml version="1.0" encoding="utf-8"?>
            <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
              <soap:Body>
                <getCalifUnidadesByAlumno xmlns="http://tempuri.org/" />
              </soap:Body>
            </soap:Envelope>
        """.trimIndent()

        return try {
            val response = api.getCalifUnidades(soapXml)
            if (response.isSuccessful && response.body() != null) {
                val xml = response.body()!!

                val califUnidades = xml
                Log.d("califUnidades", xml)

                Result.success(califUnidades)
            } else {
                Result.failure(Exception("Error califUnidades: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    private fun extraerDatos(xml: String): Alumno {
        // Paso A: Sacar el JSON de adentro del XML usando texto simple
        // Buscamos lo que está entre las etiquetas <get...Result> y </get...Result>
        val jsonString = xml
            .substringAfter("<getAlumnoAcademicoWithLineamientoResult>")
            .substringBefore("</getAlumnoAcademicoWithLineamientoResult>")

        // Paso B: Convertir ese texto a un objeto JSON real
        val json = JSONObject(jsonString)

        // Paso C: Extraer los datos usando las llaves EXACTAS
        return Alumno(
            nombre = json.optString("nombre"),
            matricula = json.optString("matricula"),
            carrera = json.optString("carrera"),
            semestre = json.optString("semActual"),
            especialidad = json.optString("especialidad"),
            estatus = json.optString("estatus"),
            lineamiento = json.optInt("lineamiento"),
            modEducativo = json.optInt("modEducativo"),
        )
    }
}