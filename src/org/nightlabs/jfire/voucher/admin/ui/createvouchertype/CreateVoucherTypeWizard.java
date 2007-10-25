package org.nightlabs.jfire.voucher.admin.ui.createvouchertype;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.annotation.Implement;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.ObjectIDUtil;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.ProductTypeLocal;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.voucher.VoucherManager;
import org.nightlabs.jfire.voucher.VoucherManagerUtil;
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

	public CreateVoucherTypeWizard(ProductTypeID parentVoucherTypeID)
	{
		this.parentVoucherTypeID = parentVoucherTypeID;
		if (parentVoucherTypeID == null)
			throw new IllegalArgumentException("parentVoucherTypeID must not be null!"); //$NON-NLS-1$
	}

	private VoucherTypeNamePage voucherTypeNamePage;
	private SelectVoucherPriceConfigPage selectVoucherPriceConfigPage;
	private SelectLocalAccountantDelegatePage selectLocalAccountantDelegatePage;

	@Override
	public void addPages()
	{
		voucherTypeNamePage = new VoucherTypeNamePage(parentVoucherTypeID);
		addPage(voucherTypeNamePage);

		selectVoucherPriceConfigPage = new SelectVoucherPriceConfigPage(parentVoucherTypeID);
		addPage(selectVoucherPriceConfigPage);

		selectLocalAccountantDelegatePage = new SelectLocalAccountantDelegatePage(parentVoucherTypeID);
		addPage(selectLocalAccountantDelegatePage);
	}

	private static String[] FETCH_GROUPS_PARENT_VOUCHER_TYPE = {
		FetchPlan.DEFAULT,
		ProductType.FETCH_GROUP_NAME,
		ProductType.FETCH_GROUP_OWNER,
		ProductType.FETCH_GROUP_VENDOR,
		ProductType.FETCH_GROUP_DELIVERY_CONFIGURATION
	};

	@Override
	@Implement
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
		voucherType.getFieldMetaData("name").setValueInherited(false); //$NON-NLS-1$

		switch (selectVoucherPriceConfigPage.getMode()) {
			case INHERIT:
				voucherType.setPackagePriceConfig(selectVoucherPriceConfigPage.getInheritedPriceConfig());
				break;
			case CREATE:
				voucherType.setPackagePriceConfig(selectVoucherPriceConfigPage.createPriceConfig());
				voucherType.getFieldMetaData("packagePriceConfig").setValueInherited(false); //$NON-NLS-1$
				break;
			case SELECT:
				voucherType.setPackagePriceConfig(selectVoucherPriceConfigPage.getSelectedPriceConfig());
				voucherType.getFieldMetaData("packagePriceConfig").setValueInherited(false); //$NON-NLS-1$
				break;
			default:
				throw new IllegalStateException("What's that?!"); //$NON-NLS-1$
		}

//		switch (selectLocalAccountantDelegatePage.getMode()) {
//			case INHERIT:
//				voucherType.getProductTypeLocal().setLocalAccountantDelegate(selectLocalAccountantDelegatePage.getInheritedLocalAccountantDelegate());
//				break;
//			case CREATE:
//				voucherType.getProductTypeLocal().getFieldMetaData("localAccountantDelegate").setValueInherited(false); //$NON-NLS-1$
//				voucherType.getProductTypeLocal().setLocalAccountantDelegate(selectLocalAccountantDelegatePage.createVoucherLocalAccountantDelegate());
//				break;
//			case SELECT:
//				voucherType.getProductTypeLocal().getFieldMetaData("localAccountantDelegate").setValueInherited(false); //$NON-NLS-1$
//				voucherType.getProductTypeLocal().setLocalAccountantDelegate(selectLocalAccountantDelegatePage.getSelectedLocalAccountantDelegate());
//				break;
//			default:
//				throw new IllegalStateException("What's that?!"); //$NON-NLS-1$
//		}

		Job job = new Job(Messages.getString("org.nightlabs.jfire.voucher.admin.ui.createvouchertype.CreateVoucherTypeWizard.createVoucherTypeJob.name")) { //$NON-NLS-1$
			@Override
			@Implement
			protected IStatus run(IProgressMonitor monitor)
			{
				try {
					VoucherManager vm = VoucherManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
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
							vt.getProductTypeLocal().getFieldMetaData("localAccountantDelegate").setValueInherited(false); //$NON-NLS-1$
							vt.getProductTypeLocal().setLocalAccountantDelegate(selectLocalAccountantDelegatePage.createVoucherLocalAccountantDelegate());
							break;
						case SELECT:
							vt.getProductTypeLocal().getFieldMetaData("localAccountantDelegate").setValueInherited(false); //$NON-NLS-1$
							vt.getProductTypeLocal().setLocalAccountantDelegate(selectLocalAccountantDelegatePage.getSelectedLocalAccountantDelegate());
							break;
						default:
							throw new IllegalStateException("What's that?!"); //$NON-NLS-1$
					}

					// and store it again with the correct LocalAccountantDelegate
					vm.storeVoucherType(vt, false, null, 1);

//					// remove this DEBUG stuff - this can now be done by the editor afterwards - still I keep it commented here ;-) Marco.
//					StoreManager sm = StoreManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
//					sm.setProductTypeStatus_published(voucherTypeID, false, null, 1);
//					if (ProductType.INHERITANCE_NATURE_LEAF == voucherType.getInheritanceNature()) {
//						sm.setProductTypeStatus_confirmed(voucherTypeID, false, null, 1);
//						sm.setProductTypeStatus_saleable(voucherTypeID, true, false, null, 1);
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

}
