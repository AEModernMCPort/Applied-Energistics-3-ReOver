package appeng.core.skyfall.certusinfused;

import appeng.core.lib.util.BlockState2String;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class CertusInfused extends IForgeRegistryEntry.Impl<CertusInfused> {

	public final IBlockState original;

	public CertusInfused(IBlockState original){
		this.original = original;
		setRegistryName(BlockState2String.toString(this.original));
	}

}
