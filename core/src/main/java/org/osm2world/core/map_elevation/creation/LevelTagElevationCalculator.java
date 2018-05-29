package org.osm2world.core.map_elevation.creation;


import org.osm2world.openstreetmap.data.TagGroup;

import static org.osm2world.openstreetmap.util.ValueStringParser.parseOsmDecimal;

/**
 * sets elevations based on level tags
 */
public class LevelTagElevationCalculator extends TagElevationCalculator {
	
	final double elePerLevel;
	
	public LevelTagElevationCalculator(double elePerLevel) {
		super(0.0, true);
		this.elePerLevel = elePerLevel;
	}
	
	public LevelTagElevationCalculator() {
		this(3.0);
	}
	
	@Override
	protected Double getEleForTags(TagGroup tags) {
		
		Float value = null;
		
		if (tags.containsKey("level")) {
			value = parseOsmDecimal(tags.getValue("level"), true);
		}
		
		if (value == null) {
			return null;
		} else {
			return elePerLevel * value;
		}
		
	}
	
}
