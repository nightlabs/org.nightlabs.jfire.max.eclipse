package org.nightlabs.jfire.trade.admin.ui.gridpriceconfig;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.annotation.Implement;
import org.nightlabs.base.ui.composite.ListComposite;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.i18n.I18nText;
import org.nightlabs.i18n.I18nTextBuffer;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.AccountingManager;
import org.nightlabs.jfire.accounting.AccountingManagerUtil;
import org.nightlabs.jfire.accounting.priceconfig.AffectedProductType;
import org.nightlabs.jfire.accounting.priceconfig.id.PriceConfigID;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;
import org.nightlabs.jfire.trade.ui.store.ProductTypeDAO;
import org.nightlabs.progress.ProgressMonitor;

public class StorePriceConfigsConfirmationDialog
extends Dialog
{
	private ListComposite<ProductType> productTypeList;
	private Set<PriceConfigID> priceConfigIDs;
	private ProductTypeID productTypeID;
	private PriceConfigID innerPriceConfigID;

	public StorePriceConfigsConfirmationDialog(Shell parentShell, Set<PriceConfigID> priceConfigIDs, ProductTypeID productTypeID, PriceConfigID innerPriceConfigID)
	{
		super(parentShell);
		this.priceConfigIDs = priceConfigIDs;
		this.productTypeID = productTypeID;
		this.innerPriceConfigID = innerPriceConfigID;
	}

	private static class ProductTypeListLabelProvider extends LabelProvider
	{
		@Override
		public String getText(Object element)
		{
			ProductType pt = (ProductType) element;
			return pt.getName().getText();
		}
	}

	private static final String[] FETCH_GROUPS_PRODUCT_TYPE = { FetchPlan.DEFAULT, ProductType.FETCH_GROUP_NAME };

	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite area = (Composite) super.createDialogArea(parent);
		new Label(area, SWT.WRAP).setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.StorePriceConfigsConfirmationDialog.description")); //$NON-NLS-1$
		productTypeList = new ListComposite<ProductType>(area, 
				SWT.MULTI | ListComposite.getDefaultWidgetStyle(parent), 
				(String) null, new ProductTypeListLabelProvider());
		ProductType dummy = new ProductType("dummy", "dummy", null, new LegalEntity("", "", ""), ProductType.INHERITANCE_NATURE_LEAF, ProductType.PACKAGE_NATURE_OUTER) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			private I18nTextBuffer name;
			@Implement
			public I18nText getName()
			{
				if (name == null) {
					name = new I18nTextBuffer();
					name.setText(Locale.getDefault().getLanguage(), Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.StorePriceConfigsConfirmationDialog.pseudoEntry_loading")); //$NON-NLS-1$
				}
				return name;
			}
			@Implement
			protected boolean _checkProductAvailability()
			{
				return false;
			}
			@Implement
			protected void calculatePrices()
			{
				throw new UnsupportedOperationException("Not implemented!"); //$NON-NLS-1$
			}
		};
		productTypeList.addElement(dummy);

		Job job = new Job(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.StorePriceConfigsConfirmationDialog.loadAffectedProductTypesJob.name")) { //$NON-NLS-1$
			protected IStatus run(ProgressMonitor monitor) throws Exception
			{
				try {
					AccountingManager m = AccountingManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
					Map<PriceConfigID, List<AffectedProductType>> affectedProductTypes = m.getAffectedProductTypes(priceConfigIDs, productTypeID, innerPriceConfigID);
					Set<ProductTypeID> productTypeIDs = new HashSet<ProductTypeID>();
					for (List<AffectedProductType> aptList : affectedProductTypes.values()) {
						for (AffectedProductType apt : aptList)
							productTypeIDs.add(apt.getProductTypeID());
					}

					final List<ProductType> productTypes = ProductTypeDAO.sharedInstance().getProductTypes(
							productTypeIDs, FETCH_GROUPS_PRODUCT_TYPE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
							monitor);

					Display.getDefault().asyncExec(new Runnable()
					{
						public void run()
						{
							productTypeList.removeAll();
							productTypeList.addElements(productTypes);
							getShell().pack(true);
						}
					});

				} catch (Exception e) {
					throw new RuntimeException(e);
				}

				return Status.OK_STATUS;
			}
		};
		job.setPriority(Job.SHORT);
		job.schedule();

		return area;
	}
}
