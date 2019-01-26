package code.elix_x.excore.utils.client.resource;

import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class WebResourcePack extends ExternalDefaultRP {

	public static final char DEFCOLONCHAR = '\uE01A';

	public static final Function<String, Optional<URL>> DEFAULTLOC2URL = loc -> {
		try{
			return Optional.of(new URL(loc.replace(DEFCOLONCHAR, ':')));
		} catch(IOException e){}
		return Optional.empty();
	};

	public static final Predicate<URL> DEFAULTURLEXISTS = url -> {
		try{
			return ((HttpURLConnection) url.openConnection()).getResponseCode() == HttpURLConnection.HTTP_OK;
		} catch(IOException e){}
		return false;
	};

	protected Function<String, Optional<URL>> getURL;
	protected Predicate<URL> urlExists;

	public WebResourcePack(String rpdomain, Function<String, Optional<URL>> getURL, Predicate<URL> urlExists){
		super("Web:" + rpdomain, rpdomain);
		this.getURL = getURL;
		this.urlExists = urlExists;
	}

	public WebResourcePack(String rpdomain){
		this(rpdomain, DEFAULTLOC2URL, DEFAULTURLEXISTS);
	}

	@Override
	public boolean resourceExists(ResourceLocation location){
		return domains.contains(location.getNamespace()) && getURL.apply(location.getPath()).map(urlExists::test).orElse(false);
	}

	@Override
	public InputStream getInputStream(ResourceLocation location) throws IOException{
		return getURL.apply(location.getPath()).orElseThrow(() -> new IOException(String.format("Could not load %s from web RP %s", location, getPackName()))).openStream();
	}

}
