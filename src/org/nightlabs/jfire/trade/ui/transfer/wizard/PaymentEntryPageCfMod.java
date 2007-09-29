package org.nightlabs.jfire.trade.ui.transfer.wizard;

import org.nightlabs.config.ConfigModule;

public class PaymentEntryPageCfMod
extends ConfigModule
{
	private static final long serialVersionUID = 1L;

	private String modeOfPaymentFlavourPK = null;

	public String getModeOfPaymentFlavourPK()
	{
		return modeOfPaymentFlavourPK;
	}
	public void setModeOfPaymentFlavourPK(String modeOfPaymentFlavourPK)
	{
		this.modeOfPaymentFlavourPK = modeOfPaymentFlavourPK;
		setChanged();
	}
}
