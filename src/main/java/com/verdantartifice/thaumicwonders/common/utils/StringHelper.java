package com.verdantartifice.thaumicwonders.common.utils;

import com.verdantartifice.thaumicwonders.ThaumicWonders;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentTranslation;

public class StringHelper {

    public static String getTranslationKey(String unlocName, String type, String... params) {
        StringBuilder builder = new StringBuilder(type + "." + ThaumicWonders.MODID + ":" + unlocName);
        for (String str : params) {
            builder.append(".").append(str);
        }
        return builder.toString();
    }

    public static String getLocalizedString(String unlocName, String type, String... params) {
        return I18n.format(getTranslationKey(unlocName, type, params));
    }

    public static TextComponentTranslation getTranslatedTextComponent(String unlocName, String type, String... params) {
        return new TextComponentTranslation(getTranslationKey(unlocName, type, params));
    }
}
