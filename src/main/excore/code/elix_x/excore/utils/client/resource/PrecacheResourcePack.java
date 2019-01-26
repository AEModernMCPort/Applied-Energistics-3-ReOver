package code.elix_x.excore.utils.client.resource;

import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.mutable.MutableObject;

import javax.annotation.Nullable;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

public class PrecacheResourcePack extends ExternalDefaultRP {

	protected IResourcePack delegate;

	public PrecacheResourcePack(String name, Set<String> domains, IResourcePack delegate){
		super(name, domains);
		this.delegate = delegate;
	}

	public PrecacheResourcePack(String name, String domain, IResourcePack delegate){
		super(name, domain);
		this.delegate = delegate;
	}

	public PrecacheResourcePack(ExternalDefaultRP delegate){
		this(delegate.name, delegate.domains, delegate);
	}

	protected Map<ResourceLocation, File> cached = new HashMap<>();

	public void precache(Stream<ResourceLocation> resources) throws IOException {
		MutableObject<IOException> e = new MutableObject<>();
		resources.filter(this::resourceExists).forEach(rl -> {
			if(e.getValue() != null) return;
			InputStream in = null;
			OutputStream out = null;
			try{
				File file = File.createTempFile(UUID.randomUUID().toString(), ".tmp");
				IOUtils.copy(in = getInputStream(rl), out = new BufferedOutputStream(new FileOutputStream(file)));
				file.deleteOnExit();
				cached.put(rl, file);
			} catch(IOException eio){
				e.setValue(eio);
			} finally {
				IOUtils.closeQuietly(in);
				IOUtils.closeQuietly(out);
			}
		});
		if(e.getValue() != null) throw e.getValue();
	}

	@Override
	public boolean resourceExists(ResourceLocation location){
		return delegate.resourceExists(location);
	}

	@Override
	public InputStream getInputStream(ResourceLocation location) throws IOException {
		File cached = this.cached.get(location);
		return cached != null ? new BufferedInputStream(new FileInputStream(cached)) : delegate.getInputStream(location);
	}

	@Nullable
	@Override
	public <T extends IMetadataSection> T getPackMetadata(MetadataSerializer metadataSerializer, String metadataSectionName) throws IOException{
		return delegate.getPackMetadata(metadataSerializer, metadataSectionName);
	}

}
