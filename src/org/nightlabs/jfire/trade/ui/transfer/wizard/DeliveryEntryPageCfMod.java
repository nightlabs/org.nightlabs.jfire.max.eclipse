package org.nightlabs.jfire.trade.ui.transfer.wizard;

import org.nightlabs.config.ConfigModule;

public class DeliveryEntryPageCfMod
extends ConfigModule
{
	private static final long serialVersionUID = 1L;

	private String modeOfDeliveryFlavourPK = null;

	public String getModeOfDeliveryFlavourPK()
	{
		return modeOfDeliveryFlavourPK;
	}
	public void setModeOfDeliveryFlavourPK(String modeOfDeliveryFlavourPK)
	{
		this.modeOfDeliveryFlavourPK = modeOfDeliveryFlavourPK;
		setChanged();
	}
}
