package com.ecbypass;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;

public class EcBypassClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        // Register /ec as a pure client-side command
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                ClientCommandManager.literal("ec")
                    .executes(context -> {
                        MinecraftClient client = MinecraftClient.getInstance();

                        if (client.player == null) return 0;

                        // Open a local 3-row inventory that mirrors enderchest slots
                        client.player.openHandledScreen(
                            new SimpleNamedScreenHandlerFactory(
                                (syncId, inv, player) -> {
                                    // Pull actual enderchest inventory from player
                                    SimpleInventory ecInv = new SimpleInventory(27);
                                    for (int i = 0; i < 27; i++) {
                                        ecInv.setStack(i, player.getEnderChestInventory().getStack(i));
                                    }

                                    // Sync changes back when screen closes
                                    ecInv.addListener(sender -> {
                                        for (int i = 0; i < 27; i++) {
                                            player.getEnderChestInventory().setStack(i, ecInv.getStack(i));
                                        }
                                    });

                                    return new GenericContainerScreenHandler(
                                        ScreenHandlerType.GENERIC_9X3,
                                        syncId,
                                        inv,
                                        ecInv,
                                        3
                                    );
                                },
                                Text.literal("Ender Chest")
                            )
                        );
                        return 1;
                    })
            );
        });
    }
}
