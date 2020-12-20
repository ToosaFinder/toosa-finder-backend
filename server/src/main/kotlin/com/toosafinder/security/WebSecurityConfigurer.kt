package com.toosafinder.security

import com.toosafinder.security.entities.Role
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.access.AccessDeniedHandlerImpl
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
class WebSecurityConfigurer(
    private val tokenAuthenticationManager: TokenAuthenticationManager
) : WebSecurityConfigurerAdapter() {

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http
            .csrf()
                .disable()
                .cors().configurationSource(corsConfigurationSource)
            .and()
            .headers().frameOptions().sameOrigin()
            .and()
            .formLogin()
            .disable()
            .addFilterAt(
                TcsAuthHeaderFilter(tokenAuthenticationManager),
                UsernamePasswordAuthenticationFilter::class.java
            )
            .exceptionHandling()
            .accessDeniedHandler(AccessDeniedHandlerImpl())
            .and()
            .authorizeRequests()

            .antMatchers("/user/registration").permitAll()
            .antMatchers("/user/login").permitAll()
            .antMatchers("/user/set-password").permitAll()
            .antMatchers("/user/restore-password").permitAll()
            .antMatchers("/user/email-confirmed/**").permitAll()
            .antMatchers("/event/tag/popular").hasAuthority(Role.Name.USER.name)
            .antMatchers("/event").hasAuthority(Role.Name.USER.name)
            .anyRequest().authenticated()
    }

    private val corsConfigurationSource: CorsConfigurationSource =
            UrlBasedCorsConfigurationSource().apply {
                registerCorsConfiguration(
                        "/**",
                        CorsConfiguration()
                                .applyPermitDefaultValues()
                                .apply { allowedMethods = listOf("*") }
                )
            }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

}
