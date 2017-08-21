package appeng.core.skyfall.command;

import appeng.core.lib.command.CommandTreeBaseNamed;
import appeng.core.skyfall.AppEngSkyfall;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class SkyobjectsCommand extends CommandTreeBaseNamed {

	public SkyobjectsCommand(){
		super("skyobjects", "command.ae3.skyobjects.name");
		addSubcommand(new KillAll());
	}

	class KillAll extends CommandBase {

		@Override
		public String getName(){
			return "command.ae3.skyobjects.killall.name";
		}

		@Override
		public String getUsage(ICommandSender sender){
			return null;
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException{
			sender.getEntityWorld().getCapability(AppEngSkyfall.skyobjectsManagerCapability, null).killall();
		}

	}

	class Spawn extends CommandBase {

		@Override
		public String getName(){
			return "spawn";
		}

		@Override
		public String getUsage(ICommandSender sender){
			return "command.ae3.skyobjects.spawn.name";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException{
			sender.getEntityWorld().getCapability(AppEngSkyfall.skyobjectsManagerCapability, null).spawn();
		}
	}

}
