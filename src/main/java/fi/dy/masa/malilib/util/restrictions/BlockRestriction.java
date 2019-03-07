package fi.dy.masa.malilib.util.restrictions;

import java.util.List;
import java.util.Set;
import fi.dy.masa.malilib.MaLiLib;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;

public class BlockRestriction extends UsageRestriction<Block>
{
    @Override
    protected void setValuesForList(Set<Block> set, List<String> names)
    {
        for (String name : names)
        {
            Block block = IRegistry.BLOCK.get(new ResourceLocation(name));

            if (block != null)
            {
                set.add(block);
            }
            else
            {
                MaLiLib.logger.warn(I18n.format("malilib.error.invalid_block_blacklist_entry", name));
            }
        }
    }
}