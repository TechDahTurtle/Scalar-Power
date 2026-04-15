package site.scalarstudios.scalarpower.block.machine.battery;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import site.scalarstudios.scalarpower.ScalarPower;
import site.scalarstudios.scalarpower.block.machine.MachineUtils;

public final class EnderBatteryPoolSavedData extends SavedData {
    private static final Identifier DATA_NAME = Identifier.fromNamespaceAndPath(ScalarPower.MODID, "ender_battery_pool");
    private static final Codec<EnderBatteryPoolSavedData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("energy").forGetter(EnderBatteryPoolSavedData::getStoredEnergy)
    ).apply(instance, EnderBatteryPoolSavedData::fromStoredEnergy));
    private static final SavedDataType<EnderBatteryPoolSavedData> TYPE = new SavedDataType<EnderBatteryPoolSavedData>(
            DATA_NAME,
            EnderBatteryPoolSavedData::new,
            CODEC);

    private int sharedEnergy;

    public static EnderBatteryPoolSavedData get(ServerLevel level) {
        ServerLevel storageLevel = level.getServer().overworld();
        return storageLevel.getDataStorage().computeIfAbsent(TYPE);
    }

    private static EnderBatteryPoolSavedData fromStoredEnergy(int energy) {
        EnderBatteryPoolSavedData data = new EnderBatteryPoolSavedData();
        data.sharedEnergy = clampEnergy(energy);
        return data;
    }

    private int getStoredEnergy() {
        return clampEnergy(sharedEnergy);
    }

    public int getEnergy() {
        return sharedEnergy;
    }

    public void setEnergy(int energy) {
        int sanitizedEnergy = clampEnergy(energy);
        sharedEnergy = sanitizedEnergy;
        setDirty();
    }

    private static int clampEnergy(int energy) {
        return Math.max(0, Math.min(MachineUtils.ENDER_BATTERY_CAPACITY, energy));
    }
}







