package com.example.sicenetapi.data

import com.example.sicenetapi.network.RetrofitClient
import org.json.JSONObject

class SicenetRepository {
    private val api = RetrofitClient.api

    suspend fun login(matricula: String, contrasenia: String): Result<String> {
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

    suspend fun getProfile(): Result<Alumno> {
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

                Result.success(alumno)
            } else {
                Result.failure(Exception("Error Perfil: ${response.code()}"))
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

        // Paso C: Extraer los datos usando las llaves EXACTAS de tu imagen
        return Alumno(
            nombre = json.optString("nombre"),
            matricula = json.optString("matricula"),
            carrera = json.optString("carrera"),
            semestre = json.optString("semActual"),
            especialidad = json.optString("especialidad"),
            estatus = json.optString("estatus")
        )
    }
}

