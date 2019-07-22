package com.zyy.pinyougou.shop.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author: Zyy
 * @date: 2019-06-23 15:49
 * @description:
 * @version:
 */
@EnableWebSecurity
public class ShopSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/css/**","/js/**","/img/**","/plugins/**","/*.html","/seller/add.shtml").permitAll()
                .anyRequest().authenticated();

        http.formLogin().loginPage("/shoplogin.html")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/admin/index.html", true)
                        .failureUrl("/login?error");

        http.csrf().disable();

        http.headers().frameOptions().sameOrigin();

        http.logout().logoutUrl("/logout").invalidateHttpSession(true);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }
}
