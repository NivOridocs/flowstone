package nivoridocs.flowstone;

import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Settings;

@Settings(onlyAnnotated = true)
public class FlowstoneConfig {

	@Setting
	@Setting.Constrain.Range(min = 0, max = 1, step = .001d)
	double lowest = .05d;

	@Setting
	@Setting.Constrain.Range(min = 0, max = 1, step = .001d)
	double highest = .5d;

	@Setting
	@Setting.Constrain.Range(min = 0, max = 98, step = 1)
	int limit = 45;

	public double getLowest() {
		return lowest;
	}

	public double getHighest() {
		return highest;
	}

	public int getLimit() {
		return limit;
	}

}
