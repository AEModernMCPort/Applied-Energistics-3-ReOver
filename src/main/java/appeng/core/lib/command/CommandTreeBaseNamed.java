package appeng.core.lib.command;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.server.command.CommandTreeBase;

public class CommandTreeBaseNamed extends CommandTreeBase {

	protected final String name;
	protected String usage;

	public CommandTreeBaseNamed(String name){
		this.name = name;
	}

	public CommandTreeBaseNamed(String name, String usage){
		this(name);
		this.usage = usage;
	}

	@Override
	public String getName(){
		return name;
	}

	@Override
	public String getUsage(ICommandSender sender){
		return usage;
	}

}
