package org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.addcurrency;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.wizard.DynamicPathWizardPage;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;

public class CreateCurrencyPage extends DynamicPathWizardPage {


	private Label currencyNameLabel;
	private Text currencyIdText;




	public CreateCurrencyPage()
	{
		super(CreateCurrencyPage.class.getName(), Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.addcurrency.CreateCurrencyPage.title"));
		setDescription(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.addcurrency.CreateCurrencyPage.description"));
	}

	@Override
	public Control createPageContents(Composite parent) {

		XComposite page = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);

		page.setLayout(new GridLayout(1, false));

		Group idGroup = new Group(page, SWT.NONE);

		idGroup.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.addcurrency.CreateCurrencyPage.createNewCurrencyText"));
		idGroup.setLayout(new GridLayout(1, false));
		idGroup.setLayoutData(new GridData(GridData.FILL_BOTH));

		currencyNameLabel = new Label(page, SWT.NONE);
		currencyNameLabel.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.addcurrency.CreateCurrencyPage.NewCurrency"));
        currencyIdText = new Text(idGroup, SWT.SINGLE | SWT.BORDER);

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		currencyNameLabel.setLayoutData(gridData);
		currencyIdText.setLayoutData(gridData);

        currencyIdText.getText();
        currencyNameLabel.getText();


		return page;
	}

	@Override
	public boolean isPageComplete()
	{
		if (currencyNameLabel == null ||currencyIdText==null )
			return false;
	//	return !"".equals(customerGroupNameEditor.getEditText()); //$NON-NLS-1$
		return true;
	}

}
