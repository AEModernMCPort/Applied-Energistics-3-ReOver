package appeng.core.core.know;

import appeng.core.core.AppEngCore;
import appeng.core.core.api.know.IKnow;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class KnowImpl implements IKnow {

	protected Set<String> know = new HashSet<>();

	@Override
	public boolean isAware(String know){
		return AppEngCore.INSTANCE.getEternalWiki().isTerminal(know) ? this.know.contains(know) : AppEngCore.INSTANCE.getEternalWiki().getPieces(know).anyMatch(this::isAware);
	}

	@Override
	public boolean doesKnow(String know){
		return AppEngCore.INSTANCE.getEternalWiki().isTerminal(know) ? this.know.contains(know) : AppEngCore.INSTANCE.getEternalWiki().getPieces(know).allMatch(this::doesKnow);
	}

	@Override
	public void learn(String know){
		if(AppEngCore.INSTANCE.getEternalWiki().isTerminal(know)) this.know.add(know);
		else AppEngCore.INSTANCE.getEternalWiki().getPieces(know).forEach(this::learn);
	}

	@Override
	public void forget(String know){
		if(AppEngCore.INSTANCE.getEternalWiki().isTerminal(know)) this.know.remove(know);
		else  AppEngCore.INSTANCE.getEternalWiki().getPieces(know).forEach(this::forget);
	}

	@Override
	public NBTTagCompound serializeNBT(){
		NBTTagCompound nbt = new NBTTagCompound();
		NBTTagList know = new NBTTagList();
		this.know.forEach(k -> know.appendTag(new NBTTagString(k)));
		nbt.setTag("know", know);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt){
		know = StreamSupport.stream(((Iterable<NBTTagString>) nbt.getTag("know")).spliterator(), false).map(NBTTagString::getString).collect(Collectors.toCollection(HashSet::new));
	}

}
