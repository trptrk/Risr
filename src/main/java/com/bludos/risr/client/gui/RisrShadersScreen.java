package com.bludos.risr.client.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class RisrShadersScreen extends Screen {
    private final Screen parent;
    private boolean shadersEnabled = true;

    public RisrShadersScreen(Screen parent) {
        super(Text.literal("Shader Packs"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Shaders: " + (shadersEnabled ? "ON" : "OFF")), button -> {
            shadersEnabled = !shadersEnabled;
            button.setMessage(Text.literal("Shaders: " + (shadersEnabled ? "ON" : "OFF")));
        }).dimensions(centerX - 100, centerY - 40, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Shader Options..."), button -> {
            // Open individual shader settings menu
        }).dimensions(centerX - 100, centerY - 10, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Done"), button -> {
            if (this.client != null) {
                this.client.setScreen(this.parent);
            }
        }).dimensions(centerX - 100, centerY + 20, 200, 20).build());
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(this.parent);
        }
    }
}
