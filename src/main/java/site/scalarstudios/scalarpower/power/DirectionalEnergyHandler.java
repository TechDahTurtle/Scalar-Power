package site.scalarstudios.scalarpower.power;

import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public final class DirectionalEnergyHandler {
    private DirectionalEnergyHandler() {
    }

    public static EnergyHandler receiveOnly(EnergyHandler delegate) {
        if (delegate == null) {
            return null;
        }
        return new ReceiveOnlyEnergyHandler(delegate);
    }

    private record ReceiveOnlyEnergyHandler(EnergyHandler delegate) implements EnergyHandler {
        @Override
        public long getAmountAsLong() {
            return delegate.getAmountAsLong();
        }

        @Override
        public long getCapacityAsLong() {
            return delegate.getCapacityAsLong();
        }

        @Override
        public int insert(int amount, TransactionContext transaction) {
            return delegate.insert(amount, transaction);
        }

        @Override
        public int extract(int amount, TransactionContext transaction) {
            return 0;
        }
    }
}

