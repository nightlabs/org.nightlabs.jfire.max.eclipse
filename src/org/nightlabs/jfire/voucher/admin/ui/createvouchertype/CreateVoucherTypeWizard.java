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
import org.nightlabs.jdo.ObjectIDUtil;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.voucher.VoucherManager;
import org.nightlabs.jfire.voucher.VoucherManagerUtil;
import org.nightlabs.jfire.voucher.admin.ui.editor.VoucherTypeEditor;
import org.nightlabs.jfire.voucher.admin.ui.editor.VoucherTypeEditorInput;
import org.nightlabs.jfire.voucher.admin.ui.resource.Messages;
import org.nightlabs.jfire.voucher.admin.ui.tree.VoucherTypeTreeNode;
import org.nightlabs.jfire.voucher.store.VoucherType;

public class CreateVoucherTypeWizard
extends DynamicPathWizard
{
	private VoucherTypeTreeNode parentNode;

	public CreateVoucherTypeWizard(VoucherTypeTreeNode parentNode)
	{
		this.parentNode = parentNode;
		if (parentNode == null)
			throw new IllegalArgumentException("parentNode must not be null!"); //$NON-NLS-1$
	}

	private VoucherTypeNamePage voucherTypeNamePage;
	private SelectVoucherPriceConfigPage selectVoucherPriceConfigPage;
	private SelectLocalAccountantDelegatePage selectLocalAccountantDelegatePage;

	@Override
	public void addPages()
	{
		ProductTypeID parentVoucherTypeID = (ProductTypeID) JDOHelper.getObjectId(parentNode.getJdoObject());

		voucherTypeNamePage = new VoucherTypeNamePage(parentVoucherTypeID);
		addPage(voucherTypeNamePage);

		selectVoucherPriceConfigPage = new SelectVoucherPriceConfigPage(parentVoucherTypeID);
		addPage(selectVoucherPriceConfigPage);

		selectLocalAccountantDelegatePage = new SelectLocalAccountantDelegatePage(parentVoucherTypeID);
		addPage(selectLocalAccountantDelegatePage);
	}

	@Implement
	public boolean performFinish()
	{
		final VoucherType voucherType = new VoucherType(
				IDGenerator.getOrganisationID(),
				ObjectIDUtil.makeValidIDString(voucherTypeNamePage.getVoucherTypeNameBuffer().getText()) + '_' + ProductType.createProductTypeID(),
				parentNode.getJdoObject(),
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

		switch (selectLocalAccountantDelegatePage.getMode()) {
			case INHERIT:
				voucherType.getProductTypeLocal().setLocalAccountantDelegate(selectLocalAccountantDelegatePage.getInheritedLocalAccountantDelegate());
				break;
			case CREATE:
				voucherType.getProductTypeLocal().getFieldMetaData("localAccountantDelegate").setValueInherited(false); //$NON-NLS-1$
				voucherType.getProductTypeLocal().setLocalAccountantDelegate(selectLocalAccountantDelegatePage.createVoucherLocalAccountantDelegate());
				break;
			case SELECT:
				voucherType.getProductTypeLocal().getFieldMetaData("localAccountantDelegate").setValueInherited(false); //$NON-NLS-1$
				voucherType.getProductTypeLocal().setLocalAccountantDelegate(selectLocalAccountantDelegatePage.getSelectedLocalAccountantDelegate());
				break;
			default:
				throw new IllegalStateException("What's that?!"); //$NON-NLS-1$
		}

		Job job = new Job(Messages.getString("org.nightlabs.jfire.voucher.admin.ui.createvouchertype.CreateVoucherTypeWizard.createVoucherTypeJob.name")) { //$NON-NLS-1$
			@Implement
			protected IStatus run(IProgressMonitor monitor)
			{
				try {
					VoucherManager vm = VoucherManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
					VoucherType vt = vm.storeVoucherType(voucherType, true, new String[] { FetchPlan.DEFAULT }, 1);
					final ProductTypeID voucherTypeID = (ProductTypeID) JDOHelper.getObjectId(vt);

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
