package org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard;

import org.nightlabs.annotation.Implement;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * @author Marco Schulze - Marco at NightLabs dot de
 */
public abstract class AbstractChooseGridPriceConfigWizard 
extends DynamicPathWizard 
{
	public AbstractChooseGridPriceConfigWizard(ProductTypeID parentProductTypeID) {
		super();
		this.parentProductTypeID = parentProductTypeID;
		setWindowTitle(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.AbstractChooseGridPriceConfigWizard.windowTitle")); //$NON-NLS-1$
	}

	private ProductTypeID parentProductTypeID = null;
	private AbstractChooseGridPriceConfigPage abstractChooseGridPriceConfigPage = null;
	
	public void addPages()
	{
		abstractChooseGridPriceConfigPage = createChooseGridPriceConfigPage(parentProductTypeID);
//		addDynamicWizardPage(abstractChooseGridPriceConfigPage);
		addPage(abstractChooseGridPriceConfigPage);
	}

	@Implement
	public boolean performFinish() {
		return true;
	}

	protected abstract AbstractChooseGridPriceConfigPage createChooseGridPriceConfigPage(ProductTypeID parentProductTypeID);

	public AbstractChooseGridPriceConfigPage getAbstractChooseGridPriceConfigPage()
	{
		return abstractChooseGridPriceConfigPage;
	}
}
