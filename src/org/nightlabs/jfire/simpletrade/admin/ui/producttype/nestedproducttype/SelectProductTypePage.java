package org.nightlabs.jfire.simpletrade.admin.ui.producttype.nestedproducttype;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.ui.wizard.DynamicPathWizardPage;
import org.nightlabs.jfire.simpletrade.admin.ui.producttype.ProductTypeTree;
import org.nightlabs.jfire.simpletrade.admin.ui.resource.Messages;
import org.nightlabs.jfire.simpletrade.store.SimpleProductType;
import org.nightlabs.jfire.store.ProductType;

public class SelectProductTypePage
		extends DynamicPathWizardPage
{
	private ProductTypeTree productTypeTree;

	public SelectProductTypePage()
	{
		super(SelectProductTypePage.class.getName(), Messages.getString("org.nightlabs.jfire.simpletrade.admin.ui.producttype.nestedproducttype.SelectProductTypePage.title"), null); //$NON-NLS-1$
		setDescription(Messages.getString("org.nightlabs.jfire.simpletrade.admin.ui.producttype.nestedproducttype.SelectProductTypePage.description")); //$NON-NLS-1$
	}

	@Override
	public Control createPageContents(Composite parent)
	{
		productTypeTree = new ProductTypeTree(parent, SWT.NONE);
		productTypeTree.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event)
			{
				Display.getDefault().asyncExec(new Runnable()
				{
					public void run()
					{
						getWizard().getContainer().updateButtons();
					}
				});
			}
		});

		return productTypeTree;
	}

	@Override
	public boolean isPageComplete()
	{
		return
				!productTypeTree.getSelectedElements().isEmpty() &&
				productTypeTree.getSelectedElements().iterator().next().getInheritanceNature() == ProductType.INHERITANCE_NATURE_LEAF;
	}

	public SimpleProductType getSelectedProductType()
	{
		if (productTypeTree.getSelectedElements().isEmpty())
			return null;

		return productTypeTree.getSelectedElements().iterator().next();
	}
}
