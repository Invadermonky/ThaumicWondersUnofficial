package com.verdantartifice.thaumicwonders.common.compat;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderException;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.versioning.VersionParser;
import net.minecraftforge.fml.common.versioning.VersionRange;
import org.jetbrains.annotations.Nullable;

public enum ModIds {
    crafttweaker(ConstIds.crafttweaker),
    expanded_arcanum(ConstIds.expanded_arcanum),
    jeresources(ConstIds.jeresources),
    new_crimson_revelations(ConstIds.new_crimson_revelations, "1.0.0"),
    thaumic_augmentation(ConstIds.thaumic_augmentation),
    the_one_probe(ConstIds.the_one_probe);

    public final String modId;
    public final boolean isLoaded;

    ModIds(String modId, String version) {
        this.modId = modId;
        this.isLoaded = isModLoaded(modId, version);
    }

    ModIds(String modId) {
        this.modId = modId;
        this.isLoaded = isModLoaded(modId);
    }

    private static boolean isModLoaded(String modId) {
        return isModLoaded(modId, null);
    }

    private static boolean isModLoaded(String modId, @Nullable String version) {
        return isModLoaded(modId, version, true, false);
    }

    private static boolean isModLoaded(String modId, @Nullable String version, boolean isMinVersion, boolean isMaxVersion) {
        return Loader.isModLoaded(modId) && isSpecifiedVersion(modId, version, isMinVersion, isMaxVersion);
    }

    private static boolean isSpecifiedVersion(String modId, @Nullable String version, boolean isMinVersion, boolean isMaxVersion) {
        if (version == null)
            return true;

        boolean match = true;
        ModContainer container = Loader.instance().getIndexedModList().get(modId);
        if (container != null) {
            try {
                VersionRange versionRange = VersionParser.parseRange(getVersionString(version, isMinVersion, isMaxVersion));
                match = versionRange.containsVersion(container.getProcessedVersion());
            } catch (LoaderException ignored) {
            }
        }
        return match;
    }

    private static String getVersionString(String version) {
        return getVersionString(version, true, false);
    }

    private static String getVersionString(String version, boolean isMinVersion, boolean isMaxVersion) {
        String versionStr = "";
        versionStr += isMinVersion ? "[" : "(";
        versionStr += isMaxVersion ? "," : "";
        versionStr += version;
        versionStr += isMinVersion ? "," : "";
        versionStr += isMaxVersion ? "]" : ")";
        return versionStr;
    }

    public static class ConstIds {
        public static final String crafttweaker = "crafttweaker";
        public static final String expanded_arcanum = "ea";
        public static final String jeresources = "jeresources";
        public static final String new_crimson_revelations = "crimsonrevelations";
        public static final String thaumic_augmentation = "thaumicaugmentation";
        public static final String the_one_probe = "theoneprobe";
    }
}
