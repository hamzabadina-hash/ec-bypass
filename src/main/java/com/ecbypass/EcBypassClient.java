package com.ecbypass;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class EcBypassClient implements ClientModInitializer {

    private static KeyBinding openEcKey;

    @Override
    public void onInitializeClient() {

        // Register keybind — default key is B
        openEcKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.ecbypass.open",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            "category.ecbypass"
        ));

        // Register /ecopen as client-side command backup
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                ClientCommandManager.literal("ecopen")
                    .executes(context -> {
                        openEnderChest();
                        return 1;
                    })
            );
        });

        // Check keybind every tick
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openEcKey.wasPressed()) {
                openEnderChest();
            }
        });
    }

    private static void openEnderChest() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;

        // Build a local copy of the enderchest inventory
        SimpleInventory ecInv = new SimpleInventory(27);
        for (int i = 0; i < 27; i++) {
            ecInv.setStack(i, client.player.getEnderChestInventory().getStack(i).copy());
        }

        // Sync changes back to actual enderchest on every change
        ecInv.addListener(sender -> {
            for (int i = 0; i < 27; i++) {
                client.player.getEnderChestInventory().setStack(i, ecInv.getStack(i).copy());
            }
        });

        // Open the screen
        client.player.openHandledScreen(
            new SimpleNamedScreenHandlerFactory(
                (syncId, inv, player) ->
                    new GenericContainerScreenHandler(
                        ScreenHandlerType.GENERIC_9X3,
                        syncId,
                        inv,
                        ecInv,
                        3
                    ),
                Text.literal("Ender Chest")
            )
        );
    }
}
