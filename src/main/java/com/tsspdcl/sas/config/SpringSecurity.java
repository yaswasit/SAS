package com.tsspdcl.sas.config;

import javax.servlet.http.HttpSessionListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import com.tsspdcl.sas.security.CustomAccessDeniedHandler;
import com.tsspdcl.sas.security.LoginSuccessHandler;
import com.tsspdcl.sas.security.SessionListener;

@Configuration
@EnableWebSecurity
public class SpringSecurity  {

	String[] staticResources  =  {
		"/assets/css/**",
	    "/assets/img/**",
	    "/assets/js/**",
	    "/downloads/**",
	    "/fragments/**",
	};
	 
    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    	   	
        http.csrf().disable()
    	.authorizeHttpRequests((authorize) ->
            authorize.antMatchers("/register/**").permitAll()
            	.antMatchers(staticResources).permitAll()
                .antMatchers("/").permitAll()
                .antMatchers("/error/**","/downloads/**").permitAll()
                .antMatchers("/sasportal/**", "/nsts/**", "/conscomp/**", "/mm/**", "/cgrf/**").hasAnyAuthority("ADMIN", "AAE", "AE", "ADE")
                //.antMatchers("/userdashboard/**").permitAll()
                //.antMatchers("/dashboard/**").hasAnyAuthority("ADMIN", "DE", "CGM")
                //.anyRequest().authenticated()
                
    		).formLogin(
    			form -> form
    			   	.loginPage("/login")
                    .loginProcessingUrl("/login")
                    .failureUrl("/login?error=true")
                    .successHandler(loginSuccessHandler)
                    //.defaultSuccessUrl("/dashboard")
                    //.defaultSuccessUrl("/sasportal")
                    .permitAll()
    		).logout(
    			logout -> logout
		        	//.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
    				.logoutUrl("/logout")
    				.logoutSuccessUrl("/signout")
    				.deleteCookies("JSESSIONID")
    				.permitAll()
    		);
        
        	http.sessionManagement()
        		
            	.maximumSessions(1)
            	.expiredUrl("/login?invalid=true")
            	.maxSessionsPreventsLogin(false)
            	.and()
            	.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            	.invalidSessionUrl("/sessionout?expired=true");
        		        
	        http.exceptionHandling()
	        	.accessDeniedHandler(accessDeniedHandler());
	        
        return http.build();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
        	.userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder());
    }
    
    @Autowired private LoginSuccessHandler loginSuccessHandler;
    
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }
    
    @Bean
    public HttpSessionListener getHttpSessionListener(){
        return new SessionListener();
    }
        
}
