package org.nightlabs.jfire.trade.ui.detail;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.nightlabs.base.ui.composite.ReadOnlyLabeledText;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.progress.ProgressMonitorWrapper;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.dao.ProductTypeDAO;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.ui.resource.Messages;

/**
 * This is the Composite which is used by the {@link GenericProductTypeDetailView}
 *
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class GenericProductTypeDetailViewComposite
extends XComposite
{
	private ReadOnlyLabeledText productTypeName;

	public GenericProductTypeDetailViewComposite(Composite parent, int style, LayoutMode layoutMode, LayoutDataMode layoutDataMode) {
		super(parent, style, layoutMode, layoutDataMode);
		createComposite(this);
	}

	public GenericProductTypeDetailViewComposite(Composite parent, int style) {
		super(parent, style);
		createComposite(this);
	}

	public static final String[] FETCH_GROUP_PRODUCT_TYPE_DETAIL = new String[] {
		ProductType.FETCH_GROUP_NAME, ProductType.FETCH_GROUP_OWNER, ProductType.FETCH_GROUP_VENDOR,
		ProductType.FETCH_GROUP_PRODUCT_TYPE_GROUPS};

	public void setProductTypeID(final ProductTypeID productTypeID) {
		Job loadJob = new Job(Messages.getString("org.nightlabs.jfire.trade.ui.detail.GenericProductTypeDetailViewComposite.loadJob.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				final ProductType productType = ProductTypeDAO.sharedInstance().getProductType(productTypeID,
						FETCH_GROUP_PRODUCT_TYPE_DETAIL, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new ProgressMonitorWrapper(monitor));
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						productTypeName.setText(productType.getName().getText());
					}
				});
				return Status.OK_STATUS;
			}
		};
		loadJob.schedule();
	}

	protected void createComposite(Composite parent)
	{
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		parent.setBackground(toolkit.getColors().getBackground());
		Form form = toolkit.createForm(parent);
		form.setLayoutData(new GridData(GridData.FILL_BOTH));
		form.setLayout(new GridLayout());
		Composite comp = form.getBody();
		comp.setLayout(new GridLayout());
		productTypeName = new ReadOnlyLabeledText(comp, Messages.getString("org.nightlabs.jfire.trade.ui.detail.GenericProductTypeDetailViewComposite.productTypeName.caption")); //$NON-NLS-1$
		productTypeName.setLayoutData(new GridData(GridData.FILL_BOTH));
	}
}
