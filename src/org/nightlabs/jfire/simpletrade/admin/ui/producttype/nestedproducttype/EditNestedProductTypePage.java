/**
 * 
 */
package org.nightlabs.jfire.simpletrade.admin.ui.producttype.nestedproducttype;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.wizard.DynamicPathWizardPage;
import org.nightlabs.jfire.simpletrade.admin.ui.resource.Messages;
import org.nightlabs.jfire.store.NestedProductType;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 *
 */
public class EditNestedProductTypePage
		extends DynamicPathWizardPage
{
	private int quantity = 1;
	private Spinner quantitySpinner;

	/**
	 * This constructor calls {@link #EditNestedProductTypePage(NestedProductType) } with
	 * <code>null</code>.
	 */
	public EditNestedProductTypePage()
	{
		this(null);
	}

	/**
	 * @param nestedProductType <code>null</code> for a new nested product type
	 *		(default values will be used) or the {@link NestedProductType} that shall
	 *		be edited.
	 */
	public EditNestedProductTypePage(NestedProductType nestedProductType)
	{
		super(EditNestedProductTypePage.class.getName(), Messages.getString("org.nightlabs.jfire.simpletrade.admin.ui.producttype.nestedproducttype.EditNestedProductTypePage.title"), null); //$NON-NLS-1$
		setDescription(Messages.getString("org.nightlabs.jfire.simpletrade.admin.ui.producttype.nestedproducttype.EditNestedProductTypePage.description")); //$NON-NLS-1$

		if (nestedProductType != null)
			this.quantity = nestedProductType.getQuantity();
	}

	public int getQuantity()
	{
		if (quantitySpinner != null)
			return quantitySpinner.getSelection();

		return quantity;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.wizard.DynamicPathWizardPage#createPageContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control createPageContents(Composite parent)
	{
		XComposite page = new XComposite(parent, SWT.NONE, LayoutMode.ORDINARY_WRAPPER);
		new Label(page, SWT.NONE).setText(Messages.getString("org.nightlabs.jfire.simpletrade.admin.ui.producttype.nestedproducttype.EditNestedProductTypePage.quantityLabel.text")); //$NON-NLS-1$
		quantitySpinner = new Spinner(page, SWT.BORDER);
		quantitySpinner.setMinimum(1);
		quantitySpinner.setMaximum(Integer.MAX_VALUE);
		quantitySpinner.setSelection(quantity);
		quantitySpinner.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				quantity = quantitySpinner.getSelection();
			}
		});

		return page;
	}

}
