package com.example.filmpuanlama.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/** Giris yapmis kullaniciya kolay erisim. */
public final class Oturum {

    private Oturum() {}

    public static Long kullaniciId() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        return (Long) a.getPrincipal();
    }

    public static boolean adminMi() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        return a.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));
    }
}
