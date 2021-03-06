package org.nightlabs.jfire.trade.admin.ui.editor.ownervendor;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.action.InheritanceAction;
import org.nightlabs.base.ui.composite.FadeableComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.job.FadeableCompositeJob;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypePageController;
import org.nightlabs.jfire.trade.admin.ui.editor.IProductTypeSectionPart;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;


/**
 * @author Fitas [at] NightLabs [dot] de
 *
 */
public class OwnerConfigSection
extends ToolBarSectionPart
implements IProductTypeSectionPart
{
	private InheritAction inheritAction;
	private LegalEntityEditComposite ownerEditComposite = null;
	private ProductType productType = null;
	private FadeableComposite fadeableComposite;
	private LegalEntity originalEntity;

	public OwnerConfigSection(IFormPage page, Composite parent, int style)
	{
		super(page, parent, style, Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.ownervendor.OwnerConfigSection.title")); //$NON-NLS-1$
		this.fadeableComposite = new FadeableComposite(getContainer(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		this.ownerEditComposite = new LegalEntityEditComposite(fadeableComposite,
				SWT.NONE);
		this.ownerEditComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		ownerEditComposite.addLegalEntityValueChangedListener(
			new ILegalEntityValueChangedListener()
			{
				public void legalEntityValueChanged()
				{
					// if value has changed
					setInheritanceSelection(false);
					markDirty();
				}
			}
		);

		inheritAction = new InheritAction();
		registerAction(inheritAction);
		inheritAction.setEnabled(false);

		updateToolBarManager();
	}

	public LegalEntityEditComposite getOwnerEditComposite() {
		return ownerEditComposite;
	}

	public ProductType getProductType() {
		return productType;
	}

	private AbstractProductTypePageController<ProductType> productTypePageController;

	public void setProductTypePageController(AbstractProductTypePageController<ProductType> pageController)
	{
		if (pageController == null || getSection() == null || getSection().isDisposed())
			return;

		productTypePageController = pageController;
		this.productType = pageController.getProductType();
		originalEntity = pageController.getProductType().getOwner();
		getOwnerEditComposite().setLegalEntity(originalEntity);
		setInheritanceSelection(productType.getFieldMetaData(ProductType.FieldName.owner).isValueInherited());

		inheritAction.setEnabled(productType.getExtendedProductTypeID() != null);
	}

	public AbstractProductTypePageController<ProductType> getProductTypePageController()
	{
		return productTypePageController;
	}

	public ProductTypeID getExtendedProductTypeID() {
		return productType.getExtendedProductTypeID();
	}

	@Override
	public void commit(boolean save) {
		productType.getFieldMetaData(ProductType.FieldName.owner).setValueInherited(inheritAction.isChecked());
		if (ownerEditComposite != null && isDirty())
		{
			productType.setOwner(ownerEditComposite.getLegalEntity());
		}
		super.commit(save);
	}

	protected boolean getInheritanceSelection() {
		return inheritAction.isChecked();
	}

	protected void setInheritanceSelection(boolean selection) {
		inheritAction.setChecked(selection);
	}

	protected void inheritPressed() {
		if( inheritAction.isChecked() )
		{
			//final ProductType extendedProductType = productTypePageController.getExtendedProductType(new NullProgressMonitor(), getExtendedProductTypeID()); // since this monitor is not yet started, we can directly pass it
			FadeableCompositeJob job = new FadeableCompositeJob(Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.ownervendor.OwnerConfigSection.loadExtendedProductTypeJob.name"), fadeableComposite, null) { //$NON-NLS-1$
				@Override
				protected IStatus run(ProgressMonitor monitor, Object source)
				throws Exception
				{
					final ProductType extendedProductType = productTypePageController.getExtendedProductType(monitor, getExtendedProductTypeID() ); // since this monitor is not yet started, we can directly pass it
					Display.getDefault().syncExec(new Runnable()
					{
						public void run()
						{
							if (extendedProductType != null)
								getOwnerEditComposite().setLegalEntity(extendedProductType.getOwner());
						}
					});

					return Status.OK_STATUS;
				}
			};
			job.setPriority(Job.SHORT);
			job.schedule();
		}
		else
			getOwnerEditComposite().setLegalEntity(originalEntity);
	}

	class InheritAction
	extends InheritanceAction {
		@Override
		public void run() {
			if (productType == null)
				return;

			inheritPressed();
			updateToolBarManager();
			markDirty();
		}

		public void updateState(ProductType productType) {
			setChecked(productType.getProductTypeLocal().getFieldMetaData(ProductType.FieldName.owner).isValueInherited());
		}
	}

}