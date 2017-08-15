package appeng.core.core.api.definition;

import appeng.api.definition.ISubDefinition;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

/**
 * Default interface for block sub-definition. For use with type parameters. All AE block sub definition implement this.
 *
 * @param <S> IBlockState container
 * @param <B> Block
 * @author Elix_x
 */
public interface IBlockSubDefinition<S extends IBlockState, B extends Block> extends ISubDefinition<S, B, IBlockSubDefinition<S, B>> {

}
