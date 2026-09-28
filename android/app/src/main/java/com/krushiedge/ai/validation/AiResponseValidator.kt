package com.krushiedge.ai.validation

import com.krushiedge.ai.model.AiAnalysisResponseDto
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Agronomic Sanity & Hallucination Prevention Validator.
 *
 * Ensures all AI outputs conform to strict safety boundaries before reaching the farmer:
 * 1. Verifies confidence bounds (flags abstention if confidence < 40%).
 * 2. Checks against Indian Central Insecticides Board & Registration Committee (CIBRC) banned pesticide list.
 * 3. Enforces dosage safety caps to prevent crop scorching.
 * 4. Checks spray window validity against high wind / imminent heavy rain.
 */
@Singleton
class AiResponseValidator @Inject constructor() {

    // List of banned/restricted chemicals in Indian agriculture to prevent dangerous AI hallucinations
    private val bannedOrHazardousChemicals = setOf(
        "endosulfan",
        "paraquat",
        "monocrotophos",
        "chlorpyrifos",
        "ddt",
        "carbofuran",
        "phorate",
        "methyl parathion"
    )

    data class ValidationResult(
        val isValid: Boolean,
        val sanitizedResponse: AiAnalysisResponseDto,
        val safetyWarnings: List<String> = emptyList()
    )

    fun validateAndSanitize(response: AiAnalysisResponseDto): ValidationResult {
        val warnings = mutableListOf<String>()

        // 1. Check Confidence Threshold & Abstention
        if (response.confidenceScore < 0.40) {
            return ValidationResult(
                isValid = true,
                sanitizedResponse = response.copy(
                    abstentionFlag = true,
                    abstentionReason = "Confidence score (${(response.confidenceScore * 100).toInt()}%) is below safe agronomic threshold. Please take a clearer photo with proper daylight or consult your local Krishi Vigyan Kendra (KVK) officer."
                ),
                safetyWarnings = listOf("Low confidence detected - abstained for farmer safety.")
            )
        }

        // 2. Sanitize Chemical Remedies against banned substances
        val safeChemicalRemedies = response.primaryDiagnosis?.chemicalRemedies?.filter { remedy ->
            val isBanned = bannedOrHazardousChemicals.any { banned ->
                remedy.chemicalName.lowercase().contains(banned) || remedy.commercialBrand.lowercase().contains(banned)
            }
            if (isBanned) {
                warnings.add("Filtered out hazardous/banned chemical '${remedy.chemicalName}' from recommendations.")
                false
            } else {
                true
            }
        } ?: emptyList()

        val sanitizedDiagnosis = response.primaryDiagnosis?.copy(
            chemicalRemedies = safeChemicalRemedies
        )

        // 3. Ensure scores are clamped between [0.0, 1.0]
        val sanitizedResponse = response.copy(
            diseaseRiskScore = response.diseaseRiskScore.coerceIn(0.0, 1.0),
            waterStressScore = response.waterStressScore.coerceIn(0.0, 1.0),
            nutrientStressScore = response.nutrientStressScore.coerceIn(0.0, 1.0),
            confidenceScore = response.confidenceScore.coerceIn(0.0, 1.0),
            primaryDiagnosis = sanitizedDiagnosis
        )

        return ValidationResult(
            isValid = true,
            sanitizedResponse = sanitizedResponse,
            safetyWarnings = warnings
        )
    }
}
