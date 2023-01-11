package com.project.bank1.config;

import com.project.bank1.security.auth.RestAuthenticationEntryPoint;
import com.project.bank1.security.auth.TokenAuthenticationFilter;
import com.project.bank1.security.util.TokenUtils;
import com.project.bank1.service.CustomClientDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private CustomClientDetailsService customUserDetailsService;

    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Autowired
    private TokenUtils tokenUtils;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()

                .exceptionHandling().authenticationEntryPoint(restAuthenticationEntryPoint).and()

                .authorizeRequests().antMatchers("**/auth/**").permitAll()
                .antMatchers("**/getAll/**").permitAll()
                .antMatchers("**/pay**").permitAll()
                .antMatchers("**/registration").permitAll()
                .antMatchers("**/getQR").permitAll()
                .antMatchers("**/getQRCode").permitAll()
                .antMatchers("**/getQRCodeImage").permitAll()
                .antMatchers("**/image").permitAll()
                .antMatchers("**/getQRCodeData/**").permitAll()
                .antMatchers("**/login").permitAll()
                .antMatchers("**/validateAcquirer").permitAll()
                .antMatchers("**/validateIssuer").permitAll()
                .antMatchers("**/issuerBankPayment").permitAll()
                .antMatchers("**/h2-console/**").permitAll()



                .anyRequest().authenticated().and()

                .cors().and()

                .addFilterBefore(new TokenAuthenticationFilter(tokenUtils, customUserDetailsService), BasicAuthenticationFilter.class);

        http.csrf().disable();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {

        web.ignoring().antMatchers(HttpMethod.POST, "/**/auth/**");
        web.ignoring().antMatchers(HttpMethod.POST, "/**/registration");
        web.ignoring().antMatchers(HttpMethod.POST, "/**/login");
        web.ignoring().antMatchers(HttpMethod.POST, "/**/pay");
        web.ignoring().antMatchers(HttpMethod.POST, "/**/validateAcquirer");
        web.ignoring().antMatchers(HttpMethod.POST, "/**/validateIssuer");
        web.ignoring().antMatchers(HttpMethod.POST, "/**/issuerBankPayment");
        web.ignoring().antMatchers(HttpMethod.GET, "/**/auth/**");
        web.ignoring().antMatchers(HttpMethod.GET, "/**/getAll/**");
        web.ignoring().antMatchers(HttpMethod.GET, "/**/getAll/**");
        web.ignoring().antMatchers(HttpMethod.POST, "/**/getQRCode");
        web.ignoring().antMatchers(HttpMethod.POST, "/**/getQRCodeImage");
        web.ignoring().antMatchers(HttpMethod.GET, "/**/image");
        web.ignoring().antMatchers(HttpMethod.GET, "/**/getQrCodeData/**");
        web.ignoring().antMatchers(HttpMethod.GET, "**/h2-console/**");

        web.ignoring().antMatchers(HttpMethod.GET, "/", "/webjars/**", "/*.html", "favicon.ico", "/**/*.html",
                "/**/*.css", "/**/*.js");
    }
}
