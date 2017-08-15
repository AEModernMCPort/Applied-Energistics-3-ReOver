package appeng.api.recipe;

public interface IngredientGrid<T, I extends IGIngredient<T>> {

	int getWidth();

	int getHeight();

	default boolean hasSlot(int x, int y){
		return true;
	}

}
