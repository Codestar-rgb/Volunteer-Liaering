package com.subspaceparasite.api.parasite;

import java.util.Collections;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.util.StringRepresentable;

/**
 * Represents every parasite entity type from the original Scape and Run: Parasites mod.
 * <p>
 * Each constant carries the internal type ID used in network and save-data,
 * base combat stats, evolution contribution weight, One Mind death value,
 * despawn eligibility, and the natural-spawning phase range.
 * <p>
 * Types are grouped by evolution tier, mirroring the original SRP class
 * hierarchy. The byte ID ranges are preserved for wire compatibility.
 *
 * @author SubspaceParasite Team
 * @since 1.0.0
 */
public enum ParasiteType implements StringRepresentable {

    // ────────────────────────────────────────────────────────────────
    //  INFECTED  (0x00 – 0x0F)
    //  Mimic-stage parasites that retain the appearance of their host.
    // ────────────────────────────────────────────────────────────────
    SIM_BIGSPIDER    (0x00, 1.0f,  16.0f, 2.0f, 0.0f, 0.28f, true,  1, 0, 2, "sim_bigspider",    "Dorpa"),
    SIM_SQUID        (0x01, 0.5f,  10.0f, 1.0f, 0.0f, 0.25f, true,  1, 0, 2, "sim_squid",        "Sim Squid"),
    SIM_HUMAN        (0x02, 2.0f,  20.0f, 3.0f, 0.0f, 0.30f, true,  2, 0, 3, "sim_human",        "Sim Human"),
    SIM_COW          (0x03, 1.0f,  12.0f, 2.0f, 0.0f, 0.25f, true,  1, 0, 2, "sim_cow",          "Sim Cow"),
    SIM_SHEEP        (0x04, 0.8f,  10.0f, 1.5f, 0.0f, 0.25f, true,  1, 0, 2, "sim_sheep",        "Sim Sheep"),
    SIM_WOLF         (0x05, 1.2f,  12.0f, 3.0f, 0.0f, 0.30f, true,  1, 0, 2, "sim_wolf",         "Sim Wolf"),
    SIM_PIG          (0x06, 0.7f,  10.0f, 1.5f, 0.0f, 0.25f, true,  1, 0, 2, "sim_pig",          "Sim Pig"),
    SIM_VILLAGER     (0x07, 1.5f,  20.0f, 2.5f, 0.0f, 0.28f, true,  2, 0, 3, "sim_villager",     "Sim Villager"),
    SIM_ADVENTURER   (0x08, 2.5f,  20.0f, 4.0f, 0.0f, 0.32f, true,  3, 0, 3, "sim_adventurer",   "Sim Adventurer"),
    SIM_HORSE        (0x09, 1.5f,  18.0f, 2.0f, 0.0f, 0.35f, true,  2, 0, 2, "sim_horse",        "Sim Horse"),
    SIM_BEAR         (0x0A, 2.0f,  24.0f, 4.0f, 0.0f, 0.28f, true,  2, 0, 3, "sim_bear",         "Sim Bear"),
    SIM_ENDERMAN     (0x0B, 3.0f,  40.0f, 7.0f, 0.0f, 0.30f, true,  5, 1, 4, "sim_enderman",     "Sim Enderman"),
    SIM_DRAGONE      (0x0C, 4.0f,  60.0f, 8.0f, 4.0f, 0.30f, false, 10, 3, 6, "sim_dragone",      "Sim Dragone"),
    SIM_DRAGONE_HEAD (0x0D, 4.0f,  60.0f, 8.0f, 4.0f, 0.30f, false, 10, 3, 6, "sim_dragone_head", "Sim Dragone Head"),

    // ────────────────────────────────────────────────────────────────
    //  SPECIAL INFECTED  (0x10 – 0x1F)
    //  Marauder-type infected; stronger mimic variants.
    // ────────────────────────────────────────────────────────────────
    MAR_ENDERMAN     (0x10, 3.5f, 50.0f,  8.0f, 2.0f, 0.32f, true,  6, 1, 4, "mar_enderman",  "Marauder Enderman"),
    MAR_COW          (0x11, 1.5f, 20.0f,  4.0f, 0.0f, 0.28f, true,  2, 0, 3, "mar_cow",       "Marauder Cow"),
    MAR_VILLAGER     (0x12, 2.0f, 25.0f,  4.0f, 0.0f, 0.30f, true,  3, 0, 3, "mar_villager",  "Marauder Villager"),
    MAR_HUMAN        (0x13, 2.5f, 24.0f,  5.0f, 0.0f, 0.32f, true,  3, 0, 3, "mar_human",     "Marauder Human"),
    MAR_SHEEP        (0x14, 1.2f, 16.0f,  3.0f, 0.0f, 0.28f, true,  2, 0, 3, "mar_sheep",     "Marauder Sheep"),
    MAR_BEAR         (0x15, 2.5f, 30.0f,  5.0f, 0.0f, 0.30f, true,  3, 0, 3, "mar_bear",      "Marauder Bear"),

    // ────────────────────────────────────────────────────────────────
    //  FERAL  (0x20 – 0x2F)
    //  Fully transformed host bodies — no longer disguised.
    // ────────────────────────────────────────────────────────────────
    FER_BEAR         (0x20, 3.0f, 40.0f,  6.0f, 2.0f, 0.30f, true,  4, 1, 4, "fer_bear",      "Feral Bear"),
    FER_COW          (0x21, 2.0f, 24.0f,  4.0f, 0.0f, 0.28f, true,  3, 1, 3, "fer_cow",       "Feral Cow"),
    FER_ENDERMAN     (0x22, 4.0f, 60.0f, 10.0f, 4.0f, 0.35f, true,  8, 2, 5, "fer_enderman",  "Feral Enderman"),
    FER_HORSE        (0x23, 2.5f, 28.0f,  4.0f, 0.0f, 0.38f, true,  3, 1, 4, "fer_horse",     "Feral Horse"),
    FER_HUMAN        (0x24, 3.0f, 30.0f,  6.0f, 0.0f, 0.32f, true,  4, 1, 4, "fer_human",     "Feral Human"),
    FER_PIG          (0x25, 1.5f, 18.0f,  3.0f, 0.0f, 0.28f, true,  2, 1, 3, "fer_pig",       "Feral Pig"),
    FER_SHEEP        (0x26, 1.5f, 18.0f,  3.0f, 0.0f, 0.28f, true,  2, 1, 3, "fer_sheep",     "Feral Sheep"),
    FER_VILLAGER     (0x27, 2.5f, 28.0f,  5.0f, 0.0f, 0.30f, true,  3, 1, 4, "fer_villager",  "Feral Villager"),
    FER_WOLF         (0x28, 2.0f, 20.0f,  5.0f, 0.0f, 0.32f, true,  3, 1, 3, "fer_wolf",      "Feral Wolf"),

    // ────────────────────────────────────────────────────────────────
    //  HIJACKED  (0x30 – 0x3F)
    //  Non-standard hosts whose abilities are co-opted.
    // ────────────────────────────────────────────────────────────────
    HI_BLAZE         (0x30, 4.0f, 40.0f, 8.0f, 0.0f, 0.28f, true,  6, 2, 5, "hi_blaze",      "Hijacked Blaze"),
    HI_GOLEM         (0x31, 5.0f, 80.0f, 10.0f, 8.0f, 0.20f, true,  8, 2, 5, "hi_golem",      "Hijacked Golem"),
    HI_SKELETON      (0x32, 3.0f, 24.0f, 5.0f, 0.0f, 0.30f, true,  4, 2, 4, "hi_skeleton",   "Hijacked Skeleton"),

    // ────────────────────────────────────────────────────────────────
    //  INBORN  (0x40 – 0x4F)
    //  Parasites born from the colony — not converted hosts.
    // ────────────────────────────────────────────────────────────────
    CARRIER_HEAVY    (0x40, 5.0f, 60.0f,  4.0f, 4.0f, 0.22f, false, 8, 1, 5, "carrier_heavy",  "Rathol"),
    CARRIER_LIGHT    (0x41, 3.0f, 30.0f,  2.0f, 0.0f, 0.30f, false, 4, 1, 4, "carrier_light",  "Gothol"),
    BUGLIN           (0x42, 1.0f,  8.0f,  2.0f, 0.0f, 0.40f, true,  1, 0, 3, "buglin",         "Lodo"),
    CARRIER_FLYING   (0x43, 3.5f, 24.0f,  3.0f, 0.0f, 0.35f, false, 5, 1, 5, "carrier_flying", "Buthol"),
    RUPTER           (0x44, 1.5f, 12.0f,  3.0f, 0.0f, 0.35f, true,  2, 0, 3, "rupter",         "Mudo"),
    MOVING_FLESH     (0x45, 1.0f, 10.0f,  1.0f, 0.0f, 0.25f, true,  1, 0, 2, "moving_flesh",   "Lesh"),
    WORKER           (0x46, 2.0f, 16.0f,  3.0f, 1.0f, 0.28f, false, 3, 1, 4, "worker",         "Kol"),
    MANGLER          (0x47, 3.0f, 24.0f,  6.0f, 2.0f, 0.30f, false, 4, 1, 5, "mangler",        "Nuuh"),
    GNAT             (0x48, 0.5f,  4.0f,  1.0f, 0.0f, 0.45f, true,  1, 0, 3, "gnat",           "Ata"),
    LICE             (0x49, 0.3f,  2.0f,  0.5f, 0.0f, 0.35f, true,  1, 0, 2, "lice",           "Viin"),

    // ────────────────────────────────────────────────────────────────
    //  CRUDE  (0x50 – 0x5F)
    //  Early colony structures and immature forms.
    // ────────────────────────────────────────────────────────────────
    INCOMPLETEFORM_SMALL  (0x50, 1.0f,  8.0f,  1.0f, 0.0f, 0.25f, true,  1, 0, 2, "incompleteform_small",  "Incomplete Form Small"),
    INCOMPLETEFORM_MEDIUM (0x51, 2.0f, 16.0f,  2.0f, 0.0f, 0.25f, true,  2, 0, 3, "incompleteform_medium", "Incomplete Form Medium"),
    HOST                  (0x52, 2.5f, 20.0f,  3.0f, 0.0f, 0.25f, true,  3, 0, 3, "host",                  "Host"),
    HOSTII                (0x53, 3.0f, 24.0f,  4.0f, 0.0f, 0.25f, true,  4, 0, 4, "hostii",                "Host II"),
    HEED                  (0x54, 2.0f, 16.0f,  2.0f, 0.0f, 0.25f, true,  3, 1, 4, "heed",                  "Heed"),
    CRUX                  (0x55, 4.0f, 40.0f,  4.0f, 2.0f, 0.20f, false, 6, 2, 5, "crux",                  "Crux"),
    CRUX_INCOMPLETE       (0x56, 3.0f, 30.0f,  3.0f, 1.0f, 0.20f, false, 4, 2, 5, "crux_incomplete",       "Crux Incomplete"),
    THRALL                (0x57, 2.0f, 16.0f,  3.0f, 0.0f, 0.28f, true,  3, 1, 4, "thrall",                "Thrall"),
    DREDGE                (0x58, 2.5f, 20.0f,  4.0f, 0.0f, 0.28f, true,  3, 1, 4, "dredge",                "Dredge"),
    AIRSCREW              (0x59, 1.5f, 10.0f,  1.0f, 0.0f, 0.40f, true,  2, 1, 4, "airscrew",              "Airscrew"),
    CARRIER_WORM          (0x5A, 3.0f, 20.0f,  2.0f, 0.0f, 0.30f, false, 5, 1, 5, "carrier_worm",          "Carrier Worm"),

    // ────────────────────────────────────────────────────────────────
    //  PRIMITIVE  (0x60 – 0x6F)
    //  First true combat forms; colony defenders.
    // ────────────────────────────────────────────────────────────────
    PRI_BANO        (0x60, 3.0f, 60.0f,  8.0f, 4.0f, 0.19f, false, 4, 1, 3, "pri_bano",        "Bano"),
    PRI_CANRA       (0x61, 2.8f, 55.0f,  7.5f, 3.8f, 0.18f, false, 4, 1, 3, "pri_canra",       "Canra"),
    PRI_EMANA       (0x62, 2.5f, 45.0f,  6.0f, 3.5f, 0.17f, false, 3, 1, 2, "pri_emana",       "Emana"),
    PRI_GIM         (0x63, 2.2f, 40.0f,  5.5f, 3.2f, 0.16f, false, 3, 1, 2, "pri_gim",         "Gim"),
    PRI_HULL        (0x64, 3.2f, 65.0f,  9.0f, 4.2f, 0.20f, false, 5, 1, 4, "pri_hull",        "Hull"),
    PRI_IKI         (0x65, 2.6f, 50.0f,  7.0f, 3.6f, 0.175f, false, 4, 1, 3, "pri_iki",         "Iki"),
    PRI_LUM         (0x66, 2.4f, 42.0f,  6.5f, 3.4f, 0.165f, false, 3, 1, 2, "pri_lum",         "Lum"),
    PRI_NOGLA       (0x67, 2.9f, 58.0f,  8.2f, 3.9f, 0.185f, false, 4, 1, 3, "pri_nogla",       "Nogla"),
    PRI_RANRAC      (0x68, 3.1f, 62.0f,  8.5f, 4.1f, 0.195f, false, 5, 1, 3, "pri_ranrac",      "Ranrac"),
    PRI_SHYCO       (0x69, 2.7f, 52.0f,  7.2f, 3.7f, 0.178f, false, 4, 1, 3, "pri_shyco",       "Shyco"),
    PRI_WYMO        (0x6A, 2.3f, 38.0f,  5.0f, 3.0f, 0.15f, false, 3, 1, 2, "pri_wymo",        "Wymo"),
    PRI_ZAA         (0x6B, 3.5f, 70.0f, 10.0f, 4.5f, 0.22f, false, 6, 1, 5, "pri_zaa",         "Zaa"),
    PRI_LONGARMS    (0x6C, 5.0f, 40.0f,  8.0f, 2.0f, 0.30f, false, 6, 2, 5, "pri_longarms",    "Primitive Longarms"),
    PRI_MANDUCATER  (0x6D, 4.5f, 36.0f,  7.0f, 2.0f, 0.28f, false, 5, 2, 5, "pri_manducater",  "Primitive Manducater"),
    PRI_REEKER      (0x6E, 3.5f, 28.0f,  4.0f, 0.0f, 0.25f, false, 5, 2, 5, "pri_reeker",      "Primitive Reeker"),
    PRI_YELLOWEYE   (0x6F, 4.0f, 32.0f,  6.0f, 2.0f, 0.30f, false, 5, 2, 5, "pri_yelloweye",   "Primitive Yelloweye"),
    PRI_SUMMONER    (0x70, 4.0f, 30.0f,  4.0f, 1.0f, 0.25f, false, 6, 3, 6, "pri_summoner",    "Primitive Summoner"),
    PRI_BOLSTER     (0x71, 3.5f, 28.0f,  3.0f, 2.0f, 0.25f, false, 5, 2, 5, "pri_bolster",     "Primitive Bolster"),
    PRI_TOZON       (0x72, 5.0f, 48.0f,  7.0f, 4.0f, 0.22f, false, 7, 3, 6, "pri_tozon",       "Primitive Tozon"),
    PRI_ARACHNIDA   (0x73, 4.0f, 32.0f,  6.0f, 0.0f, 0.32f, false, 5, 2, 5, "pri_arachnida",   "Primitive Arachnida"),
    PRI_DEVOURER    (0x74, 5.5f, 50.0f,  9.0f, 3.0f, 0.28f, false, 7, 3, 6, "pri_devourer",    "Primitive Devourer"),
    PRI_VERMIN      (0x75, 3.0f, 20.0f,  4.0f, 0.0f, 0.35f, false, 4, 2, 5, "pri_vermin",      "Primitive Vermin"),
    PRI_VISCERA     (0x76, 4.0f, 34.0f,  5.0f, 2.0f, 0.25f, false, 6, 3, 6, "pri_viscera",     "Primitive Viscera"),
    PRI_BURROWER    (0x77, 3.5f, 28.0f,  4.0f, 2.0f, 0.25f, false, 5, 2, 5, "pri_burrower",    "Primitive Burrower"),

    // ────────────────────────────────────────────────────────────────
    //  ADAPTED  (0x70 – 0x7F)
    //  Evolved primitives — stronger in every stat.
    // ────────────────────────────────────────────────────────────────
    ADA_LONGARMS    (0x70, 7.0f,  60.0f, 12.0f, 4.0f, 0.32f, false,  9, 3, 6, "ada_longarms",    "Adapted Longarms"),
    ADA_MANDUCATER  (0x71, 6.5f,  54.0f, 10.0f, 4.0f, 0.30f, false,  8, 3, 6, "ada_manducater",  "Adapted Manducater"),
    ADA_REEKER      (0x72, 5.0f,  40.0f,  6.0f, 0.0f, 0.28f, false,  7, 3, 6, "ada_reeker",      "Adapted Reeker"),
    ADA_YELLOWEYE   (0x73, 6.0f,  48.0f,  9.0f, 4.0f, 0.32f, false,  8, 3, 6, "ada_yelloweye",   "Adapted Yelloweye"),
    ADA_SUMMONER    (0x74, 5.5f,  44.0f,  6.0f, 2.0f, 0.28f, false,  9, 4, 7, "ada_summoner",    "Adapted Summoner"),
    ADA_BOLSTER     (0x75, 5.0f,  40.0f,  5.0f, 4.0f, 0.28f, false,  7, 3, 6, "ada_bolster",     "Adapted Bolster"),
    ADA_TOZON       (0x76, 7.0f,  70.0f, 10.0f, 6.0f, 0.25f, false, 10, 4, 7, "ada_tozon",       "Adapted Tozon"),
    ADA_ARACHNIDA   (0x77, 6.0f,  48.0f,  9.0f, 0.0f, 0.35f, false,  8, 3, 6, "ada_arachnida",   "Adapted Arachnida"),
    ADA_DEVOURER    (0x78, 8.0f,  80.0f, 13.0f, 5.0f, 0.30f, false, 11, 4, 7, "ada_devourer",    "Adapted Devourer"),
    ADA_VERMIN      (0x79, 4.5f,  30.0f,  6.0f, 0.0f, 0.38f, false,  6, 3, 6, "ada_vermin",      "Adapted Vermin"),
    ADA_VISCERA     (0x7A, 6.0f,  50.0f,  8.0f, 4.0f, 0.28f, false,  9, 4, 7, "ada_viscera",     "Adapted Viscera"),
    ADA_BURROWER    (0x7B, 5.0f,  40.0f,  6.0f, 4.0f, 0.28f, false,  7, 3, 6, "ada_burrower",    "Adapted Burrower"),

    // ────────────────────────────────────────────────────────────────
    //  NEXUS — BECKON  (0x80 – 0x8F)
    //  Colony nexus structures that lure and convert.
    // ────────────────────────────────────────────────────────────────
    BECKON_SI       (0x80,  8.0f,  80.0f,  6.0f, 6.0f, 0.00f, false, 12, 3, 6, "beckon_si",    "Beckon Stage I"),
    BECKON_SII      (0x81, 12.0f, 120.0f,  8.0f, 8.0f, 0.00f, false, 18, 4, 7, "beckon_sii",   "Beckon Stage II"),
    BECKON_SIII     (0x82, 16.0f, 160.0f, 10.0f, 10.0f, 0.00f, false, 25, 5, 8, "beckon_siii",  "Beckon Stage III"),
    BECKON_SIV      (0x83, 24.0f, 240.0f, 14.0f, 14.0f, 0.00f, false, 40, 6, 9, "beckon_siv",   "Beckon Stage IV"),

    // ────────────────────────────────────────────────────────────────
    //  NEXUS — DISPATCHER  (0x90 – 0x9F)
    //  Colony nexus structures that spawn combat units.
    // ────────────────────────────────────────────────────────────────
    DISPATCHER_TEN  (0x90,  5.0f,  60.0f,  4.0f, 4.0f, 0.00f, false,  8, 3, 6, "dispatcher_ten",  "Dispatcher Tendril"),
    DISPATCHER_SI   (0x91,  8.0f,  80.0f,  6.0f, 6.0f, 0.00f, false, 12, 4, 7, "dispatcher_si",   "Dispatcher Stage I"),
    DISPATCHER_SII  (0x92, 12.0f, 120.0f,  8.0f, 8.0f, 0.00f, false, 20, 5, 8, "dispatcher_sii",  "Dispatcher Stage II"),
    DISPATCHER_SIII (0x93, 16.0f, 160.0f, 10.0f, 10.0f, 0.00f, false, 30, 6, 9, "dispatcher_siii", "Dispatcher Stage III"),
    DISPATCHER_SIV  (0x94, 24.0f, 240.0f, 14.0f, 14.0f, 0.00f, false, 45, 7, 10,"dispatcher_siv",  "Dispatcher Stage IV"),

    // ────────────────────────────────────────────────────────────────
    //  NEXUS — ROOTER  (0xA0 – 0xAF)
    //  Colony nexus structures that anchor and expand.
    // ────────────────────────────────────────────────────────────────
    ROOTER_BALL     (0xA0,  6.0f,  70.0f,  4.0f, 6.0f, 0.00f, false, 10, 3, 6, "rooter_ball",   "Rooter Spore"),
    ROOTER_SI       (0xA1,  8.0f,  80.0f,  6.0f, 6.0f, 0.00f, false, 12, 4, 7, "rooter_si",     "Rooter Stage I"),
    ROOTER_SII      (0xA2, 12.0f, 120.0f,  8.0f, 8.0f, 0.00f, false, 20, 5, 8, "rooter_sii",    "Rooter Stage II"),
    ROOTER_SIII     (0xA3, 16.0f, 160.0f, 10.0f, 10.0f, 0.00f, false, 30, 6, 9, "rooter_siii",   "Rooter Stage III"),
    ROOTER_SIV      (0xA4, 24.0f, 240.0f, 14.0f, 14.0f, 0.00f, false, 45, 7, 10,"rooter_siv",    "Rooter Stage IV"),

    // ────────────────────────────────────────────────────────────────
    //  OTHER NEXUS
    // ────────────────────────────────────────────────────────────────
    KYPHOSIS        (0xA5, 10.0f, 100.0f, 10.0f, 8.0f, 0.20f, false, 15, 4, 7, "kyphosis",     "Kyphosis"),
    SENTRY          (0xA6,  6.0f,  60.0f,  6.0f, 4.0f, 0.00f, false, 10, 3, 6, "sentry",       "Sentry"),
    SEIZER          (0xA7,  8.0f,  70.0f,  8.0f, 4.0f, 0.25f, false, 12, 4, 7, "seizer",       "Seizer"),
    WORM            (0xA8,  7.0f,  50.0f,  6.0f, 2.0f, 0.30f, false, 10, 3, 7, "worm",         "Worm"),

    // ────────────────────────────────────────────────────────────────
    //  DETERRENT  (0xB0 – 0xBF)
    //  Area-denial parasites that debilitate nearby threats.
    // ────────────────────────────────────────────────────────────────
    DODT            (0xB0,  4.0f,  30.0f,  4.0f, 0.0f, 0.25f, false, 6, 3, 6, "dodt",         "Dodt"),
    LEEMB           (0xB1,  3.5f,  24.0f,  3.0f, 0.0f, 0.25f, false, 5, 3, 6, "leemb",        "Leemb"),
    NAK             (0xB2,  5.0f,  36.0f,  5.0f, 0.0f, 0.28f, false, 7, 3, 6, "nak",          "Nak"),
    ROF             (0xB3,  4.5f,  32.0f,  4.0f, 0.0f, 0.25f, false, 6, 3, 6, "rof",          "Rof"),
    TONRO           (0xB4,  6.0f,  40.0f,  6.0f, 2.0f, 0.22f, false, 8, 4, 7, "tonro",        "Tonro"),
    UNVO            (0xB5,  5.0f,  34.0f,  5.0f, 0.0f, 0.28f, false, 7, 3, 6, "unvo",         "Unvo"),

    // ────────────────────────────────────────────────────────────────
    //  PURE  (0xC0 – 0xCF)
    //  Fully self-actualised combat parasites.
    // ────────────────────────────────────────────────────────────────
    OVERSEER        (0xC0, 10.0f, 100.0f, 10.0f, 6.0f, 0.30f, false, 15, 5, 8, "overseer",       "Overseer"),
    VIGILANTE       (0xC1,  8.0f,  80.0f,  8.0f, 4.0f, 0.32f, false, 12, 5, 8, "vigilante",      "Vigilante"),
    WARDEN          (0xC2,  9.0f,  90.0f,  9.0f, 8.0f, 0.22f, false, 14, 5, 8, "warden",         "Warden"),
    BOMBER_LIGHT    (0xC3,  6.0f,  40.0f,  6.0f, 0.0f, 0.35f, false,  8, 4, 7, "bomber_light",   "Bomber Light"),
    MARAUDER        (0xC4,  9.0f,  80.0f, 10.0f, 4.0f, 0.35f, false, 14, 5, 8, "marauder",       "Marauder"),
    MONARCH         (0xC5, 12.0f, 120.0f, 12.0f, 8.0f, 0.28f, false, 20, 6, 9, "monarch",        "Monarch"),
    GRUNT           (0xC6,  6.0f,  50.0f,  7.0f, 2.0f, 0.30f, false,  8, 4, 7, "grunt",          "Grunt"),
    SEEKER          (0xC7,  7.0f,  60.0f,  7.0f, 0.0f, 0.38f, false, 10, 5, 8, "seeker",         "Seeker"),
    BOMBER_HEAVY    (0xC8,  8.0f,  60.0f,  8.0f, 2.0f, 0.28f, false, 12, 5, 8, "bomber_heavy",   "Bomber Heavy"),

    // ────────────────────────────────────────────────────────────────
    //  PREEMINENT  (0xD0 – 0xDF)
    //  Elite combat forms — commanders of lesser parasites.
    // ────────────────────────────────────────────────────────────────
    WRAITH          (0xD0, 14.0f, 140.0f, 14.0f, 6.0f, 0.35f, false, 22, 6, 9, "wraith",          "Wraith"),
    BOGLE           (0xD1, 10.0f, 100.0f, 10.0f, 4.0f, 0.30f, false, 16, 6, 9, "bogle",           "Bogle"),
    HAUNTER         (0xD2, 12.0f, 110.0f, 12.0f, 4.0f, 0.32f, false, 18, 6, 9, "haunter",         "Haunter"),
    CARRIER_COLONY  (0xD3,  8.0f,  80.0f,  6.0f, 4.0f, 0.25f, false, 14, 5, 8, "carrier_colony",  "Carrier Colony"),
    SUCCOR          (0xD4, 10.0f,  90.0f,  8.0f, 6.0f, 0.28f, false, 16, 6, 9, "succor",          "Succor"),
    ARCHITECT       (0xD5, 16.0f, 160.0f, 12.0f, 10.0f, 0.22f, false, 30, 7, 10,"architect",       "Architect"),

    // ────────────────────────────────────────────────────────────────
    //  ANCIENT  (0xE0 – 0xEF)
    //  Endgame entities — the oldest and most powerful forms.
    // ────────────────────────────────────────────────────────────────
    DREADNAUT       (0xE0, 24.0f, 300.0f, 20.0f, 16.0f, 0.25f, false, 50, 8, 10,"dreadnaut",       "Dreadnaut"),
    OVERLORD        (0xE1, 32.0f, 400.0f, 24.0f, 20.0f, 0.22f, false, 80, 9, 10,"overlord",        "Overlord"),
    POD             (0xE2, 10.0f, 200.0f,  4.0f, 8.0f,  0.00f, false, 20, 7, 10,"pod",             "Pod"),
    DREADNAUT_TEN   (0xE3, 30.0f, 500.0f, 30.0f, 20.0f, 0.28f, false,100,10, 10,"dreadnaut_ten",   "Dreadnaut Ten"),

    // ────────────────────────────────────────────────────────────────
    //  DERIVED  (0xF0 – 0xFF)
    //  Special evolutionary offshoots.
    // ────────────────────────────────────────────────────────────────
    KIRIN           (0xF0, 20.0f, 200.0f, 18.0f, 10.0f, 0.35f, false, 40, 7, 10,"kirin",         "Kirin"),
    DRACONITE       (0xF1, 28.0f, 350.0f, 22.0f, 14.0f, 0.32f, false, 60, 8, 10,"draconite",     "Draconite"),

    // ────────────────────────────────────────────────────────────────
    //  ABOMINATION  (0xFE – 0xFF)
    //  Multi-part boss entities.
    // ────────────────────────────────────────────────────────────────
    ABO_BODIES      (0xFE, 30.0f, 600.0f, 24.0f, 16.0f, 0.25f, false, 80, 9, 10,"abo_bodies",   "Abomination Bodies"),
    ABO_HEAD        (0xFF, 30.0f, 400.0f, 30.0f, 10.0f, 0.30f, false, 80, 9, 10,"abo_head",     "Abomination Head");

    /* ================================================================ */

    /** Internal type ID used for network serialisation and save-data. */
    private final byte id;

    /** How many evolution points killing this type contributes to the One Mind. */
    private final float evolutionWeight;

    /** Base maximum health. */
    private final float maxHealth;

    /** Base melee attack damage. */
    private final float attackDamage;

    /** Base natural armor points. */
    private final float armor;

    /** Base movement speed (blocks per tick). */
    private final float movementSpeed;

    /** Whether this type can naturally despawn. */
    private final boolean canDespawn;

    /** One Mind system value awarded on death. */
    private final int deathValue;

    /** Minimum evolution phase for natural spawning (inclusive). */
    private final int spawnPhaseMin;

    /** Maximum evolution phase for natural spawning (inclusive). */
    private final int spawnPhaseMax;

    /** Serialisation name (lowercase, underscore-separated). */
    private final String serialName;

    /** Human-readable display name. */
    private final String displayName;

    /* ---- static lookup table ---- */
    private static final Map<Byte, ParasiteType> ID_MAP;

    static {
        var hashMap = new java.util.HashMap<Byte, ParasiteType>();
        for (ParasiteType pt : values()) {
            hashMap.put(pt.id, pt);
        }
        ID_MAP = Collections.unmodifiableMap(hashMap);
    }

    ParasiteType(int id, float evolutionWeight, float maxHealth, float attackDamage,
                 float armor, float movementSpeed, boolean canDespawn, int deathValue,
                 int spawnPhaseMin, int spawnPhaseMax, String serialName, String displayName) {
        this.id = (byte) id;
        this.evolutionWeight = evolutionWeight;
        this.maxHealth = maxHealth;
        this.attackDamage = attackDamage;
        this.armor = armor;
        this.movementSpeed = movementSpeed;
        this.canDespawn = canDespawn;
        this.deathValue = deathValue;
        this.spawnPhaseMin = spawnPhaseMin;
        this.spawnPhaseMax = spawnPhaseMax;
        this.serialName = serialName;
        this.displayName = displayName;
    }

    // ──────────────── Accessors ────────────────

    public byte getId()                          { return id; }
    public float getEvolutionWeight()            { return evolutionWeight; }
    public float getMaxHealth()                  { return maxHealth; }
    public float getAttackDamage()               { return attackDamage; }
    public float getArmor()                      { return armor; }
    public float getMovementSpeed()              { return movementSpeed; }
    public boolean canDespawn()                  { return canDespawn; }
    public int getDeathValue()                   { return deathValue; }
    public int getSpawnPhaseMin()                { return spawnPhaseMin; }
    public int getSpawnPhaseMax()                { return spawnPhaseMax; }
    public String getDisplayName()               { return displayName; }

    // ──────────────── StringRepresentable ────────────────

    @Override
    public String getSerializedName()            { return serialName; }

    // ──────────────── Helper Methods ────────────────

    /**
     * Retrieves a {@code ParasiteType} by its internal byte ID.
     *
     * @param typeId the internal type ID
     * @return the matching constant, or {@code null} if no match exists
     */
    @Nullable
    public static ParasiteType getByTypeId(byte typeId) {
        return ID_MAP.get(typeId);
    }

    /**
     * Returns the {@link EvolutionPath} this type belongs to, derived from
     * the byte ID range.
     *
     * @return the evolution path for this type
     */
    public EvolutionPath getEvolutionTier() {
        int unsigned = id & 0xFF;
        if (unsigned <= 0x0F) return EvolutionPath.INFECTED;
        if (unsigned <= 0x1F) return EvolutionPath.SPECIAL_INFECTED;
        if (unsigned <= 0x2F) return EvolutionPath.FERAL;
        if (unsigned <= 0x3F) return EvolutionPath.HIJACKED;
        if (unsigned <= 0x4F) return EvolutionPath.INBORN;
        if (unsigned <= 0x5F) return EvolutionPath.CRUDE;
        if (unsigned <= 0x6F) return EvolutionPath.PRIMITIVE;
        if (unsigned <= 0x7F) return EvolutionPath.ADAPTED;
        if (unsigned <= 0xAF) return EvolutionPath.NEXUS;  // 0x80–0xAF: Beckon, Dispatcher, Rooter, other nexus
        if (unsigned <= 0xBF) return EvolutionPath.DETERRENT;
        if (unsigned <= 0xCF) return EvolutionPath.PURE;
        if (unsigned <= 0xDF) return EvolutionPath.PREEMINENT;
        if (unsigned <= 0xEF) return EvolutionPath.ANCIENT;
        if (unsigned <= 0xFD) return EvolutionPath.DERIVED;
        return EvolutionPath.ABOMINATION; // 0xFE–0xFF
    }

    /**
     * Returns whether this type is a colony-spawned entity (inborn, crude,
     * nexus, or advanced forms that emerge from colony structures).
     *
     * @return {@code true} if this type is produced by colonies
     */
    public boolean isColonyType() {
        EvolutionPath path = getEvolutionTier();
        return path == EvolutionPath.INBORN
                || path == EvolutionPath.CRUDE
                || path == EvolutionPath.NEXUS
                || path == EvolutionPath.PRIMITIVE
                || path == EvolutionPath.ADAPTED
                || path == EvolutionPath.PURE
                || path == EvolutionPath.PREEMINENT
                || path == EvolutionPath.ANCIENT;
    }

    /**
     * Returns an unmodifiable snapshot of this type's base combat stats.
     *
     * @return a map keyed by stat name with the base value
     */
    public Map<String, Float> getBaseStats() {
        Map<String, Float> stats = new java.util.LinkedHashMap<>();
        stats.put("maxHealth",    maxHealth);
        stats.put("attackDamage", attackDamage);
        stats.put("armor",        armor);
        stats.put("movementSpeed", movementSpeed);
        return Collections.unmodifiableMap(stats);
    }

    /**
     * Checks whether this type can naturally spawn during the given phase.
     *
     * @param phase the evolution phase to test
     * @return {@code true} if the phase is within this type's spawn range
     */
    public boolean canSpawnInPhase(EvoPhase phase) {
        if (phase == EvoPhase.NONE) return false;
        int n = phase.getPhaseNumber();
        return n >= spawnPhaseMin && n <= spawnPhaseMax;
    }
}
