
package appeng.api.definitions.sub;


import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;


/**
 * Default interface for block sub-definitions. For use with type parameters. All AE block sub definitions implement this.
 * 
 * @author Elix_x
 *
 * @param <S> IBlockState container
 * @param <B> Block
 */
public interface IBlockSubDefinition<S extends IBlockState, B extends Block> extends ISubDefinition<S, B, IBlockSubDefinition<S, B>>
{

}
