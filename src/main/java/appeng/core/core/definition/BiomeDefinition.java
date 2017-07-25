package appeng.core.core.definition;

import appeng.core.core.api.definition.IBiomeDefinition;
import appeng.core.lib.definition.Definition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.Biome;
import org.apache.commons.lang3.tuple.Pair;

public class BiomeDefinition<B extends Biome> extends Definition<B> implements IBiomeDefinition<B> {

	public BiomeDefinition(ResourceLocation identifier, B biome){
		super(identifier, biome);
	}

	@Override
	public boolean isSameAs(Object other){
		// TODO 1.11.2-CD:A - Add other checks
		if(super.isSameAs(other)){
			return true;
		} else {
			if(isEnabled()){
				B biome = maybe().get();
				if(other instanceof Pair){
					Pair p = (Pair) other;
					if(p.getLeft() instanceof IBlockAccess && p.getRight() instanceof BlockPos){
						return ((IBlockAccess) p.getLeft()).getBiome((BlockPos) p.getRight()) == biome;
					}
				}
			}
			return false;
		}
	}

}
