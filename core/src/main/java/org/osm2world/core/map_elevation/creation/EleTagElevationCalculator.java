package org.osm2world.core.map_elevation.creation;


import org.osm2world.openstreetmap.data.TagGroup;

import static org.osm2world.openstreetmap.util.ValueStringParser.parseOsmDecimal;

/**
 * sets elevations based on ele tags
 */
public class EleTagElevationCalculator extends TagElevationCalculator {
	
	@Override
	protected Double getEleForTags(TagGroup tags) {
		
		Float value = null;
		
		if (tags.containsKey("ele")) {
			value = parseOsmDecimal(tags.getValue("ele"), true);
		}
		
		if (value == null) {
			return null;
		} else {
			return (double) value;
		}
		
	}
	
}
