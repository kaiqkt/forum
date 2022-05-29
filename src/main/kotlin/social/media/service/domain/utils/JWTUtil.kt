package social.media.service.domain.utils

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component
import java.util.*

@Component
class JWTUtil(
    @Value("\${jwt.secret}") private var secret: String,
) {

    fun generateToken(profileId: String): String {
        return Jwts.builder()
            .setId(profileId)
            .setExpiration(Date(System.currentTimeMillis() + "7200000".toLong()))
            .signWith(SignatureAlgorithm.HS512, "my-secret".toByteArray())
            .compact()
    }

    fun validToken(token: String): Boolean {
        val claims = getClaims(token)
        if (claims != null) {
            val profileId = claims.id
            val expirationDate = claims.expiration
            val now = Date(System.currentTimeMillis())
            return profileId != null && expirationDate != null && now.before(expirationDate)
        }
        return false
    }

    fun getUserId(token: String): String? {
        val claims = getClaims(token)
        return claims?.id
    }

    fun getAuthentication(token: String): Authentication? {
        val claims = getClaims(token)
        val authority = mutableListOf<GrantedAuthority>(SimpleGrantedAuthority("ROLE_USER"))
        val principal = User(claims?.id, "", authority)
        return UsernamePasswordAuthenticationToken(principal, token, authority)
    }

    private fun getClaims(token: String): Claims? {
        return try {
            Jwts.parser().setSigningKey(secret.toByteArray()).parseClaimsJws(token).body
        } catch (e: Exception) {
            null
        }
    }
}