package org.nightlabs.jfire.voucher.admin.ui.createvouchertype;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.ObjectIDUtil;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.ProductTypeLocal;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.voucher.VoucherManagerRemote;
import org.nightlabs.jfire.voucher.admin.ui.editor.VoucherTypeEditor;
import org.nightlabs.jfire.voucher.admin.ui.editor.VoucherTypeEditorInput;
import org.nightlabs.jfire.voucher.admin.ui.resource.Messages;
import org.nightlabs.jfire.voucher.dao.VoucherTypeDAO;
import org.nightlabs.jfire.voucher.store.VoucherType;
import org.nightlabs.progress.NullProgressMonitor;

public class CreateVoucherTypeWizard
extends DynamicPathWizard
{
	private ProductTypeID parentVoucherTypeID;
//	private OwnerVendorPage ownerVendorPage;

	public CreateVoucherTypeWizard(ProductTypeID parentVoucherTypeID)
	{
		this.parentVoucherTypeID = parentVoucherTypeID;
	}

	private VoucherTypeNamePage voucherTypeNamePage;
	private SelectVoucherPriceConfigPage selectVoucherPriceConfigPage;
	private SelectLocalAccountantDelegatePage selectLocalAccountantDelegatePage;

	@Override
	public void addPages()
	{
		assert parentVoucherTypeID != null;

		voucherTypeNamePage = new VoucherTypeNamePage(parentVoucherTypeID);
		addPage(voucherTypeNamePage);

		selectVoucherPriceConfigPage = new SelectVoucherPriceConfigPage(parentVoucherTypeID);
		addPage(selectVoucherPriceConfigPage);

		selectLocalAccountantDelegatePage = new SelectLocalAccountantDelegatePage(parentVoucherTypeID);
		addPage(selectLocalAccountantDelegatePage);

//		ownerVendorPage = new OwnerVendorPage(parentVoucherTypeID);
//		addPage(ownerVendorPage);
	}

	private static String[] FETCH_GROUPS_PARENT_VOUCHER_TYPE = {
		FetchPlan.DEFAULT,
		ProductType.FETCH_GROUP_NAME,
		ProductType.FETCH_GROUP_OWNER,
		ProductType.FETCH_GROUP_VENDOR,
		ProductType.FETCH_GROUP_DELIVERY_CONFIGURATION
	};

	@Override
	public boolean performFinish()
	{
		VoucherType parentVoucherType = VoucherTypeDAO.sharedInstance().getVoucherType(
				parentVoucherTypeID, FETCH_GROUPS_PARENT_VOUCHER_TYPE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				new NullProgressMonitor()); // TODO async!

		final VoucherType voucherType = new VoucherType(
				IDGenerator.getOrganisationID(),
				ObjectIDUtil.makeValidIDString(voucherTypeNamePage.getVoucherTypeNameBuffer().getText()) + '_' + ProductType.createProductTypeID(),
				parentVoucherType,
				voucherTypeNamePage.getInheritanceNature(),
				voucherTypeNamePage.getPackageNature());
		voucherType.getName().copyFrom(voucherTypeNamePage.getVoucherTypeNameBuffer());
		voucherType.getFieldMetaData(ProductType.FieldName.name).setValueInherited(false);

		switch (selectVoucherPriceConfigPage.getMode()) {
		case INHERIT:
			voucherType.setPackagePriceConfig(selectVoucherPriceConfigPage.getInheritedPriceConfig());
			break;
		case CREATE:
			voucherType.setPackagePriceConfig(selectVoucherPriceConfigPage.createPriceConfig());
			voucherType.getFieldMetaData(ProductType.FieldName.packagePriceConfig).setValueInherited(false);
			break;
		case SELECT:
			voucherType.setPackagePriceConfig(selectVoucherPriceConfigPage.getSelectedPriceConfig());
			voucherType.getFieldMetaData(ProductType.FieldName.packagePriceConfig).setValueInherited(false);
			break;
		case NONE:
			break;		
		default:
			throw new IllegalStateException("What's that?!"); //$NON-NLS-1$
		}

//		if(ownerVendorPage.isPageComplete())
//			ownerVendorPage.configureProductType(voucherType);
		Job job = new Job(Messages.getString("org.nightlabs.jfire.voucher.admin.ui.createvouchertype.CreateVoucherTypeWizard.createVoucherTypeJob.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				try {
					VoucherManagerRemote vm = JFireEjb3Factory.getRemoteBean(VoucherManagerRemote.class, Login.getLogin().getInitialContextProperties());
					VoucherType vt = vm.storeVoucherType(
							voucherType, true,
							new String[] {
									FetchPlan.DEFAULT,
									ProductType.FETCH_GROUP_PRODUCT_TYPE_LOCAL,
									ProductTypeLocal.FETCH_GROUP_FIELD_METADATA_MAP,
									ProductTypeLocal.FETCH_GROUP_LOCAL_ACCOUNTANT_DELEGATE },
									NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
					final ProductTypeID voucherTypeID = (ProductTypeID) JDOHelper.getObjectId(vt);

					// We cannot access the ProductTypeLocal before it has been stored. Hence, we unfortunately, need to set it after we already stored it.
					// Alternatively, we could later pass the localAccountantDelegate to the store-method...
					switch (selectLocalAccountantDelegatePage.getMode()) {
					case INHERIT:
						vt.getProductTypeLocal().setLocalAccountantDelegate(selectLocalAccountantDelegatePage.getInheritedLocalAccountantDelegate());
						break;
					case CREATE:
						vt.getProductTypeLocal().getFieldMetaData(ProductTypeLocal.FieldName.localAccountantDelegate).setValueInherited(false);
						vt.getProductTypeLocal().setLocalAccountantDelegate(selectLocalAccountantDelegatePage.createVoucherLocalAccountantDelegate());
						break;
					case SELECT:
						vt.getProductTypeLocal().getFieldMetaData(ProductTypeLocal.FieldName.localAccountantDelegate).setValueInherited(false);
						vt.getProductTypeLocal().setLocalAccountantDelegate(selectLocalAccountantDelegatePage.getSelectedLocalAccountantDelegate());
						break;
					default:
						throw new IllegalStateException("What's that?!"); //$NON-NLS-1$
					}

					vm.storeVoucherType(vt, false, null, 1);

//					// remove this DEBUG stuff - this can now be done by the editor afterwards - still I keep it commented here ;-) Marco.
//					StoreManager sm = JFireEjbFactory.getBean(StoreManager.class, Login.getLogin().getInitialContextProperties());
//					sm.setProductTypeStatus_published(voucherTypeID, false, null, 1);
//					if (ProductType.INHERITANCE_NATURE_LEAF == voucherType.getInheritanceNature()) {
//					sm.setProductTypeStatus_confirmed(voucherTypeID, false, null, 1);
//					sm.setProductTypeStatus_saleable(voucherTypeID, true, false, null, 1);
//					}
//					// end DEBUG stuff

					Display.getDefault().asyncExec(new Runnable() {
						public void run()
						{
							try {
								RCPUtil.openEditor(
										new VoucherTypeEditorInput(voucherTypeID),
										VoucherTypeEditor.EDITOR_ID);
							} catch (Exception e) {
								throw new RuntimeException(e);
							}
						}
					});
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.setPriority(Job.SHORT);
		job.schedule();
		return true;
	}

	public void setParentVoucherTypeID(ProductTypeID parentVoucherTypeID) {
		this.parentVoucherTypeID = parentVoucherTypeID;
	}
}
