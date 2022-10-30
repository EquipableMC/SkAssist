package tk.shanebee.bee.elements.nbt.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.slot.Slot;
import ch.njol.util.Kleenean;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import tk.shanebee.bee.SkBee;
import tk.shanebee.bee.api.NBT.NBTCustomBlock;
import tk.shanebee.bee.api.NBT.NBTCustomEntity;
import tk.shanebee.bee.api.NBT.NBTCustomTileEntity;
import tk.shanebee.bee.api.NBT.NBTItemType;
import tk.shanebee.bee.api.NBTApi;

import javax.annotation.Nullable;

@Name("NBT - Compound of")
@Description({"Get the nbt compound of a block/entity/item/file. This is a more advanced version of NBT than just getting an NBT string ",
        "which allows for better manipulation. Optionally you can return a copy of the compound. This way you can modify it without ",
        "actually modifying the original compound, for example when grabbing the compound from an entity, modifying it and applying to ",
        "other entities. NBT from files and items will not be the original, but will be a copy."})
@Examples({"set {_n} to nbt compound of player's tool",
        "set {_nbt} to nbt compound of target entity",
        "set {_n} to nbt compound of \"{id:\"\"minecraft:diamond_sword\"\",tag:{Damage:0,Enchantments:[{id:\"\"minecraft:sharpness\"\",lvl:3s}]},Count:1b}\"",
        "set {_nbt} to nbt compound of file \"world/playerdata/some-uuid.dat\""})
@Since("1.6.0")
public class ExprNbtCompound extends PropertyExpression<Object, NBTCompound> {

    private final static NBTApi NBT_API;

    static {
        NBT_API = SkBee.getPlugin().getNbtApi();
        Skript.registerExpression(ExprNbtCompound.class, NBTCompound.class, ExpressionType.PROPERTY,
                "[:full] nbt compound [:copy] (of|from) %blocks/entities/itemtypes/itemstacks/slots/strings%",
                "nbt compound (of|from) file[s] %strings%");
    }

    private boolean copy;
    private boolean file;
    private boolean isFullItem;

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        setExpr(exprs[0]);
        copy = parseResult.hasTag("copy");
        file = matchedPattern == 1;
        isFullItem = parseResult.hasTag("full");
        return true;
    }

    @Override
    protected NBTCompound @NotNull [] get(@NotNull Event e, Object @NotNull [] source) {
        return get(source, object -> {
            NBTCompound compound = null;
            if (object instanceof Block) {
                Block block = ((Block) object);
                BlockState state = block.getState();

                if (NBTApi.isTileEntity(state)) {
                    compound = new NBTCustomTileEntity(state);
                } else if (NBTApi.SUPPORTS_BLOCK_NBT) {
                    compound = new NBTCustomBlock(block).getData();
                }
            } else if (object instanceof Entity) {
                compound = new NBTCustomEntity(((Entity) object));
            } else if (object instanceof ItemType) {
                if (!isFullItem) {
                    compound = new NBTItemType(((ItemType) object));
                } else {
                    ItemStack stack = ((ItemType) object).getRandom();
                    if (stack != null) {
                        compound = getFromItem(stack);
                    }
                }
            } else if (object instanceof ItemStack) {
                compound = getFromItem((ItemStack) object);
            } else if (object instanceof Slot) {
                ItemStack stack = ((Slot) object).getItem();
                if (stack != null) {
                    compound = getFromItem(stack);
                }
            } else if (object instanceof String) {
                if (file) {
                    String fileNBT = NBT_API.getNBT(object, NBTApi.ObjectType.FILE);
                    if (fileNBT != null) {
                        compound = new NBTContainer(fileNBT);
                    }
                } else {
                    if (NBTApi.validateNBT(((String) object))) {
                        compound = new NBTContainer((String) object);
                    }
                }
            }
            if (compound != null) {
                if (copy) {
                    return new NBTContainer(compound.toString());
                } else {
                    return compound;
                }
            }
            return null;
        });
    }

    private NBTCompound getFromItem(ItemStack itemStack) {
        if (isFullItem) {
            return NBTItem.convertItemtoNBT(itemStack);
        }
        return new NBTItem(itemStack, true);
    }

    @Override
    public @NotNull Class<? extends NBTCompound> getReturnType() {
        return NBTCompound.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean d) {
        return "nbt compound from " + getExpr().toString(e, d);
    }

}
