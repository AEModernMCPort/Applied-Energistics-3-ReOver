package code.elix_x.excore.utils.client.resource;

import com.google.common.collect.Sets;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.MetadataSerializer;

import javax.annotation.Nullable;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Set;

public abstract class ExternalDefaultRP implements IResourcePack {

	protected String name;
	protected BufferedImage image;
	protected Set<String> domains;

	public ExternalDefaultRP(String name, BufferedImage image, Set<String> domains){
		this.name = name;
		this.image = image;
		this.domains = domains;
	}

	public ExternalDefaultRP(String name, BufferedImage image, String domain){
		this(name, image, Sets.newHashSet(domain));
	}

	public ExternalDefaultRP(String name, Set<String> domains){
		this(name, null, domains);
	}

	public ExternalDefaultRP(String name, String domain){
		this(name, null, domain);
	}

	public void registerAsDefault(){
		ResourcePacksHelper.addDefaultResourcePack(this);
	}

	@Override
	public String getPackName(){
		return name;
	}

	@Override
	public BufferedImage getPackImage() throws IOException{
		return image;
	}

	@Override
	public Set<String> getResourceDomains(){
		return domains;
	}

	@Nullable
	@Override
	public <T extends IMetadataSection> T getPackMetadata(MetadataSerializer metadataSerializer, String metadataSectionName) throws IOException{
		return null;
	}
}
