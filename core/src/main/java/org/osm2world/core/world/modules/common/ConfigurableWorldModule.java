package org.osm2world.core.world.modules.common;

import org.apache.commons.configuration2.Configuration;
import org.osm2world.core.Config;
import org.osm2world.core.map_data.data.MapWaySegment;
import org.osm2world.core.target.TargetBounds;
import org.osm2world.core.world.creation.WorldModule;

/**
 * simple superclass for {@link WorldModule}s that stores a configuration set by
 *
 */
public abstract class ConfigurableWorldModule implements WorldModule {
    protected Configuration config;
	
	@Override
	public void setConfiguration(Configuration config) {
		this.config = config;
	}
	
	public boolean insideBounds(MapWaySegment segment){
        TargetBounds targetBounds = Config.getInstance().getTargetBounds();
        if (targetBounds != null){
	        if (!targetBounds.isInside(segment.getStartNode())){
	            return false;
            }
            if (!targetBounds.isInside(segment.getEndNode())){
                return false;
            }
        }
	    return true;
    }
}
