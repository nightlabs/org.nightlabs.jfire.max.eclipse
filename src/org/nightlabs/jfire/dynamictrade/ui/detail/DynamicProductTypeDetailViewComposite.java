package org.nightlabs.jfire.dynamictrade.ui.detail;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.ui.composite.ReadOnlyLabeledText;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.dynamictrade.store.DynamicProductType;
import org.nightlabs.jfire.dynamictrade.ui.resource.Messages;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.dao.ProductTypeDAO;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/**
 * This is the Composite which is used by the {@link DynamicProductTypeDetailView}
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 * @author marco schulze - marco at nightlabs dot de
 */
public class DynamicProductTypeDetailViewComposite
extends XComposite
{
	private XComposite textWrapper;
	private ReadOnlyLabeledText productTypeCategory;
	private ReadOnlyLabeledText productTypeName;

	public DynamicProductTypeDetailViewComposite(Composite parent, int style, LayoutMode layoutMode, LayoutDataMode layoutDataMode) {
		super(parent, style, layoutMode, layoutDataMode);
		createComposite(this);
	}

	public DynamicProductTypeDetailViewComposite(Composite parent, int style) {
		super(parent, style);
		createComposite(this);
	}
	
	public static final String[] FETCH_GROUP_PRODUCT_TYPE_DETAIL = new String[] {
		ProductType.FETCH_GROUP_NAME, ProductType.FETCH_GROUP_OWNER, ProductType.FETCH_GROUP_VENDOR,
		ProductType.FETCH_GROUP_PRODUCT_TYPE_GROUPS, ProductType.FETCH_GROUP_EXTENDED_PRODUCT_TYPE_ID
	};

	public static final String[] FETCH_GROUP_PRODUCT_TYPE_CATEGORY = new String[] {
		ProductType.FETCH_GROUP_NAME, 
		ProductType.FETCH_GROUP_EXTENDED_PRODUCT_TYPE_NO_LIMIT
	};

	private volatile Job setProductTypeIDJob = null;
	public void setProductTypeID(final ProductTypeID productTypeID) {
		Job loadJob = new Job(Messages.getString("org.nightlabs.jfire.dynamictrade.ui.detail.DynamicProductTypeDetailViewComposite.loadDynamicProductTypeJob.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				monitor.beginTask(Messages.getString("org.nightlabs.jfire.dynamictrade.ui.detail.DynamicProductTypeDetailViewComposite.loadDynamicProductTypeJob.name"), 100); //$NON-NLS-1$
				try {
					final DynamicProductType productType = (DynamicProductType) ProductTypeDAO.sharedInstance().getProductType(
							productTypeID,
							FETCH_GROUP_PRODUCT_TYPE_DETAIL,
							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
							new SubProgressMonitor(monitor, 50)
					);

					final DynamicProductType category = (DynamicProductType) ProductTypeDAO.sharedInstance().getProductType(
							productType.getExtendedProductTypeID(),
							FETCH_GROUP_PRODUCT_TYPE_CATEGORY,
							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
							new SubProgressMonitor(monitor, 50)
					);

					final Job thisJob = this;
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							if (thisJob != setProductTypeIDJob)
								return;

							productTypeName.setText(productType.getName().getText());

							StringBuilder categoryText = new StringBuilder();
							ProductType cat = category;
							while (cat != null) {
								if (categoryText.length() != 0)
									categoryText.insert(0, Messages.getString("org.nightlabs.jfire.dynamictrade.ui.detail.DynamicProductTypeDetailViewComposite.categoryText.separator")); //$NON-NLS-1$

								categoryText.insert(0, cat.getName().getText());
								cat = cat.getExtendedProductType();
							}

							productTypeCategory.setText(categoryText.toString());
						}
					});
					return Status.OK_STATUS;
				} finally {
					monitor.done();
				}
			}
		};
		setProductTypeIDJob = loadJob;
		loadJob.schedule();
	}

	protected void createComposite(XComposite parent)
	{
		textWrapper = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		productTypeCategory = new ReadOnlyLabeledText(textWrapper, Messages.getString("org.nightlabs.jfire.dynamictrade.ui.detail.DynamicProductTypeDetailViewComposite.categoryLabel.text"), SWT.BORDER); //$NON-NLS-1$
		productTypeCategory.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		productTypeCategory.getTextControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		productTypeName = new ReadOnlyLabeledText(textWrapper, Messages.getString("org.nightlabs.jfire.dynamictrade.ui.detail.DynamicProductTypeDetailViewComposite.productTypeLabel.text"), SWT.BORDER); //$NON-NLS-1$

		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.VERTICAL_ALIGN_CENTER);
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
	}

}
