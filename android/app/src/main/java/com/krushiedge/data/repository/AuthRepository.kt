package com.krushiedge.data.repository

import com.krushiedge.data.local.dao.UserDao
import com.krushiedge.data.local.entity.UserEntity
import com.krushiedge.data.security.SecurityUtils
import com.krushiedge.util.SessionManager
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

sealed class AuthResult {
    data class Success(val userId: String, val name: String, val language: String) : AuthResult()
    data class Failure(val message: String) : AuthResult()
}

@Singleton
class AuthRepository @Inject constructor(
    private val userDao: UserDao,
    private val sessionManager: SessionManager
) {

    /**
     * Register a new user with secure salted SHA-256 password hashing.
     * Never stores the raw password.
     */
    suspend fun signUp(
        name: String,
        identifier: String,   // phone or email
        rawPassword: String,
        language: String
    ): AuthResult {
        if (name.isBlank()) return AuthResult.Failure("Name cannot be empty.")
        if (identifier.isBlank()) return AuthResult.Failure("Phone/email cannot be empty.")
        if (rawPassword.length < 6) return AuthResult.Failure("Password must be at least 6 characters.")

        // Check duplicate
        val existing = userDao.getUserByIdentifier(identifier.trim())
        if (existing != null) return AuthResult.Failure("Account already exists with this phone/email.")

        val salt = SecurityUtils.generateSalt()
        val hash = SecurityUtils.hashPassword(rawPassword, salt)
        val userId = UUID.randomUUID().toString()
        val token = SecurityUtils.generateSessionToken()

        val userEntity = UserEntity(
            id = userId,
            name = name.trim(),
            identifier = identifier.trim(),
            passwordHash = hash,
            salt = salt,
            preferredLanguage = language
        )

        userDao.insertUser(userEntity)
        sessionManager.saveSession(
            userId = userId,
            name = name.trim(),
            identifier = identifier.trim(),
            language = language,
            token = token
        )
        return AuthResult.Success(userId, name.trim(), language)
    }

    /**
     * Verify credentials using stored hash + salt. Never compares plaintext.
     */
    suspend fun login(
        identifier: String,
        rawPassword: String
    ): AuthResult {
        if (identifier.isBlank()) return AuthResult.Failure("Phone/email cannot be empty.")
        if (rawPassword.isBlank()) return AuthResult.Failure("Password cannot be empty.")

        val user = userDao.getUserByIdentifier(identifier.trim())
            ?: return AuthResult.Failure("No account found. Please sign up first.")

        val isValid = SecurityUtils.verifyPassword(rawPassword, user.salt, user.passwordHash)
        if (!isValid) return AuthResult.Failure("Incorrect password. Please try again.")

        val token = SecurityUtils.generateSessionToken()
        sessionManager.saveSession(
            userId = user.id,
            name = user.name,
            identifier = user.identifier,
            language = user.preferredLanguage,
            token = token
        )
        return AuthResult.Success(user.id, user.name, user.preferredLanguage)
    }

    fun logout() {
        sessionManager.clearSession()
    }

    fun isLoggedIn(): Boolean = sessionManager.isUserLoggedIn()

    fun getCurrentUserName(): String = sessionManager.getUserName()

    fun getCurrentLanguage(): String = sessionManager.getLanguage()
}
