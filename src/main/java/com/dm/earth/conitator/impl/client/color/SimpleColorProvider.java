package com.dm.earth.conitator.impl.client.color;

import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

@FunctionalInterface
public interface SimpleColorProvider extends ItemColorProvider, BlockColorProvider {

	int getColor(int tintIndex);

    @Override
    default int getColor(ItemStack itemStack, int i) {
        return this.getColor(i);
    }

    @Override
    default int getColor(BlockState arg0, BlockRenderView arg1, BlockPos arg2, int arg3) {
        return this.getColor(arg3);
    }

}
