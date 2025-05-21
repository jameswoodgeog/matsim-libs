package org.matsim.simwrapper.dashboard;

import org.matsim.application.analysis.accessibility.AccessibilityAnalysis;
import org.matsim.application.analysis.accessibility.PreparePois;
import org.matsim.contrib.accessibility.Modes4Accessibility;
import org.matsim.simwrapper.*;
import org.matsim.simwrapper.viz.ColorScheme;
import org.matsim.simwrapper.viz.GridMap;
import org.matsim.simwrapper.viz.XYTime;

import java.util.Iterator;
import java.util.List;

/**
 * Shows emission in the scenario.
 */
public class AccessibilityDashboard implements Dashboard {


	private final List<String> pois;
	private final String coordinateSystem;
	private final List<Modes4Accessibility> modes;


	/**
	 * Best provide the crs from {@link org.matsim.core.config.groups.GlobalConfigGroup}
	 *
	 * @param coordinateSystem
	 */
	public AccessibilityDashboard(String coordinateSystem, List<String> pois, List<Modes4Accessibility> modes) {

		this.coordinateSystem = coordinateSystem;
		this.pois = pois;
		this.modes = modes;

	}

	@Override
	public void configure(Header header, Layout layout) {


		header.title = "Accessibility";
		header.description = "Shows accessibility for different modes of transport to different points of interest.";


		for (String poi : pois) {

			layout.row("pois-" + poi).el(XYTime.class, (viz, data) -> {
				viz.title = "POIs: " + poi;
				viz.description = "Shows points of interest of type " + poi;
				viz.file = data.computeWithPlaceholder(PreparePois.class, "%s/pois_simwrapper.csv", poi);
//				viz.width = 0.75;
				viz.height = 12.;
				viz.radius = 50;
				viz.setColorRamp("Jet", 0, true);
			});

			layout.tab(poi).add("pois-" + poi);


			for (Iterator<Modes4Accessibility> iterator = modes.iterator(); iterator.hasNext(); ) {
				Modes4Accessibility modeLeft = iterator.next();
				layout.row(modeLeft.name() + "-" + poi)
					.el(GridMap.class, (viz, data) -> {
						accessibilityDataGridMap(modeLeft.name(), modeLeft.name() + "_accessibility", poi, viz, data);
					});

				if (iterator.hasNext()) {
					Modes4Accessibility modeRight = iterator.next();
					layout.row(modeLeft.name() + "-" + poi)
						.el(GridMap.class, (viz, data) -> {
							accessibilityDataGridMap(modeRight.name(), modeRight.name() + "_accessibility", poi, viz, data);
						});
				}


				layout.tab(poi).add(modeLeft.name() + "-" + poi);
			}
		}

	}

	private void accessibilityDataGridMap(String modeName, String columnName, String poi, GridMap viz, Data data) {
		viz.title = modeName + " accessibility to " + poi;
		viz.unit = "Utils";
		viz.description = "yellow: high accessibility; purple: low accessibility";
		viz.setColorRamp(ColorScheme.Viridis);
		viz.cellSize = 500;
		viz.opacity = 0.75;
		viz.maxHeight = 0;

		viz.projection = this.coordinateSystem;
		viz.center = data.context().getCenter();
		viz.zoom = data.context().mapZoomLevel;
		viz.file = data.computeWithPlaceholder(AccessibilityAnalysis.class, "%s/accessibilities_simwrapper.csv", poi);
		viz.valueColumn = columnName;
		viz.height = 12.;
//		viz.width = 0.5;
	}
}
