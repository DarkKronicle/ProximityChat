package io.github.darkkronicle.proximitychat.mixins;

import io.github.darkkronicle.proximitychat.BypassHandler;
import io.github.darkkronicle.proximitychat.ProximityChat;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.filter.TextStream;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {

    @Shadow @Final private MinecraftServer server;

    @Shadow public ServerPlayerEntity player;

    @Inject(method = "handleMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/filter/TextStream$Message;getFiltered()Ljava/lang/String;"), cancellable = true)
    private void handleMessage(TextStream.Message message, CallbackInfo ci) {
        String raw = message.getRaw();
        String filtered = message.getFiltered();
        TranslatableText rawText = raw.isEmpty() ? null : new TranslatableText("chat.type.text", this.player.getDisplayName(), raw);
        TranslatableText filteredText = filtered.isEmpty() ? null : new TranslatableText("chat.type.text", this.player.getDisplayName(), filtered);
        this.server.getPlayerManager().broadcast(filteredText, player -> {
            if (BypassHandler.getInstance().shouldBypass(player)) {
                return this.player.shouldFilterMessagesSentTo(player) ? filteredText : rawText;
            }
            if (ProximityChat.shouldSend(player, this.player)) {
                return this.player.shouldFilterMessagesSentTo(player) ? filteredText : rawText;
            }
            return null;
        }, MessageType.CHAT, this.player.getUuid());
        ci.cancel();
    }


}
