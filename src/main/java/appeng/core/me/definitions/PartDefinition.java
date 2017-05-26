package appeng.core.me.definitions;


import java.util.Optional;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import appeng.api.definitions.IItemDefinition;
import appeng.core.lib.definitions.Definition;
import appeng.core.me.AppEngME;
import appeng.core.me.api.definitions.IPartDefinition;
import appeng.core.me.api.part.PartRegistryEntry;
import appeng.core.me.api.parts.IPart;


public class PartDefinition<P extends PartRegistryEntry> extends Definition<P> implements IPartDefinition<P>
{

	private final Optional item;

	public PartDefinition( ResourceLocation identifier, P t, IItemDefinition<?> item )
	{
		super( identifier, t );
		this.item = Optional.ofNullable( item );
	}

	@Override
	public <I extends Item> Optional<IItemDefinition<I>> maybeItem()
	{
		return item;
	}

	@Override
	public boolean isSameAs( Object other )
	{
		if( super.isSameAs( other ) )
		{
			return true;
		}
		else
		{
			if( isEnabled() )
			{
				if( other instanceof IPart )
				{
					return AppEngME.INSTANCE.getRegistryName( ( (IPart<?>) other ).getClass() ).equals( identifier() );
				}
			}
			return false;
		}
	}

}
