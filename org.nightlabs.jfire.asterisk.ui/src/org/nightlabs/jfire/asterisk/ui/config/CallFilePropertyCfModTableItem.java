package org.nightlabs.jfire.asterisk.ui.config;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 * @deprecated I think this class is never used. Please delete it. Marco.
 */
@Deprecated
public class CallFilePropertyCfModTableItem
{
	private String propertyKey;
	private String defaultPropertyValue;
	private boolean overridden;
	private String propertyValue;

	public CallFilePropertyCfModTableItem(String propertyKey, String defaultPropertyValue, boolean overriden, String propertyValue) {
		this.propertyKey = propertyKey;
		this.defaultPropertyValue = defaultPropertyValue;
		this.overridden = overriden;
		this.propertyValue = propertyValue;
	}

	public String getPropertyKey() {
		return propertyKey;
	}

	public void setPropertyKey(String propertyKey) {
		this.propertyKey = propertyKey;
	}

	public String getDefaultPropertyValue() {
		return defaultPropertyValue;
	}

	public void setDefaultPropertyValue(String defaultPropertyValue) {
		this.defaultPropertyValue = defaultPropertyValue;
	}

	public boolean isOverridden() {
		return overridden;
	}

	public void setOverridden(boolean overridden) {
		this.overridden = overridden;
	}

	public String getPropertyValue() {
		return propertyValue;
	}

	public void setPropertyValue(String propertyValue) {
		this.propertyValue = propertyValue;
	}
}
