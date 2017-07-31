package appeng.core.me.parts.part;

import net.minecraft.nbt.NBTTagCompound;

public class DummyState extends PartBase.StateBase<PartDummy, DummyState> {

	public DummyState(PartDummy part){
		super(part);
	}

	@Override
	public NBTTagCompound serializeNBT(){
		return null;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt){

	}
}
