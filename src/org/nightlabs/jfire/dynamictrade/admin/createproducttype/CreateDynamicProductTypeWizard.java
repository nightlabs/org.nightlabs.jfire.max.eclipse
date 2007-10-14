package org.nightlabs.jfire.dynamictrade.admin.createproducttype;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.annotation.Implement;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jdo.ObjectIDUtil;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.dynamictrade.DynamicTradeManager;
import org.nightlabs.jfire.dynamictrade.DynamicTradeManagerUtil;
import org.nightlabs.jfire.dynamictrade.admin.editor.DynamicProductTypeEditor;
import org.nightlabs.jfire.dynamictrade.admin.editor.DynamicProductTypeEditorInput;
import org.nightlabs.jfire.dynamictrade.admin.resource.Messages;
import org.nightlabs.jfire.dynamictrade.admin.tree.DynamicProductTypeTreeNode;
import org.nightlabs.jfire.dynamictrade.store.DynamicProductType;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.progress.ProgressMonitor;

public class CreateDynamicProductTypeWizard
extends DynamicPathWizard
{
	private DynamicProductTypeTreeNode parentNode;

	public CreateDynamicProductTypeWizard(DynamicProductTypeTreeNode parentNode)
	{
		this.parentNode = parentNode;
		if (parentNode == null)
			throw new IllegalArgumentException("parentNode must not be null!"); //$NON-NLS-1$
	}

	private DynamicProductTypeNamePage dynamicProductTypeNamePage;

	@Override
	public void addPages()
	{
		ProductTypeID parentDynamicProductTypeID = (ProductTypeID) JDOHelper.getObjectId(parentNode.getJdoObject());

		dynamicProductTypeNamePage = new DynamicProductTypeNamePage(parentDynamicProductTypeID);
		addPage(dynamicProductTypeNamePage);
	}

	@Override
	public boolean performFinish()
	{
		final DynamicProductType dynamicProductType = new DynamicProductType(
				IDGenerator.getOrganisationID(),
				ObjectIDUtil.makeValidIDString(dynamicProductTypeNamePage.getDynamicProductTypeNameBuffer().getText()) + '_' + ProductType.createProductTypeID(),
				parentNode.getJdoObject(),
				dynamicProductTypeNamePage.getInheritanceNature(),
				dynamicProductTypeNamePage.getPackageNature());
		dynamicProductType.getName().copyFrom(dynamicProductTypeNamePage.getDynamicProductTypeNameBuffer());
		dynamicProductType.getFieldMetaData("name").setValueInherited(false); //$NON-NLS-1$

		Job job = new Job(Messages.getString("org.nightlabs.jfire.dynamictrade.admin.createproducttype.CreateDynamicProductTypeWizard.createDynamicProductTypeJob.name")) { //$NON-NLS-1$
			@Implement
			protected IStatus run(ProgressMonitor monitor) throws Exception
			{
				DynamicTradeManager vm = DynamicTradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
				DynamicProductType vt = vm.storeDynamicProductType(dynamicProductType, true, new String[] { FetchPlan.DEFAULT }, 1);
				final ProductTypeID dynamicProductTypeID = (ProductTypeID) JDOHelper.getObjectId(vt);

				// TODO remove this DEBUG stuff
//				StoreManager sm = StoreManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
//				sm.setProductTypeStatus_published(dynamicProductTypeID, false, null, 1);
//				if (ProductType.INHERITANCE_NATURE_LEAF == dynamicProductType.getInheritanceNature()) {
//					sm.setProductTypeStatus_confirmed(dynamicProductTypeID, false, null, 1);
//					sm.setProductTypeStatus_saleable(dynamicProductTypeID, true, false, null, 1);
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
		job.setPriority(Job.SHORT);
		job.schedule();
		return true;
	}

}
