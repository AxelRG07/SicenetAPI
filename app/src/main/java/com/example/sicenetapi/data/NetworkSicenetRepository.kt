package com.example.sicenetapi.data

import android.util.Log
import com.example.sicenetapi.data.local.AlumnoDao
import com.example.sicenetapi.network.SicenetApi
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

    override suspend fun getCargaAcademica(): Result<String> {
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

                val cargaAcademica = xml
                Log.d("datosCargaAcademica", xml)

                Result.success(cargaAcademica)
            } else {
                Result.failure(Exception("Error Carga Académica: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getKardex(lineamiento: Int): Result<String> {
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

        return try {
            val response = api.getKardex(soapXml)
            if (response.isSuccessful && response.body() != null) {
                val xml = response.body()!!

                val kardex = xml
                Log.d("kardex", xml)

                Result.success(kardex)
            } else {
                Result.failure(Exception("Error Kardex: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCalifFinal(modEducativo: Int): Result<String> {
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

                val califFinal = xml
                Log.d("califFinal", xml)

                Result.success(califFinal)
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