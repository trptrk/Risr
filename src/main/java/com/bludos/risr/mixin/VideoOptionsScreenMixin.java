package com.bludos.risr.mixin;

import com.bludos.risr.client.gui.RisrShadersScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.VideoOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VideoOptionsScreen.class)
public abstract class VideoOptionsScreenMixin extends Screen {

    protected VideoOptionsScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "addOptions", at = @At("RETURN"), remap = true)
    protected void addShadersButton(CallbackInfo ci) {
        int x = this.width / 2 - 155; // Adjust position based on UI layout
        int y = this.height / 6 + 168; // Below the main buttons

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Shaders..."), button -> {
            if (this.client != null) {
                this.client.setScreen(new RisrShadersScreen(this));
            }
        }).dimensions(x, y, 150, 20).build());
    }
}
