package com.zerobase.dividend.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@Slf4j
@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)//security5.7이후 설정방식이 달라짐
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter authenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity
                //.httpBasic(HttpBasicConfigurer::disable)//http token으로 작동
                .httpBasic(AbstractHttpConfigurer::disable)
                //.csrf(CsrfConfigurer::disable)
                //.csrf(csrf->csrf.ignoringRequestMatchers("/h2-console/**"))
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .csrf(cs-> cs.ignoringRequestMatchers(toH2Console()).disable())
                .authorizeHttpRequests(authorize -> authorize
                    .requestMatchers(AntPathRequestMatcher.antMatcher("/auth/signup" )).permitAll()
                    .requestMatchers(AntPathRequestMatcher.antMatcher("/auth/signin")).permitAll()
                    //.requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll()
                    .requestMatchers(toH2Console()).permitAll()
                    .anyRequest().authenticated())

                //위는 허용 아래는 잠금
                //.authorizeHttpRequests(authorize-> authorize.requestMatchers(HttpMethod.POST,"/**/reviews").authenticated())
                .headers(c->c.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .addFilterBefore(this.authenticationFilter, UsernamePasswordAuthenticationFilter.class);
         return httpSecurity.getOrBuild();
    }
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//                .httpBasic().disable()//토큰인증*
//                .csrf().disable()//사이트 위변조 요청방지*
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)*
//                .and()
//                .authorizeRequests()
//                .antMatchers("/**/signup", "/**/signin").permitAll()
//                .and()
//                .addFilterBefore(this.authenticationFilter, UsernamePasswordAuthenticationFilter.class);
//    }//security5.7이후 설정방식이 달라짐

//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer(){
//        return (web) -> web.ignoring()
//                .requestMatchers("/h2-console/**");
//    }

//    @Override
//    public void configure(final WebSecurity web) throws Exception {
//        web.ignoring()
//                .antMatchers("/h2-console/**");
//    }

    // spring boot 2.x
    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration authenticationConfiguration) throws Exception {
//=        return super.authenticationManagerBean();
        return authenticationConfiguration.getAuthenticationManager();
    }

}

