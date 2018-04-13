package appeng.core.core.know;

import appeng.core.core.api.know.EternalWiki;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EternalWikiImpl implements EternalWiki {

	public static final EternalWiki ETERNALWIKI = new EternalWikiImpl();

	public static void freeze(){
		((EternalWikiImpl) ETERNALWIKI).freezeI();
	}

	private EternalWikiImpl(){}

	protected Multimap<String, String> knowParents = HashMultimap.create();
	protected Multimap<String, String> knowComplexity = HashMultimap.create();

	protected void freezeI(){
		knowParents = ImmutableMultimap.copyOf(knowParents);
		knowComplexity = ImmutableMultimap.copyOf(knowComplexity);
	}

	@Override
	public void registerKnow(String k, Stream<String> ps){
		String know = k.replaceAll("[/]+", "/");
		if(know.isEmpty()) throw new IllegalArgumentException("Are you that lazy to not name a knowledge, weirdo?");
		Set<String> parents = ps.collect(Collectors.toSet());
		Optional.of(know.split("/")).map(ka -> ka.length > 1 ? Arrays.stream(ka).limit(ka.length - 1).collect(Collectors.joining("/")) : null).ifPresent(parents::add);
		if(parents.stream().anyMatch(p -> knowParents.containsEntry(p, know))) throw new IllegalArgumentException("Know-loops aren't implemented yet!");
		knowParents.putAll(know, parents);
		knowComplexity.put("", know);
		parents.forEach(p -> knowComplexity.put(p, know));
	}

	@Override
	public boolean isCompound(String know){
		return !knowComplexity.containsKey(know);
	}

	@Override
	public Stream<String> getParents(String know){
		return knowParents.get(know).stream();
	}

	@Override
	public Stream<String> getPieces(String know){
		return knowComplexity.get(know).stream();
	}

	@Override
	public boolean isAware(String know){
		return doesKnow(know);
	}

	@Override
	public boolean doesKnow(String know){
		return knowComplexity.containsEntry("", know);
	}

}
