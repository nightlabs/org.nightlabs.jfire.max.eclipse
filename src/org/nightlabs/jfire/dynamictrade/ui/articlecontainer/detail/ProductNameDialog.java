package org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.dialog.CenteredDialog;
import org.nightlabs.base.ui.language.I18nTextEditorMultiLine;
import org.nightlabs.base.ui.language.I18nTextEditor.EditMode;
import org.nightlabs.i18n.I18nText;
import org.nightlabs.jfire.dynamictrade.ui.resource.Messages;

public class ProductNameDialog
extends CenteredDialog
{
	private I18nText productName;
	private I18nTextEditorMultiLine productNameEditor;
	private boolean editable;
	
	public ProductNameDialog(Shell parentShell, I18nText productName, boolean editable)
	{
		super(parentShell);
		this.editable = editable;
		this.productName = productName;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	@Override
	protected Point getInitialSize()
	{
		return new Point(400, 400);
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		getShell().setText(Messages.getString("org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail.ProductNameDialog.title")); //$NON-NLS-1$
		Composite area = (Composite) super.createDialogArea(parent);
		this.productNameEditor = new I18nTextEditorMultiLine(area);
		this.productNameEditor.setEditable(editable);
		this.productNameEditor.setI18nText(productName, EditMode.BUFFERED);
		this.productNameEditor.setLayoutData(new GridData(GridData.FILL_BOTH));
		return area;
	}

	@Override
	protected void okPressed()
	{
		super.okPressed();
		productNameEditor.copyToOriginal();
	}
}
