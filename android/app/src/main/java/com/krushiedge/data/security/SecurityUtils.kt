package com.krushiedge.data.security

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.UUID

object SecurityUtils {

    fun generateSalt(): String {
        val random = SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)
        return salt.joinToString("") { "%02x".format(it) }
    }

    fun hashPassword(password: String, salt: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val combined = password + salt
        val hashBytes = md.digest(combined.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    fun verifyPassword(password: String, salt: String, expectedHash: String): Boolean {
        val calculatedHash = hashPassword(password, salt)
        return calculatedHash.equals(expectedHash, ignoreCase = true)
    }

    fun generateSessionToken(): String {
        return UUID.randomUUID().toString() + "-" + System.currentTimeMillis()
    }
}
