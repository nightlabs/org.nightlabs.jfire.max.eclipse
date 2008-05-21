package org.nightlabs.jfire.trade.admin.ui.editor.authority;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.base.ui.language.I18nTextEditorMultiLine;
import org.nightlabs.base.ui.language.I18nTextEditor.EditMode;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.base.admin.ui.editor.authority.AssignAuthorityWizard;
import org.nightlabs.jfire.base.admin.ui.editor.authority.AuthorityPageControllerHelper;
import org.nightlabs.jfire.base.admin.ui.editor.authority.InheritedAuthorityResolver;
import org.nightlabs.jfire.security.Authority;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypePageController;
import org.nightlabs.jfire.trade.admin.ui.editor.IProductTypeSectionPart;
import org.nightlabs.progress.ProgressMonitor;

public class AuthoritySection
extends ToolBarSectionPart
implements IProductTypeSectionPart
{
	private I18nTextEditor name;
	private I18nTextEditor description;

	public AuthoritySection(IFormPage page, Composite parent) {
		super(page, parent, ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE, "Authority");
		((GridData)getSection().getLayoutData()).grabExcessVerticalSpace = false;

		name = new I18nTextEditor(getContainer());
		name.addModifyListener(markDirtyModifyListener);
		description = new I18nTextEditorMultiLine(getContainer());
		description.addModifyListener(markDirtyModifyListener);

		assignAuthorityAction.setEnabled(false);
		getToolBarManager().add(assignAuthorityAction);
		updateToolBarManager();
	}

	private ModifyListener markDirtyModifyListener = new ModifyListener() {
		@Override
		public void modifyText(ModifyEvent arg0) {
			markDirty();
		}
	};

	private ProductType productType;
	private AuthorityPageController authorityPageController;
	private AuthorityPageControllerHelper authorityPageControllerHelper;

	private Action assignAuthorityAction = new Action() {
		{
			setText("Assign authority");
		}

		@Override
		public void run() {
			if (authorityPageControllerHelper == null)
				return;

			final AssignAuthorityWizard assignAuthorityWizard = new AssignAuthorityWizard(
					authorityPageControllerHelper.getAuthorityTypeID(),
					new InheritedAuthorityResolver() {
						@Override
						public Authority getInheritedAuthority(ProgressMonitor monitor) {
							if (productType.getExtendedProductTypeID() == null)
								return null;

							ProductType extendedProductType = getProductTypePageController().getExtendedProductType(
									monitor,
									productType.getExtendedProductTypeID());

							return extendedProductType.getProductTypeLocal().getSecuringAuthority();
						}
					});
			DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(getSection().getShell(), assignAuthorityWizard);
			if (dialog.open() == Dialog.OK) {
				Job job = new Job("Loading authority") {
					protected org.eclipse.core.runtime.IStatus run(org.nightlabs.progress.ProgressMonitor monitor) throws Exception {
						authorityPageControllerHelper.load(
								assignAuthorityWizard.getAuthorityTypeID(),
								assignAuthorityWizard.getAuthorityID(),
								assignAuthorityWizard.getNewAuthority(),
								monitor);

						getSection().getDisplay().asyncExec(new Runnable() {
							public void run() {
								authorityChanged();
								markDirty();
							}
						});

						return Status.OK_STATUS;
					}
				};
				job.setPriority(Job.SHORT);
				job.schedule();
			}
		}
	};

	@Override
	public ProductType getProductType() {
		return productType;
	}

	@Override
	public AuthorityPageController getProductTypePageController() {
		return authorityPageController;
	}

	@Override
	public void setProductTypePageController(AbstractProductTypePageController<ProductType> productTypeDetailPageController) {
		authorityPageController = (AuthorityPageController) productTypeDetailPageController;
		productType = productTypeDetailPageController.getControllerObject();
		authorityPageControllerHelper = authorityPageController.getAuthorityPageControllerHelper();
		assignAuthorityAction.setEnabled(authorityPageControllerHelper != null);
		getSection().getDisplay().asyncExec(new Runnable() {
			public void run() {
				authorityChanged();
			}
		});
	}

	private void authorityChanged() {
		if (authorityPageControllerHelper.getAuthority() == null) {
			name.setI18nText(null, EditMode.DIRECT);
			description.setI18nText(null, EditMode.DIRECT);

			setMessage("There is no authority assigned to this product type.");
			setEnabled(false);
		}
		else {
			name.setI18nText(authorityPageController.getAuthorityPageControllerHelper().getAuthority().getName(), EditMode.DIRECT);
			description.setI18nText(authorityPageController.getAuthorityPageControllerHelper().getAuthority().getDescription(), EditMode.DIRECT);

			setMessage(null);
			setEnabled(true);
		}
	}

	private void setEnabled(boolean enabled) {
		name.setEnabled(enabled);
		description.setEnabled(enabled);
	}
}
