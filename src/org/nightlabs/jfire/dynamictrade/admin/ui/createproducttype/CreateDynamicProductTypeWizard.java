package org.nightlabs.jfire.dynamictrade.admin.ui.createproducttype;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.ObjectIDUtil;
import org.nightlabs.jfire.base.JFireEjbFactory;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.dynamictrade.DynamicTradeManager;
import org.nightlabs.jfire.dynamictrade.admin.ui.editor.DynamicProductTypeEditor;
import org.nightlabs.jfire.dynamictrade.admin.ui.editor.DynamicProductTypeEditorInput;
import org.nightlabs.jfire.dynamictrade.admin.ui.priceconfig.ChooseDynamicTradePriceConfigPage;
import org.nightlabs.jfire.dynamictrade.admin.ui.resource.Messages;
import org.nightlabs.jfire.dynamictrade.store.DynamicProductType;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.dao.ProductTypeDAO;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.admin.ui.editor.ownervendor.OwnerVendorPage;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.progress.ProgressMonitor;

public class CreateDynamicProductTypeWizard
extends DynamicPathWizard
{
	private ProductTypeID parentProductTypeID;
	private ChooseDynamicTradePriceConfigPage selectPriceConfigPage;
	private OwnerVendorPage ownerVendorPage;

	private static String[] FETCH_GROUPS_PARENT_PRODUCT_TYPE = {
		FetchPlan.DEFAULT,
		ProductType.FETCH_GROUP_NAME,
		ProductType.FETCH_GROUP_OWNER,
		ProductType.FETCH_GROUP_VENDOR,
		ProductType.FETCH_GROUP_DELIVERY_CONFIGURATION
	};

	public CreateDynamicProductTypeWizard(ProductTypeID parentProductTypeID)
	{
		if (parentProductTypeID == null)
			throw new IllegalArgumentException("parentProductTypeID must not be null.");

		this.parentProductTypeID = parentProductTypeID;
	}

	private DynamicProductTypeNamePage dynamicProductTypeNamePage;

	@Override
	public void addPages()
	{
		dynamicProductTypeNamePage = new DynamicProductTypeNamePage(parentProductTypeID);
		addPage(dynamicProductTypeNamePage);

		selectPriceConfigPage = new ChooseDynamicTradePriceConfigPage(parentProductTypeID);
		addPage(selectPriceConfigPage);

		ownerVendorPage = new OwnerVendorPage(parentProductTypeID);
		addPage(ownerVendorPage);

	}

	@Override
	public boolean performFinish()
	{
		ProductType parentProductType = ProductTypeDAO.sharedInstance().getProductType(
				parentProductTypeID, FETCH_GROUPS_PARENT_PRODUCT_TYPE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor()); // TODO async!

		final DynamicProductType dynamicProductType = new DynamicProductType(
				IDGenerator.getOrganisationID(),
				ObjectIDUtil.makeValidIDString(dynamicProductTypeNamePage.getDynamicProductTypeNameBuffer().getText()) + '_' + ProductType.createProductTypeID(),
				parentProductType,
				dynamicProductTypeNamePage.getInheritanceNature(),
				dynamicProductTypeNamePage.getPackageNature());
		dynamicProductType.getName().copyFrom(dynamicProductTypeNamePage.getDynamicProductTypeNameBuffer());
		dynamicProductType.getFieldMetaData(ProductType.FieldName.name).setValueInherited(false);

		selectPriceConfigPage.configureProductType(dynamicProductType);

		if(ownerVendorPage.isPageComplete())
			ownerVendorPage.configureProductType(dynamicProductType);


		Job job = new Job(Messages.getString("org.nightlabs.jfire.dynamictrade.admin.ui.createproducttype.CreateDynamicProductTypeWizard.createDynamicProductTypeJob.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception
			{
				DynamicTradeManager vm = JFireEjbFactory.getBean(DynamicTradeManager.class, Login.getLogin().getInitialContextProperties());
				DynamicProductType vt = vm.storeDynamicProductType(dynamicProductType, true, new String[] { FetchPlan.DEFAULT }, 1);
				final ProductTypeID dynamicProductTypeID = (ProductTypeID) JDOHelper.getObjectId(vt);

				// TODO remove this DEBUG stuff
//				StoreManager sm = JFireEjbFactory.getBean(StoreManager.class, Login.getLogin().getInitialContextProperties());
//				sm.setProductTypeStatus_published(dynamicProductTypeID, false, null, 1);
//				if (ProductType.INHERITANCE_NATURE_LEAF == dynamicProductType.getInheritanceNature()) {
//				sm.setProductTypeStatus_confirmed(dynamicProductTypeID, false, null, 1);
//				sm.setProductTypeStatus_saleable(dynamicProductTypeID, true, false, null, 1);
//				}
				// end DEBUG stuff

				Display.getDefault().asyncExec(new Runnable() {
					public void run()
					{
						try {
							RCPUtil.openEditor(
									new DynamicProductTypeEditorInput(dynamicProductTypeID),
									DynamicProductTypeEditor.EDITOR_ID);
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					}
				});
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.setPriority(org.eclipse.core.runtime.jobs.Job.SHORT);
		job.schedule();
		return true;
	}

}
