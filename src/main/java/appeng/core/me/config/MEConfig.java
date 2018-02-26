package appeng.core.me.config;

import appeng.core.me.api.network.block.Connection;
import appeng.core.me.api.parts.PartColor;
import appeng.core.me.network.connect.ConnectionsParams;
import code.elix_x.excomms.color.RGBA;

import java.util.HashMap;
import java.util.Map;

public class MEConfig {

	private Map<String, RGBA> partColors = new HashMap<>();

	public MEConfig(){
	}

	public void registerColors(){
		partColors.forEach((name, ref) -> PartColor.createNewColor(name, ref, PartColor.defaultCompatibility(ref)));
	}

	/*
	 *	|	||
	 *	||	|_
	 */

	public double dataMaxDistance = 150;
	public double energyMaxDistance = 420;

	private double lossExponent = 2.5;

	public double lossFactor(Connection connection, double distance){
		return Math.pow(1 - Math.pow(Math.max(distance, 0) / connection.maxDistance(), lossExponent), 1/lossExponent);
	}

	public ConnectionsParams<?> decay(ConnectionsParams<?> params, double distance){
		return params.mul(c -> lossFactor(c, distance));
	}

}
