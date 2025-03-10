/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.florianmichael.viafabricplus.injection.mixin.fixes.vialegacy;

import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_19_3to1_19_1.ClientboundPackets1_19_3;
import de.florianmichael.viafabricplus.definition.classic.CustomClassicProtocolExtensions;
import de.florianmichael.viafabricplus.definition.classic.model.WeatherTypes;
import net.raphimc.vialegacy.protocols.classic.protocola1_0_15toc0_28_30.ClientboundPacketsc0_28;
import net.raphimc.vialegacy.protocols.classic.protocola1_0_15toc0_28_30.ServerboundPacketsc0_28;
import net.raphimc.vialegacy.protocols.classic.protocolc0_28_30toc0_28_30cpe.ClientboundPacketsc0_30cpe;
import net.raphimc.vialegacy.protocols.classic.protocolc0_28_30toc0_28_30cpe.Protocolc0_30toc0_30cpe;
import net.raphimc.vialegacy.protocols.classic.protocolc0_28_30toc0_28_30cpe.ServerboundPacketsc0_30cpe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Protocolc0_30toc0_30cpe.class, remap = false)
public class MixinProtocolc0_30toc0_30cpe extends AbstractProtocol<ClientboundPacketsc0_30cpe, ClientboundPacketsc0_28, ServerboundPacketsc0_30cpe, ServerboundPacketsc0_28> {

    @Inject(method = "registerPackets", at = @At("RETURN"))
    public void extendPackets(CallbackInfo ci) {
        this.registerClientbound(CustomClassicProtocolExtensions.EXT_WEATHER_TYPE, null, new PacketHandlers() {
            @Override
            public void register() {
                handler(wrapper -> {
                    wrapper.cancel();
                    final byte weatherType = wrapper.read(Type.BYTE);

                    final PacketWrapper changeRainState = PacketWrapper.create(ClientboundPackets1_19_3.GAME_EVENT, wrapper.user());
                    changeRainState.write(Type.BYTE, weatherType == WeatherTypes.SUNNY ? (byte) 2 : (byte) 1); // start raining
                    changeRainState.write(Type.FLOAT, 0F); // unused
                    changeRainState.sendRaw();

                    if (weatherType == WeatherTypes.RAINING || weatherType == WeatherTypes.SNOWING) {
                        final PacketWrapper changeRainType = PacketWrapper.create(ClientboundPackets1_19_3.GAME_EVENT, wrapper.user());
                        changeRainType.write(Type.BYTE, (byte) 7);
                        changeRainType.write(Type.FLOAT, weatherType == WeatherTypes.RAINING ? 0F : 1F);
                        changeRainType.sendRaw();
                    }
                });
            }
        });
    }
}
