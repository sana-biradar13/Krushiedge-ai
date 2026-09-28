package com.krushiedge.ai.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Objects (DTOs) for Cloud AI API (Gemini / Agronomy Backend).
 * Contains structured schemas and validation contracts.
 */

@Serializable
data class AiAnalysisRequestDto(
    @SerializedName("crop_type") val cropType: String,
    @SerializedName("crop_stage") val cropStage: String,
    @SerializedName("field_id") val fieldId: String,
    @SerializedName("grid_id") val gridId: String? = null,
    @SerializedName("image_base64") val imageBase64: String? = null,
    @SerializedName("ndvi") val ndvi: Double? = null,
    @SerializedName("soil_moisture") val soilMoisture: Double? = null,
    @SerializedName("temperature_celsius") val temperatureCelsius: Double? = null,
    @SerializedName("humidity_percent") val humidityPercent: Double? = null,
    @SerializedName("rainfall_mm") val rainfallMm: Double? = null,
    @SerializedName("soil_type") val soilType: String? = null,
    @SerializedName("farmer_language") val farmerLanguage: String = "kn"
)

@Serializable
data class AiAnalysisResponseDto(
    @SerializedName("crop_type") val cropType: String,
    @SerializedName("overall_stress_level") val overallStressLevel: String,
    @SerializedName("disease_risk_score") val diseaseRiskScore: Double,
    @SerializedName("water_stress_score") val waterStressScore: Double,
    @SerializedName("nutrient_stress_score") val nutrientStressScore: Double,
    @SerializedName("primary_diagnosis") val primaryDiagnosis: DiagnosisDto? = null,
    @SerializedName("confidence_score") val confidenceScore: Double,
    @SerializedName("evidence_factors") val evidenceFactors: List<EvidenceFactorDto> = emptyList(),
    @SerializedName("actionable_advice") val actionableAdvice: ActionableAdviceDto? = null,
    @SerializedName("abstention_flag") val abstentionFlag: Boolean = false,
    @SerializedName("abstention_reason") val abstentionReason: String? = null,
    @SerializedName("model_version") val modelVersion: String = "krushiedge-cloud-v3.1",
    @SerializedName("latency_ms") val latencyMs: Long = 0L
)

@Serializable
data class DiagnosisDto(
    @SerializedName("disease_id") val diseaseId: String,
    @SerializedName("name_en") val nameEn: String,
    @SerializedName("name_kn") val nameKn: String,
    @SerializedName("name_hi") val nameHi: String,
    @SerializedName("pathogen") val pathogen: String,
    @SerializedName("severity") val severity: String,
    @SerializedName("symptom_description") val symptomDescription: LocalizedTextDto,
    @SerializedName("cause_description") val causeDescription: LocalizedTextDto,
    @SerializedName("chemical_remedies") val chemicalRemedies: List<ChemicalRemedyDto> = emptyList(),
    @SerializedName("organic_remedies") val organicRemedies: List<OrganicRemedyDto> = emptyList(),
    @SerializedName("preventive_steps") val preventiveSteps: List<LocalizedTextDto> = emptyList()
)

@Serializable
data class ChemicalRemedyDto(
    @SerializedName("chemical_name") val chemicalName: String,
    @SerializedName("commercial_brand") val commercialBrand: String,
    @SerializedName("dosage") val dosage: String,
    @SerializedName("safety_class") val safetyClass: String,
    @SerializedName("cost_inr") val costInr: Double
)

@Serializable
data class OrganicRemedyDto(
    @SerializedName("name") val name: LocalizedTextDto,
    @SerializedName("preparation") val preparation: LocalizedTextDto,
    @SerializedName("dosage") val dosage: String,
    @SerializedName("cost_inr") val costInr: Double
)

@Serializable
data class EvidenceFactorDto(
    @SerializedName("evidence_type") val evidenceType: String,
    @SerializedName("weight_pct") val weightPct: Double,
    @SerializedName("explanation_en") val explanationEn: String,
    @SerializedName("explanation_kn") val explanationKn: String,
    @SerializedName("explanation_hi") val explanationHi: String
)

@Serializable
data class ActionableAdviceDto(
    @SerializedName("urgent_action") val urgentAction: LocalizedTextDto,
    @SerializedName("irrigation_action") val irrigationAction: LocalizedTextDto? = null,
    @SerializedName("spray_window_safe") val sprayWindowSafe: Boolean = true,
    @SerializedName("spray_window_reason") val sprayWindowReason: LocalizedTextDto? = null
)

@Serializable
data class LocalizedTextDto(
    @SerializedName("en") val en: String,
    @SerializedName("kn") val kn: String,
    @SerializedName("hi") val hi: String
)
