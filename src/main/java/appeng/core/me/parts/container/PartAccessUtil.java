package appeng.core.me.parts.container;

import appeng.core.me.api.parts.PartPositionRotation;
import appeng.core.me.api.parts.VoxelPosition;
import appeng.core.me.api.parts.container.PartsAccess;
import appeng.core.me.api.parts.part.Part;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PartAccessUtil {

	public static boolean regionsMatchOccupiedVoxels(PartsAccess firstAccess, PartsAccess secondAccess, VoxelPosition firstPos, VoxelPosition secondPos, Stream<VoxelPosition> voxels){
		List<VoxelPosition> voxelsList = voxels.collect(Collectors.toList());

		BiFunction<PartsAccess, Stream<VoxelPosition>, List<Part>> voxels2parts = (access, stream) -> stream.map(access::getPart).filter(Optional::isPresent).map(info -> info.get().getPart()).collect(Collectors.toList());
		List<Part> firstAllParts = voxels2parts.apply(firstAccess, voxelsList.stream().map(firstPos::add));
		List<Part> secondAllParts = voxels2parts.apply(secondAccess, voxelsList.stream().map(secondPos::add));
		if(firstAllParts.size() != secondAllParts.size()) return false;

		Comparator<Part> partComparator = Comparator.comparing(Part::getRegistryName);
		Collections.sort(firstAllParts, partComparator);
		Collections.sort(secondAllParts, partComparator);
		if(!(firstAllParts.equals(secondAllParts))) return false;

		Multimap<Part, PartPositionRotation> firstPart2PosRotMap = HashMultimap.create();
		Multimap<Part, PartPositionRotation> secondPart2PosRotMap = HashMultimap.create();
		populatePart2PosRotMap(firstPart2PosRotMap, firstAccess, firstPos, voxelsList.stream());
		populatePart2PosRotMap(secondPart2PosRotMap, secondAccess, secondPos, voxelsList.stream());

		return firstPart2PosRotMap.equals(secondPart2PosRotMap);
	}

	private static void populatePart2PosRotMap(Multimap<Part, PartPositionRotation> map, PartsAccess access, VoxelPosition position, Stream<VoxelPosition> stream){
		stream.map(position::add).map(access::getPart).filter(Optional::isPresent).map(Optional::get).forEach(info -> map.put(info.getPart(), info.getPositionRotation().posAsRelativeTo(position)));
	}

}
