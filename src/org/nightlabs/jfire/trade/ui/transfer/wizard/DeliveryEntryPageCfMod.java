package org.nightlabs.jfire.trade.ui.transfer.wizard;

import java.util.HashMap;
import java.util.Map;

import org.nightlabs.config.ConfigModule;
import org.nightlabs.config.InitException;
import org.nightlabs.jfire.store.deliver.ModeOfDeliveryFlavour;

public class DeliveryEntryPageCfMod
extends ConfigModule
{
	private static final long serialVersionUID = 1L;

	private String modeOfDeliveryFlavourPK = null;

	private Map<String, String> deliveryConfigurationPK2modeOfDeliveryFlavourPK = null;

	@Override
	public void init() throws InitException {
		super.init();
		if (deliveryConfigurationPK2modeOfDeliveryFlavourPK == null)
			deliveryConfigurationPK2modeOfDeliveryFlavourPK = new HashMap<String, String>();
	}

	/**
	 * Get the last selected {@link ModeOfDeliveryFlavour}-PK-String.
	 *
	 * @return the result of {@link ModeOfDeliveryFlavour#getPrimaryKey()} of the last selected <code>ModeOfDeliveryFlavour</code>.
	 */
	public String getModeOfDeliveryFlavourPK()
	{
		return modeOfDeliveryFlavourPK;
	}
	public void setModeOfDeliveryFlavourPK(String modeOfDeliveryFlavourPK)
	{
		this.modeOfDeliveryFlavourPK = modeOfDeliveryFlavourPK;
		setChanged();
	}

	public Map<String, String> getDeliveryConfigurationPK2modeOfDeliveryFlavourPK() {
		return deliveryConfigurationPK2modeOfDeliveryFlavourPK;
	}
	public void setDeliveryConfigurationPK2modeOfDeliveryFlavourPK(Map<String, String> deliveryConfigurationPK2modeOfDeliveryFlavourPK) {
		this.deliveryConfigurationPK2modeOfDeliveryFlavourPK = deliveryConfigurationPK2modeOfDeliveryFlavourPK;
		setChanged();
	}
	public void setModeOfDeliveryFlavourPK(String deliveryConfigurationPK, String modeOfDeliveryFlavourPK)
	{
		deliveryConfigurationPK2modeOfDeliveryFlavourPK.put(deliveryConfigurationPK, modeOfDeliveryFlavourPK);
		this.modeOfDeliveryFlavourPK = modeOfDeliveryFlavourPK;
		setChanged();
	}
}
