package fi.dy.masa.malilib.util;

import javax.annotation.Nullable;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.interfaces.IRangeChangeListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;

public class LayerRange
{
    public static final int WORLD_HORIZONTAL_SIZE_MAX =  30000000;
    public static final int WORLD_HORIZONTAL_SIZE_MIN = -30000000;
    public static final int WORLD_VERTICAL_SIZE_MAX = 255;
    public static final int WORLD_VERTICAL_SIZE_MIN = 0;

    protected final IRangeChangeListener refresher;
    protected LayerMode layerMode = LayerMode.ALL;
    protected EnumFacing.Axis axis = EnumFacing.Axis.Y;
    protected int layerSingle = 0;
    protected int layerAbove = 0;
    protected int layerBelow = 0;
    protected int layerRangeMin = 0;
    protected int layerRangeMax = 0;
    protected boolean hotkeyRangeMin;
    protected boolean hotkeyRangeMax;

    public LayerRange(IRangeChangeListener refresher)
    {
        this.refresher = refresher;
    }

    public LayerMode getLayerMode()
    {
        return this.layerMode;
    }

    public EnumFacing.Axis getAxis()
    {
        return this.axis;
    }

    public boolean getMoveLayerRangeMin()
    {
        return this.hotkeyRangeMin;
    }

    public boolean getMoveLayerRangeMax()
    {
        return this.hotkeyRangeMax;
    }

    public void toggleHotkeyMoveRangeMin()
    {
        this.hotkeyRangeMin = ! this.hotkeyRangeMin;
    }

    public void toggleHotkeyMoveRangeMax()
    {
        this.hotkeyRangeMax = ! this.hotkeyRangeMax;
    }

    public int getLayerSingle()
    {
        return this.layerSingle;
    }

    public int getLayerAbove()
    {
        return this.layerAbove;
    }

    public int getLayerBelow()
    {
        return this.layerBelow;
    }

    public int getLayerRangeMin()
    {
        return this.layerRangeMin;
    }

    public int getLayerRangeMax()
    {
        return this.layerRangeMax;
    }

    public int getLayerMin()
    {
        switch (this.layerMode)
        {
            case ALL:
                return getWorldMinValueForAxis(this.axis);
            case SINGLE_LAYER:
                return this.layerSingle;
            case ALL_ABOVE:
                return this.layerAbove;
            case ALL_BELOW:
                return getWorldMinValueForAxis(this.axis);
            case LAYER_RANGE:
                return this.layerRangeMin;
            default:
                return WORLD_HORIZONTAL_SIZE_MIN;
        }
    }

    public int getLayerMax()
    {
        switch (this.layerMode)
        {
            case ALL:
                return getWorldMaxValueForAxis(this.axis);
            case SINGLE_LAYER:
                return this.layerSingle;
            case ALL_ABOVE:
                return getWorldMaxValueForAxis(this.axis);
            case ALL_BELOW:
                return this.layerBelow;
            case LAYER_RANGE:
                return this.layerRangeMax;
            default:
                return WORLD_HORIZONTAL_SIZE_MAX;
        }
    }

    public int getCurrentLayerValue(boolean isSecondValue)
    {
        switch (this.layerMode)
        {
            case SINGLE_LAYER:
                return this.layerSingle;

            case ALL_ABOVE:
                return this.layerAbove;

            case ALL_BELOW:
                return this.layerBelow;

            case LAYER_RANGE:
                return isSecondValue ? this.layerRangeMax : this.layerRangeMin;

            default:
                return 0;
        }
    }

    public static int getWorldMinValueForAxis(EnumFacing.Axis axis)
    {
        switch (axis)
        {
            case Y:
                return WORLD_VERTICAL_SIZE_MIN;
            case X:
            case Z:
            default:
                return WORLD_HORIZONTAL_SIZE_MIN;
        }
    }

    public static int getWorldMaxValueForAxis(EnumFacing.Axis axis)
    {
        switch (axis)
        {
            case Y:
                return WORLD_VERTICAL_SIZE_MAX;
            case X:
            case Z:
            default:
                return WORLD_HORIZONTAL_SIZE_MAX;
        }
    }

    public void setLayerMode(LayerMode mode)
    {
        this.setLayerMode(mode, true);
    }

    public void setLayerMode(LayerMode mode, boolean printMessage)
    {
        this.layerMode = mode;

        this.refresher.updateAll();

        if (printMessage)
        {
            String val = TextFormatting.GREEN.toString() + mode.getDisplayName();
            InfoUtils.printActionbarMessage("malilib.message.set_layer_mode_to", val);
        }
    }

    public void setAxis(EnumFacing.Axis axis)
    {
        this.axis = axis;

        this.refresher.updateAll();
        String val = TextFormatting.GREEN.toString() + axis.getName();
        InfoUtils.printActionbarMessage("malilib.message.set_layer_axis_to", val);
    }

    public void setLayerSingle(int layer)
    {
        int old = this.layerSingle;
        layer = this.getWorldLimitsClampedValue(layer);

        if (layer != old)
        {
            
        }
        this.markAffectedLayersForRenderUpdate();
        this.layerSingle = this.getWorldLimitsClampedValue(layer);
        this.markAffectedLayersForRenderUpdate();
    }

    public void setLayerAbove(int layer)
    {
        this.markAffectedLayersForRenderUpdate();
        this.layerAbove = this.getWorldLimitsClampedValue(layer);
        this.markAffectedLayersForRenderUpdate();
    }

    public void setLayerBelow(int layer)
    {
        this.markAffectedLayersForRenderUpdate();
        this.layerBelow = this.getWorldLimitsClampedValue(layer);
        this.markAffectedLayersForRenderUpdate();
    }

    public boolean setLayerRangeMin(int layer)
    {
        return this.setLayerRangeMin(layer, false);
    }

    protected boolean setLayerRangeMin(int layer, boolean force)
    {
        int old = this.layerRangeMin;

        this.markAffectedLayersForRenderUpdate();
        this.layerRangeMin = this.getWorldLimitsClampedValue(layer);

        if (force == false)
        {
            this.layerRangeMin = MathHelper.clamp(this.layerRangeMin, this.layerRangeMin, this.layerRangeMax);
        }

        this.markAffectedLayersForRenderUpdate();

        return this.layerRangeMin != old;
    }

    public boolean setLayerRangeMax(int layer)
    {
        return this.setLayerRangeMax(layer, false);
    }

    protected boolean setLayerRangeMax(int layer, boolean force)
    {
        int old = this.layerRangeMax;

        this.markAffectedLayersForRenderUpdate();
        this.layerRangeMax = this.getWorldLimitsClampedValue(layer);

        if (force == false)
        {
            this.layerRangeMax = MathHelper.clamp(this.layerRangeMax, this.layerRangeMin, this.layerRangeMax);
        }

        this.markAffectedLayersForRenderUpdate();

        return this.layerRangeMax != old;
    }

    public void setToPosition(Entity entity)
    {
        int pos = 0;

        switch (this.axis)
        {
            case X:
                pos = (int) entity.posX;
                break;
            case Y:
                pos = (int) entity.posY;
                break;
            case Z:
                pos = (int) entity.posZ;
                break;
        }

        switch (this.layerMode)
        {
            case SINGLE_LAYER:
                this.setLayerSingle(pos);
                break;
            case ALL_ABOVE:
                this.setLayerAbove(pos);
                break;
            case ALL_BELOW:
                this.setLayerBelow(pos);
                break;
            case LAYER_RANGE:
                this.setLayerRangeMin(pos, true);
                this.setLayerRangeMax(pos, true);
                break;
            default:
        }
    }

    protected void markAffectedLayersForRenderUpdate()
    {
        int val1;
        int val2;

        switch (this.layerMode)
        {
            case ALL:
                this.refresher.updateAll();
                return;
            case SINGLE_LAYER:
            {
                val1 = this.layerSingle - 1;
                val2 = this.layerSingle + 1;
                break;
            }
            case ALL_ABOVE:
            {
                val1 = this.layerAbove - 1;
                val2 = this.axis == EnumFacing.Axis.Y ? WORLD_VERTICAL_SIZE_MAX : WORLD_HORIZONTAL_SIZE_MAX;
                break;
            }
            case ALL_BELOW:
            {
                val1 = this.axis == EnumFacing.Axis.Y ? WORLD_VERTICAL_SIZE_MIN : WORLD_HORIZONTAL_SIZE_MIN;
                val2 = this.layerBelow + 1;
                break;
            }
            case LAYER_RANGE:
            {
                val1 = this.layerRangeMin - 1;
                val2 = this.layerRangeMax + 1;
                break;
            }
            default:
                return;
        }

        switch (this.axis)
        {
            case X:
                this.refresher.updateBetweenX(val1, val2);
                break;
            case Y:
                this.refresher.updateBetweenY(val1, val2);
                break;
            case Z:
                this.refresher.updateBetweenZ(val1, val2);
                break;
        }
    }

    public boolean moveLayer(int amount)
    {
        String axisName = this.axis.getName().toLowerCase();
        String strTo = GuiBase.TXT_GREEN + axisName + " = ";

        switch (this.layerMode)
        {
            case ALL:
                return false;
            case SINGLE_LAYER:
            {
                this.setLayerSingle(this.layerSingle + amount);
                String val = strTo + this.layerSingle;
                InfoUtils.printActionbarMessage("malilib.message.set_layer_to", val);
                break;
            }
            case ALL_ABOVE:
            {
                this.setLayerAbove(this.layerAbove + amount);
                String val = strTo + this.layerAbove;
                InfoUtils.printActionbarMessage("malilib.message.moved_min_layer_to", val);
                break;
            }
            case ALL_BELOW:
            {
                this.setLayerBelow(this.layerBelow + amount);
                String val = strTo + this.layerBelow;
                InfoUtils.printActionbarMessage("malilib.message.moved_max_layer_to", val);
                break;
            }
            case LAYER_RANGE:
            {
                EntityPlayer player = Minecraft.getInstance().player;

                if (player != null)
                {
                    double playerPos = this.axis == Axis.Y ? player.posY : (this.axis == Axis.X ? player.posX : player.posZ);
                    double min = this.layerRangeMin + 0.5D;
                    double max = this.layerRangeMax + 0.5D;
                    boolean minClosest = (Math.abs(playerPos - min) < Math.abs(playerPos - max)) || playerPos < min;
                    boolean moveMin = this.hotkeyRangeMin || (minClosest          && this.hotkeyRangeMax == false);
                    boolean moveMax = this.hotkeyRangeMax || (minClosest == false && this.hotkeyRangeMin == false);
                    boolean moved = false;

                    if (moveMin)
                    {
                        moved |= this.setLayerRangeMin(this.layerRangeMin + amount);
                    }

                    if (moveMax)
                    {
                        moved |= this.setLayerRangeMax(this.layerRangeMax + amount);
                    }

                    if (moved)
                    {
                        if (moveMin && moveMax)
                        {
                            InfoUtils.printActionbarMessage("malilib.message.moved_layer_range", String.valueOf(amount), axisName);
                        }
                        else
                        {
                            String val1 = moveMin ? I18n.format("malilib.message.layer_range.range_min") : I18n.format("malilib.message.layer_range.range_max");
                            InfoUtils.printActionbarMessage("malilib.message.moved_layer_range_boundary", val1, String.valueOf(amount), axisName);
                        }
                    }
                }

                break;
            }
            default:
        }

        return true;
    }

    public String getCurrentLayerString()
    {
        switch (this.layerMode)
        {
            case ALL:
                return "";

            case SINGLE_LAYER:
                return String.valueOf(this.layerSingle);

            case ALL_ABOVE:
                return String.valueOf(this.layerAbove);

            case ALL_BELOW:
                return String.valueOf(this.layerBelow);

            case LAYER_RANGE:
                return String.format("%d ... %s", this.layerRangeMin, this.layerRangeMax);

            default:
                return "";
        }
    }

    protected int getWorldLimitsClampedValue(int value)
    {
        if (this.axis == EnumFacing.Axis.Y)
        {
            return MathHelper.clamp(value, WORLD_VERTICAL_SIZE_MIN, WORLD_VERTICAL_SIZE_MAX);
        }
        else
        {
            return MathHelper.clamp(value, WORLD_HORIZONTAL_SIZE_MIN, WORLD_HORIZONTAL_SIZE_MAX);
        }
    }

    public boolean isPositionWithinRange(BlockPos pos)
    {
        return this.isPositionWithinRange(pos.getX(), pos.getY(), pos.getZ());
    }

    public boolean isPositionWithinRange(int x, int y, int z)
    {
        switch (this.layerMode)
        {
            case ALL:
                return true;

            case SINGLE_LAYER:
            {
                switch (this.axis)
                {
                    case X:
                        return x == this.layerSingle;
                    case Y:
                        return y == this.layerSingle;
                    case Z:
                        return z == this.layerSingle;
                }

                break;
            }

            case ALL_ABOVE:
            {
                switch (this.axis)
                {
                    case X:
                        return x >= this.layerAbove;
                    case Y:
                        return y >= this.layerAbove;
                    case Z:
                        return z >= this.layerAbove;
                }

                break;
            }

            case ALL_BELOW:
            {
                switch (this.axis)
                {
                    case X:
                        return x <= this.layerBelow;
                    case Y:
                        return y <= this.layerBelow;
                    case Z:
                        return z <= this.layerBelow;
                }

                break;
            }

            case LAYER_RANGE:
            {
                switch (this.axis)
                {
                    case X:
                        return x >= this.layerRangeMin && x <= this.layerRangeMax;
                    case Y:
                        return y >= this.layerRangeMin && y <= this.layerRangeMax;
                    case Z:
                        return z >= this.layerRangeMin && z <= this.layerRangeMax;
                }

                break;
            }
        }

        return false;
    }

    public boolean isPositionAtRenderEdgeOnSide(BlockPos pos, EnumFacing side)
    {
        switch (this.axis)
        {
            case X:
                return (side == EnumFacing.WEST && pos.getX() == this.getLayerMin()) || (side == EnumFacing.EAST && pos.getX() == this.getLayerMax());
            case Y:
                return (side == EnumFacing.DOWN && pos.getY() == this.getLayerMin()) || (side == EnumFacing.UP && pos.getY() == this.getLayerMax());
            case Z:
                return (side == EnumFacing.NORTH && pos.getZ() == this.getLayerMin()) || (side == EnumFacing.SOUTH && pos.getZ() == this.getLayerMax());
            default:
                return false;
        }
    }

    public boolean intersects(SubChunkPos pos)
    {
        switch (this.axis)
        {
            case X:
            {
                final int xMin = (pos.getX() << 4);
                final int xMax = (pos.getX() << 4) + 15;
                return (xMax < this.getLayerMin() || xMin > this.getLayerMax()) == false;
            }
            case Y:
            {
                final int yMin = (pos.getY() << 4);
                final int yMax = (pos.getY() << 4) + 15;
                return (yMax < this.getLayerMin() || yMin > this.getLayerMax()) == false;
            }
            case Z:
            {
                final int zMin = (pos.getZ() << 4);
                final int zMax = (pos.getZ() << 4) + 15;
                return (zMax < this.getLayerMin() || zMin > this.getLayerMax()) == false;
            }
            default:
                return false;
        }
    }

    public boolean intersects(AxisAlignedBB box)
    {
        switch (this.axis)
        {
            case X:
            {
                return (box.maxX < this.getLayerMin() || box.minX > this.getLayerMax()) == false;
            }
            case Y:
            {
                return (box.maxY < this.getLayerMin() || box.minY > this.getLayerMax()) == false;
            }
            case Z:
            {
                return (box.maxZ < this.getLayerMin() || box.minZ > this.getLayerMax()) == false;
            }
            default:
                return false;
        }
    }

    public int getClampedValue(int value, EnumFacing.Axis type)
    {
        if (this.axis == type)
        {
            return MathHelper.clamp(value, this.getLayerMin(), this.getLayerMax());
        }

        return MathHelper.clamp(value, getWorldMinValueForAxis(type), getWorldMaxValueForAxis(type));
    }

    @Nullable
    public AxisAlignedBB getClampedRenderBoundingBox(AxisAlignedBB box)
    {
        if (this.intersects(box) == false)
        {
            return null;
        }

        switch (this.axis)
        {
            case X:
            {
                final double xMin = Math.max(box.minX, this.getLayerMin());
                final double xMax = Math.min(box.maxX, this.getLayerMax());
                return new AxisAlignedBB(xMin, box.minY, box.minZ, xMax, box.maxY, box.maxZ);
            }
            case Y:
            {
                final double yMin = Math.max(box.minY, this.getLayerMin());
                final double yMax = Math.min(box.maxY, this.getLayerMax());
                return new AxisAlignedBB(box.minX, yMin, box.minZ, box.maxX, yMax, box.maxZ);
            }
            case Z:
            {
                final double zMin = Math.max(box.minZ, this.getLayerMin());
                final double zMax = Math.min(box.maxZ, this.getLayerMax());
                return new AxisAlignedBB(box.minX, box.minY, zMin, box.maxX, box.maxY, zMax);
            }
            default:
                return null;
        }
    }

    public JsonObject toJson()
    {
        JsonObject obj = new JsonObject();

        obj.add("mode", new JsonPrimitive(this.layerMode.name()));
        obj.add("axis", new JsonPrimitive(this.axis.name()));
        obj.add("layer_single", new JsonPrimitive(this.layerSingle));
        obj.add("layer_above", new JsonPrimitive(this.layerAbove));
        obj.add("layer_below", new JsonPrimitive(this.layerBelow));
        obj.add("layer_range_min", new JsonPrimitive(this.layerRangeMin));
        obj.add("layer_range_max", new JsonPrimitive(this.layerRangeMax));
        obj.add("hotkey_range_min", new JsonPrimitive(this.hotkeyRangeMin));
        obj.add("hotkey_range_max", new JsonPrimitive(this.hotkeyRangeMax));

        return obj;
    }

    public static LayerRange createFromJson(JsonObject obj, IRangeChangeListener refresher)
    {
        LayerRange range = new LayerRange(refresher);
        range.fromJson(obj);
        return range;
    }

    public void fromJson(JsonObject obj)
    {
        this.layerMode = LayerMode.fromStringStatic(JsonUtils.getString(obj, "mode"));
        this.axis = EnumFacing.Axis.byName(JsonUtils.getString(obj, "axis"));
        if (this.axis == null) { this.axis = EnumFacing.Axis.Y; }

        this.layerSingle = JsonUtils.getInteger(obj, "layer_single");
        this.layerAbove = JsonUtils.getInteger(obj, "layer_above");
        this.layerBelow = JsonUtils.getInteger(obj, "layer_below");
        this.layerRangeMin = JsonUtils.getInteger(obj, "layer_range_min");
        this.layerRangeMax = JsonUtils.getInteger(obj, "layer_range_max");
        this.hotkeyRangeMin = JsonUtils.getBoolean(obj, "hotkey_range_min");
        this.hotkeyRangeMax = JsonUtils.getBoolean(obj, "hotkey_range_max");
    }
}
