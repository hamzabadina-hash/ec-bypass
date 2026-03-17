package com.ecbypass;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EcBypassMod implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("ec_bypass");

    @Override
    public void onInitialize() {
        LOGGER.info("EC Bypass loaded.");
    }
}
