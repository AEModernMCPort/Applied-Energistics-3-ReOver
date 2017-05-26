package appeng.core.lib.definitions;

import appeng.api.definitions.IDefinition;
import appeng.api.definitions.sub.ISubDefinition;
import net.minecraft.util.ResourceLocation;

import java.util.Optional;

public class Definition<T> implements IDefinition<T> {

	private final ResourceLocation identifier;
	private final Optional<T> t;
	private Optional<ISubDefinition<?, T, ?>> subDefinition;

	public Definition(ResourceLocation identifier, T t){
		this.identifier = identifier;
		this.t = Optional.ofNullable(t);
	}

	public <D extends Definition<T>> D setSubDefinition(ISubDefinition<?, T, ?> subDefinition){
		this.subDefinition = Optional.ofNullable(subDefinition);
		return (D) this;
	}

	@Override
	public final ResourceLocation identifier(){
		return identifier;
	}

	@Override
	public final Optional<T> maybe(){
		return t;
	}

	@Override
	public <D, P extends T, S extends ISubDefinition<D, P, S>> Optional<S> maybeSubDefinition(){
		return (Optional<S>) subDefinition;
	}

	@Override
	public boolean isEnabled(){
		return t.isPresent();
	}

	@Override
	public boolean isSameAs(Object other){
		return t.isPresent() && t.get() == other;
	}

}
