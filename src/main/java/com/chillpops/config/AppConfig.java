package com.chillpops.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.Clock;
import java.time.ZoneId;

@Configuration
public class AppConfig {

    public static final String ZONE_ID_STR = "Asia/Kolkata";

    @Bean
    public ZoneId zoneId() {
        return ZoneId.of(ZONE_ID_STR);
    }

    @Bean
    public Clock clock(ZoneId zoneId) {
        return Clock.system(zoneId);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
