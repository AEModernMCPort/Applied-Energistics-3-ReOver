package appeng.api.config;

/**
 * If config implements this interface, it is considered to have transient values that require compilation & decompilation at runtime
 *
 * @author Elix_x
 */
public interface ConfigCompilable {

	/**
	 * Called immediately after loading.
	 */
	void compile();

	/**
	 * Called immediately before saving.
	 */
	void decompile();

}
