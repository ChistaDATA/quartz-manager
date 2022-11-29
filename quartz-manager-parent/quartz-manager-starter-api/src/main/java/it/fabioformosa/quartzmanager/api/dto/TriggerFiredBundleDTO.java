package it.fabioformosa.quartzmanager.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TriggerFiredBundleDTO {

	private int timesTriggered;

	private int repeatCount;

	private Date finalFireTime;

	private Date nextFireTime;

	private Date previousFireTime;

	private String jobKey;

	private String jobClass;

	public int getPercentage() {
		if (this.repeatCount <= 0)
			return -1;
		return Math.round((float) timesTriggered / (float) this.repeatCount
				* 100);
	}

}
