package com.passaparola.thiagodesales.passaparolaview.utils;

import java.util.Random;

public class Utils {

    public static String sortChiaraImage() {
        Random r = new Random();
        int nextChiara = r.nextInt(9);
        String str = "ch" + (nextChiara == 0 ? 1 : nextChiara);
        return str;
    }

}
