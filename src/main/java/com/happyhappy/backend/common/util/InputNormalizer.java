package com.happyhappy.backend.common.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Objects;

public final class InputNormalizer {

    private InputNormalizer() {
    }

    public static String username(String s) {
        return Normalizer.normalize(Objects.toString(s, ""), Normalizer.Form.NFKC)
                .strip()
                .toLowerCase(Locale.ROOT);
    }

    public static String userId(String s) {
        return Normalizer.normalize(Objects.toString(s, ""), Normalizer.Form.NFKC)
                .strip();
    }

    public static String nickname(String s) {
        return Normalizer.normalize(Objects.toString(s, ""), Normalizer.Form.NFKC)
                .strip();
    }
}