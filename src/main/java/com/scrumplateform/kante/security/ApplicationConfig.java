package com.scrumplateform.kante.security;

import org.springframework.context.annotation.Configuration;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {
    
    // private final StudentAccountRepository utilisateurRepository;

    // @Bean
    // public UserDetailsService userDetailsService(){
    //     return usermane -> utilisateurRepository.findByEmail(usermane)
    //         .orElseThrow(() -> new UsernameNotFoundException("Utilsateur introuvable"));
    // }


    // @Bean
    // public AuthenticationProvider authenticationProvider(){
    //     DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    //     authProvider.setUserDetailsService(userDetailsService());
    //     authProvider.setPasswordEncoder(passwordEncoder());
    //     return authProvider;
    // }


    // @Bean
    // public AuthenticationManager authenticationManager(AuthenticationConfiguration config)throws Exception{
    //     return config.getAuthenticationManager();
    // }


    // @Bean
    // public PasswordEncoder passwordEncoder() {
    //    return new BCryptPasswordEncoder();
    // };


    
}
