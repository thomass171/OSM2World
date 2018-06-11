package org.osm2world.core.world.modules;

import static java.util.Arrays.asList;
import static java.util.Collections.EMPTY_LIST;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


import org.apache.commons.configuration2.BaseConfiguration;
import org.apache.commons.configuration2.BaseHierarchicalConfiguration;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.junit.Test;
import org.osm2world.core.Config;
import org.osm2world.openstreetmap.data.MapBasedTagGroup;
import org.osm2world.openstreetmap.data.Tag;
import org.osm2world.core.ConversionFacade;
import org.osm2world.core.osm.data.OSMData;
import org.osm2world.core.osm.data.OSMNode;
import org.osm2world.core.osm.data.OSMWay;
import org.osm2world.core.target.Target;
import org.osm2world.core.target.statistics.StatisticsTarget;
import org.osm2world.core.target.statistics.StatisticsTarget.Stat;
import org.osm2world.core.world.creation.WorldModule;


public class PowerModuleTest {
	
	@Test
	public void testRepeatedRendering() throws Exception {
		Config.reset();
		/* create fake data */
		
		List<OSMNode> nodes = asList(
				new OSMNode(0, 0, new MapBasedTagGroup(new Tag("power","tower")), 101),
				new OSMNode(0, 0.001, new MapBasedTagGroup(new Tag("power","tower")), 102)
				);
		
		List<OSMWay> ways = asList(
				new OSMWay(new MapBasedTagGroup(new Tag("power","line"), new Tag("cables","4")), 201, nodes)
				);
		
		OSMData osmData = new OSMData(EMPTY_LIST, nodes, ways, EMPTY_LIST);
		
		/* render to multiple targets */
		
		ConversionFacade cf = new ConversionFacade(osmData);
		
		StatisticsTarget t1 = new StatisticsTarget();
		StatisticsTarget t2 = new StatisticsTarget();
		
		List<Target<?>> targets = Arrays.<Target<?>>asList(t1, t2);
		Config.getInstance().enableModules(new String[]{"PowerModule"});
        ConversionFacade.Results results = cf.createRepresentations();
		cf.render(results,targets);
		PowerModule powerModule = (PowerModule) cf.getModule("PowerModule");
		assertNotNull(powerModule);
		assertNull(cf.getModule("RoadModule"));
		/* check whether the results are the same each time */
		
		for (Stat stat : Stat.values()) {
			assertEquals(t1.getGlobalCount(stat), t2.getGlobalCount(stat));
		}
		
	}
	
}
