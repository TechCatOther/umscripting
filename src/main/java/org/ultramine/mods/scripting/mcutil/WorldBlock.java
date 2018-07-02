package org.ultramine.mods.scripting.mcutil;

import groovy.lang.Closure;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.ultramine.mods.scripting.event.EventAliasRegistry;
import org.ultramine.mods.scripting.event.IAliasedEventRegistry;
import org.ultramine.mods.scripting.event.IEventSource;
import org.ultramine.server.chunk.IChunkLoadCallback;
import org.ultramine.server.world.WorldDescriptor;

public class WorldBlock implements IEventSource, IBlockPos
{
	private final WorldDescriptor desc;
	public final int x;
	public final int y;
	public final int z;

	public WorldBlock(WorldDescriptor desc, int x, int y, int z)
	{
		this.desc = desc;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public WorldBlock(WorldServer world, int x, int y, int z)
	{
		this(world.func_73046_m().getMultiWorld().getDescFromWorld(world), x, y, z);
	}

	public WorldDescriptor getWorld()
	{
		return desc;
	}

	public WorldServer getWorldHandle()
	{
		return desc.getOrLoadWorld();
	}

	@Override
	public int getX()
	{
		return x;
	}

	@Override
	public int getY()
	{
		return y;
	}

	@Override
	public int getZ()
	{
		return z;
	}

	@Override
	public boolean isInAABB(IBlockPos min, IBlockPos max)
	{
		return x >= min.getX() && x <= max.getX() && y >= min.getY() && y <= max.getY() && z >= min.getZ() && z <= max.getZ();
	}

	public boolean exists()
	{
		return getWorldHandle().blockExists(x, y, z);
	}

	public Block getType()
	{
		return getWorldHandle().getBlock(x, y, z);
	}

	public int getMeta()
	{
		return getWorldHandle().getBlockMetadata(x, y, z);
	}

	public TileEntity getTile()
	{
		return getWorldHandle().getTileEntity(x, y, z);
	}

	public BlockState getState()
	{
		return new BlockState(getType(), getMeta());
	}

	public void getState(Closure<Void> callback)
	{
		if(exists())
			callback.call(getState());
		else
			load((c) -> callback.call(getState()));
	}

	public boolean isType(Block block)
	{
		return Block.isEqualTo(getType(), block);
	}

	public boolean isType(Block... blocks)
	{
		Block type = getType();
		for(Block block : blocks)
			if(Block.isEqualTo(type, block))
				return true;
		return false;
	}

	public boolean isType(BlockTypes block)
	{
		return block.contains(getType(), getMeta());
	}

	public boolean isType(Block block, int meta)
	{
		return isType(block) && (meta == OreDictionary.WILDCARD_VALUE || getMeta() == meta);
	}

	public boolean isType(BlockState state)
	{
		return state.isType(getType(), getMeta());
	}

	public boolean isType(BlockState... states)
	{
		Block type = getType();
		int meta = getMeta();
		for(BlockState state : states)
			if(state.isType(type, meta))
				return true;
		return false;
	}

	public void isType(BlockState state, Closure<Void> callback)
	{
		if(exists())
			callback.call(isType(state));
		else
			load((c) -> callback.call(isType(state)));
	}

	public boolean setType(Block type)
	{
		return setType(type, 0);
	}

	public boolean setType(Block type, int meta)
	{
		GameUtil.checkBlockMeta(meta);
		return getWorldHandle().setBlockSilently(x, y, z, type, meta, 3);
	}

	public boolean setType(BlockState state)
	{
		GameUtil.checkBlockMeta(state.getMeta()); //exclude wildcard value
		return getWorldHandle().setBlockSilently(x, y, z, state.getType(), state.getMeta(), 3);
	}

	public boolean setType(String type)
	{
		return setType(type, 0);
	}

	public boolean setType(String type, int meta)
	{
		GameUtil.checkBlockMeta(meta);
		return getWorldHandle().setBlockSilently(x, y, z, GameUtil.getBlock(type), meta, 3);
	}

	public boolean setMeta(int meta)
	{
		GameUtil.checkBlockMeta(meta);
		return getWorldHandle().setBlockMetadataWithNotify(x, y, z, meta, 3);
	}

	public void setType(BlockState state, Closure<Void> callback)
	{
		GameUtil.checkBlockMeta(state.getMeta()); //exclude wildcard value
		if(exists())
		{
			getWorldHandle().setBlockSilently(x, y, z, state.getType(), state.getMeta(), 3);
			callback.call();
		}
		else
			load((c) -> {
				getWorldHandle().setBlockSilently(x, y, z, state.getType(), state.getMeta(), 3);
				callback.call();
			});
	}

	public void load(IChunkLoadCallback callback)
	{
		getWorldHandle().theChunkProviderServer.loadAsync(x >> 4, z >> 4, callback);
	}

	public void harvest(boolean drop)
	{
		getWorldHandle().func_147480_a(x, y, z, drop);
	}

	public void harvest()
	{
		harvest(true);
	}

	public Object asType(Class<?> cls)
	{
		if(Block.class.isAssignableFrom(cls))
			return getType();
		else if(cls == BlockState.class)
			return new BlockState(getType(), getMeta());
		else if(cls == BlockTypes.class)
			return BlockTypes.of(getType());
		if(TileEntity.class.isAssignableFrom(cls))
			return getTile();
		if(cls == BlockSnapshot.class)
			return getSnapshot();
		if(cls == WorldSelection.class)
			return new WorldSelection(desc, new BlockPos(x, y, z), new BlockPos(x, y, z));
		if(IInventory.class.isAssignableFrom(cls))
			return getInventory();

		return null;
	}

	public IInventory getInventory()
	{
		TileEntity tile = getTile();
		if(!(tile instanceof IInventory))
			return null;
		if(tile instanceof TileEntityChest)
		{
			TileEntityChest te = (TileEntityChest)tile;
			World world = te.getWorldObj();
			int x = te.xCoord;
			int y = te.yCoord;
			int z = te.zCoord;
			if (world.getBlock(x - 1, y, z) == Blocks.chest)
				return new InventoryLargeChest("container.chestDouble", (TileEntityChest)world.getTileEntity(x - 1, y, z), te);
			else if (world.getBlock(x + 1, y, z) == Blocks.chest)
				return new InventoryLargeChest("container.chestDouble", te, (TileEntityChest)world.getTileEntity(x + 1, y, z));
			else if (world.getBlock(x, y, z - 1) == Blocks.chest)
				return new InventoryLargeChest("container.chestDouble", (TileEntityChest)world.getTileEntity(x, y, z - 1), te);
			else if (world.getBlock(x, y, z + 1) == Blocks.chest)
				return new InventoryLargeChest("container.chestDouble", te, (TileEntityChest)world.getTileEntity(x, y, z + 1));
		}

		return (IInventory)tile;
	}

	public BlockSnapshot getSnapshot()
	{
		return BlockSnapshot.getBlockSnapshot(getWorldHandle(), x, y, z);
	}

	public void sync()
	{
		getWorldHandle().markBlockForUpdate(x, y, z);
	}

	public void syncTile()
	{
		TileEntity tile = getTile();
		if(tile != null)
		{
			PlayerManager.PlayerInstance pi = getWorldHandle().getPlayerManager().getOrCreateChunkWatcher(x >> 4, z >> 4, false);
			if(pi != null)
				pi.sendToAllPlayersWatchingChunk(tile.getDescriptionPacket());
		}
	}

	public void copyTo(WorldBlock to)
	{
		to.setType(getType(), getMeta());
		TileEntity te = getTile();
		if(te != null)
		{
			NBTTagCompound nbt = new NBTTagCompound();
			te.writeToNBT(nbt);
			nbt.setInteger("x", to.x);
			nbt.setInteger("y", to.y);
			nbt.setInteger("z", to.z);

			if(nbt.getString("id").equals("savedMultipart")) //ultramine - ForgeMultipart support
			{
//				TileMultipart temp = TileMultipart.createFromNBT(tag);
//				BlockCoord coord = new BlockCoord(position.getBlockX(), position.getBlockY(), position.getBlockZ());
//				for(TMultiPart part : JavaConversions.asJavaIterable(temp.partList()))
//					TileMultipart.addPart(world, coord, part);
			}
			else
			{
				TileEntity tileEntity = TileEntity.createAndLoadEntity(nbt);
				if (tileEntity != null) {
					to.getWorldHandle().setTileEntity(to.x, to.y, to.z, tileEntity);
				}
			}
		}
	}

	public WorldBlock offset(int ox, int oy, int oz)
	{
		return new WorldBlock(desc, x + ox, y + oy, z + oz);
	}

	public WorldBlock offset(ForgeDirection dir)
	{
		return new WorldBlock(desc, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
	}

	public WorldBlock offset(ForgeDirection dir, int amount)
	{
		return new WorldBlock(desc, x + dir.offsetX*amount, y + dir.offsetY*amount, z + dir.offsetZ*amount);
	}

	@Override
	public IAliasedEventRegistry getOn()
	{
		return (script, type, prior) -> EventAliasRegistry.createWorldBlockProxy(script, type, prior, this);
	}

	public String toString()
	{
		return "WorldBlock["+desc.getDimension()+"]("+x+", "+y+", "+z+")";
	}

	public Object methodMissing(String name, Object args)
	{
		Object[] argsArr = InvokerHelper.asArray(args);
		Object[] argsArr2 = new Object[argsArr.length + 3];
		if(argsArr.length != 0)
			System.arraycopy(argsArr, 0, argsArr2, 3, argsArr.length);
		argsArr2[0] = x;
		argsArr2[1] = y;
		argsArr2[2] = z;
		return InvokerHelper.invokeMethod(getWorldHandle(), name, argsArr2);
	}
}
