package com.example.thehub.utils

object ChileLocationHelper {

    // Mapa simple: Nombre de Región -> Lista de Comunas
    // He puesto algunas como ejemplo, tú puedes completar la lista
    val regionsAndComunas = mapOf(
        "Arica y Parinacota" to listOf("Arica", "Camarones", "Putre", "General Lagos"),
        "Valparaíso" to listOf("Valparaíso", "Viña del Mar", "Quilpué", "Villa Alemana", "San Antonio"),
        "Metropolitana" to listOf("Santiago", "Maipú", "Puente Alto", "La Florida", "Las Condes", "Providencia", "Ñuñoa"),
        "Biobío" to listOf("Concepción", "Talcahuano", "Los Ángeles", "San Pedro de la Paz"),
        "Araucanía" to listOf("Temuco", "Villarrica", "Pucón", "Angol")
    )

    fun getRegions(): List<String> {
        return regionsAndComunas.keys.toList()
    }

    fun getComunas(region: String): List<String> {
        return regionsAndComunas[region] ?: emptyList()
    }
}