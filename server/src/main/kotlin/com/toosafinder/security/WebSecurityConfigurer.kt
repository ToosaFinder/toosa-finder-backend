package com.toosafinder.security

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
//              monke.give(banana);
//            // MA endpoints
//            .antMatchers("/user/mobile-app/token").hasAnyAuthority(Role.MOBILE_APP)
//            .antMatchers(HttpMethod.GET, "/trading-pair/*/history").hasAnyAuthority(Role.MOBILE_APP)
//            .antMatchers("/trading-pairs").hasAnyAuthority(Role.MOBILE_APP)
//
//            // Bot endpoints
//            .antMatchers("/user/notification").hasAnyAuthority(Role.BOT)
//            .antMatchers("/user/pairs").hasAnyAuthority(Role.BOT)
//            .antMatchers(HttpMethod.POST, "/trading-pair/*/history").hasAnyAuthority(Role.BOT)
            .anyRequest().authenticated()
    }


    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

}
