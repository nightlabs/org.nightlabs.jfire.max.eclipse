package org.nightlabs.jfire.trade.admin.ui.editor.endcustomertransferpolicy;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.action.InheritanceAction;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.base.ui.language.I18nTextEditorMultiLine;
import org.nightlabs.base.ui.language.I18nTextEditor.EditMode;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.trade.endcustomer.EndCustomerTransferPolicy;

public class EndCustomerTransferPolicySection extends ToolBarSectionPart
{
	private EndCustomerTransferPolicyControllerHelper endCustomerTransferPolicyControllerHelper;

	private I18nTextEditor name;
	private I18nTextEditor description;
	private Label nameLabel;
	private Label descriptionLabel;

	public EndCustomerTransferPolicySection(IFormPage page, Composite parent) {
		super(page, parent, ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE, "End-customer transfer policy");
		((GridData)getSection().getLayoutData()).grabExcessVerticalSpace = false;

		Composite wrapper = new XComposite(getContainer(), SWT.NONE);
		wrapper.setLayout(new GridLayout(2, false));

		nameLabel = new Label(wrapper, SWT.NONE);
		nameLabel.setText("Name");
		name = new I18nTextEditor(wrapper);
		name.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		name.addModifyListener(markDirtyModifyListener);

		descriptionLabel = new Label(wrapper, SWT.NONE);
		descriptionLabel.setText("Description");
		GridData gd = new GridData();
		gd.verticalAlignment = GridData.BEGINNING;
		descriptionLabel.setLayoutData(gd);
		description = new I18nTextEditorMultiLine(wrapper, name.getLanguageChooser());
		description.setLayoutData(new GridData(GridData.FILL_BOTH));
		description.addModifyListener(markDirtyModifyListener);

		getToolBarManager().add(assignAction);
		getToolBarManager().add(inheritAction);
		updateToolBarManager();
	}

	private ModifyListener markDirtyModifyListener = new ModifyListener() {
		@Override
		public void modifyText(ModifyEvent arg0) {
			markDirty();
		}
	};

	private Action assignAction = new Action() {
		{
			setText("Assign");
			setToolTipText("Assign an end-customer transfer policy.");
		}

		@Override
		public void run() {
			// TODO open a wizard for the assignment.
		}
	};

	private InheritanceAction inheritAction = new InheritanceAction() {
		@Override
		public void run() {
			final boolean oldEnabled = inheritAction.isEnabled();
			inheritAction.setEnabled(false);
			Job job = new Job("Loading end-customer transfer policy") {
				@Override
				protected org.eclipse.core.runtime.IStatus run(org.nightlabs.progress.ProgressMonitor monitor) throws Exception {
					try {
						boolean setInherited = isChecked();
//						ProductType pt = productType;
//						if (pt == null || pt.getExtendedProductTypeID() == null)
//							return Status.OK_STATUS;
//
//						ProductType extPT = ProductTypeDAO.sharedInstance().getProductType(
//								pt.getExtendedProductTypeID(),
//								new String[] { FetchPlan.DEFAULT, ProductType.FETCH_GROUP_END_CUSTOMER_TRANSFER_POLICY },
//								NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
//								monitor
//						);
//
//						if (extPT.getEndCustomerTransferPolicy() == null)
//							pt.setEndCustomerTransferPolicy(null);
//						else {
//
//						}
//
//
//						getSection().getDisplay().asyncExec(new Runnable() {
//							public void run() {
////								authorityChanged();
//								markDirty();
//							}
//						});

						return Status.OK_STATUS;
					} finally {
						getSection().getDisplay().asyncExec(new Runnable() {
							public void run() {
								inheritAction.setEnabled(oldEnabled);
							}
						});
					}
				}
			};
			job.setPriority(Job.SHORT);
			job.schedule();
		}
	};

	public void setEndCustomerTransferPolicyControllerHelper(EndCustomerTransferPolicyControllerHelper endCustomerTransferPolicyControllerHelper) {
		if (Display.getCurrent() == null)
			throw new IllegalStateException("Must be called on UI thread!");

		this.endCustomerTransferPolicyControllerHelper = endCustomerTransferPolicyControllerHelper;
		inheritAction.setChecked(false);
		inheritAction.setEnabled(false);

		EndCustomerTransferPolicy ectPolicy = endCustomerTransferPolicyControllerHelper == null ? null : endCustomerTransferPolicyControllerHelper.getEndCustomerTransferPolicy();
		ProductType pt = endCustomerTransferPolicyControllerHelper == null ? null : endCustomerTransferPolicyControllerHelper.getProductType();

		nameLabel.getParent().setEnabled(ectPolicy != null);
		if (pt != null) {
			inheritAction.setChecked(pt.getFieldMetaData(ProductType.FieldName.endCustomerTransferPolicy).isValueInherited());
			if (pt.getExtendedProductTypeID() != null)
				inheritAction.setEnabled(true);
		}

		if (ectPolicy == null) {
			name.setI18nText(null);
			description.setI18nText(null);
		}
		else {
			name.setI18nText(ectPolicy.getName(), EditMode.DIRECT);
			description.setI18nText(ectPolicy.getDescription(), EditMode.DIRECT);
		}
	}
}
