package org.example.cloudpos.cart.util;

import com.github.f4b6a3.ulid.UlidCreator;

public final class Ulids {
    private Ulids() {}
    public static String newUlid() {
        return UlidCreator.getUlid().toString(); // 26자, Crockford Base32
    }
}
