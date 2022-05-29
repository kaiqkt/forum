package social.media.service.resources.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsConfigurationSource
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfiguration(
    private val authenticationManager: AuthenticationManager,
    private val securityContextRepository: SecurityContextRepository
) {

    @Bean
    fun filterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        // Disable default security.
        http.httpBasic().disable()
        http.formLogin().disable()
        http.csrf().disable()
        http.logout().disable()

        // Add custom security.
        http.authenticationManager(authenticationManager)
        http.securityContextRepository(securityContextRepository)

        // Disable authentication for `/auth/**` routes.
        http.authorizeExchange().pathMatchers("/auth/login").permitAll()
        http.authorizeExchange().pathMatchers(HttpMethod.POST, *POST_MATCHER_ALL).permitAll()
        http.authorizeExchange().pathMatchers(HttpMethod.GET, *GET_MATCHER_ALL).permitAll()
        http.authorizeExchange().anyExchange().authenticated()
        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val cors = CorsConfiguration().applyPermitDefaultValues()
        cors.allowedMethods = listOf("POST", "GET", "PUT", "DELETE", "OPTIONS")
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", cors)
        return source
    }

    companion object {
        private val POST_MATCHER_ALL = arrayOf(
            "/profile/{profileId}"
        )
        private val GET_MATCHER_ALL = arrayOf(
            "/profile/{profileId}"
        )
    }
}
