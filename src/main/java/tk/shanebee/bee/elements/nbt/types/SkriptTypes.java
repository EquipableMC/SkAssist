package tk.shanebee.bee.elements.nbt.types;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.classes.Serializer;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.yggdrasil.Fields;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.shanebee.bee.api.NBT.NBTCustomType;

import java.io.StreamCorruptedException;

@SuppressWarnings({"unused", "NullableProblems"})
public class SkriptTypes {

    static {
        Classes.registerClass(new ClassInfo<>(NBTCustomType.class, "nbttype")
                .user("nbt ?types?")
                .name("NBT - Tag Type")
                .description("Represents a type of NBT tag.")
                .usage(NBTCustomType.getNames())
                .examples("set byte tag \"points\" of {_nbt} to 1",
                        "set compound tag \"tool\" of {_nbt} to nbt compound of player's tool")
                .since("1.10.0")
                .parser(new Parser<NBTCustomType>() {

                    @Nullable
                    @Override
                    public NBTCustomType parse(String s, ParseContext context) {
                        return NBTCustomType.fromName(s);
                    }

                    @Override
                    public String toString(NBTCustomType nbtCustomType, int i) {
                        return nbtCustomType.getName();
                    }

                    @Override
                    public String toVariableNameString(NBTCustomType nbtCustomType) {
                        return toString(nbtCustomType, 0);
                    }
                }));
        Classes.registerClass(new ClassInfo<>(NBTCompound.class, "nbtcompound")
                .user("nbt ?compound")
                .name("NBT - Compound")
                .description("Represents the NBT compound of an entity/block/item.")
                .usage("{id:\"minecraft:netherite_axe\",tag:{Damage:0,Enchantments:[{id:\"minecraft:unbreaking\",lvl:2s}]},Count:1b}")
                .examples("set {_a} to nbt compound of player")
                .since("1.6.0")
                .parser(new Parser<NBTCompound>() {

                    @Override
                    public boolean canParse(@NotNull ParseContext context) {
                        return false;
                    }

                    @Override
                    public String toString(@NotNull NBTCompound nbt, int flags) {
                        return nbt.toString();
                    }

                    @Override
                    public String toVariableNameString(@NotNull NBTCompound nbt) {
                        return "nbt:" + nbt.toString();
                    }
                })
                .serializer(new Serializer<NBTCompound>() {
                    @Override
                    public @NotNull Fields serialize(@NotNull NBTCompound nbt) {
                        Fields fields = new Fields();
                        fields.putObject("nbt", nbt.toString());
                        return fields;
                    }

                    @Override
                    public void deserialize(@NotNull NBTCompound o, @NotNull Fields f) {
                        assert false;
                    }

                    @Override
                    protected NBTCompound deserialize(@NotNull Fields fields) throws StreamCorruptedException {
                        String nbt = fields.getObject("nbt", String.class);
                        assert nbt != null;
                        try {
                            return new NBTContainer(nbt);
                        } catch (IllegalArgumentException ex) {
                            throw new StreamCorruptedException("Invalid nbt data: " + nbt);
                        }
                    }

                    @Override
                    public boolean mustSyncDeserialization() {
                        return true;
                    }

                    @Override
                    protected boolean canBeInstantiated() {
                        return false;
                    }
                }));
    }

}
