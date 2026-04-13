package site.scalarstudios.scalarpower.machines;

/**
 * Utility class containing power capacity and input/output configuration values for all machines.
 *
 * @author Lemon_Juiced
 */
public final class MachineUtils {
    // Generators
    public static final int GENERAL_GENERATOR_CAPACITY = 20_000;
    public static final int GENERAL_GENERATOR_SPU_PER_SIDE = 80;
    public static final int COAL_GENERATOR_SPU_PER_TICK = 40;
    public static final int GEOTHERMAL_GENERATOR_TANK_CAPACITY_MB = 4_000;
    public static final int GEOTHERMAL_GENERATOR_SPU_PER_TICK = 40;
    public static final int GEOTHERMAL_GENERATOR_MB_PER_TICK = 1;
    public static final int BAROMETRIC_GENERATOR_Y_STEP = 10;
    public static final int BAROMETRIC_GENERATOR_SPU_PER_Y_STEP = 5;
    public static final double BAROMETRIC_GENERATOR_BAD_WEATHER_MULTIPLIER = 1.5D;
    public static final int BAROMETRIC_GENERATOR_SPU_PER_SIDE = 240; // Potential max of 240 SPU/t in bad weather at y = 320
    public static final int ENTROPY_GENERATOR_SPU_PER_SIDE = 400; // Potential max of 390 SPU/t

    // Machines
    public static final int BASIC_MACHINE_CAPACITY = 20_000;
    public static final int BASIC_MACHINE_SPU_PER_TICK = 20;
    public static final int BASIC_MACHINE_SPU_PER_SIDE = 40;
    public static final int BASIC_MACHINE_TIME_PER_CRAFT = 100;
    public static final int STEEL_MACHINE_CAPACITY = 40_000;
    public static final int STEEL_MACHINE_SPU_PER_TICK_PER_LANE = 30;
    public static final int STEEL_MACHINE_SPU_PER_SIDE = 120; // Steel Machines have 2 lanes (double 60)
    public static final int STEEL_MACHINE_TIME_PER_CRAFT = 50;
    /* Alloy Smelter */
    public static final int ALLOY_SMELTER_TIME_PER_CRAFT = 120; // Slightly longer than normal machines
    /* Powered Furnace */
    public static final int POWERED_FURNACE_TIME_PER_CRAFT = 200; // Matches vanilla furnace
    public static final int STEEL_POWERED_FURNACE_TIME_PER_CRAFT = 100;

    // Batteries
    public static final int BASIC_BATTERY_CAPACITY = 1_000_000;
    public static final int BASIC_BATTERY_SPU_PER_SIDE = 100;
    public static final int STEEL_BATTERY_CAPACITY = 10_000_000;
    public static final int STEEL_BATTERY_SPU_PER_SIDE = 800;
    public static final int ENDER_BATTERY_CAPACITY = STEEL_BATTERY_CAPACITY * 10;
    public static final int ENDER_BATTERY_SPU_PER_SIDE = STEEL_BATTERY_SPU_PER_SIDE * 10;

    // Wires
    public static final int WIRE_BASE_CAPACITY = 256;
    public static final int WIRE_BASE_THROUGHPUT = 80;
    //public static final int WIRE_T1_MULTIPLIER = 1; // This is multiplying by 1, here for completeness. DO NOT USE.
    public static final int WIRE_T2_MULTIPLIER = 2;
    public static final int WIRE_T3_MULTIPLIER = WIRE_T2_MULTIPLIER * 2;
    public static final int WIRE_T4_MULTIPLIER = WIRE_T3_MULTIPLIER * 2;
}
