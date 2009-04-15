package org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.language.I18nTextEditorMultiLine;
import org.nightlabs.base.ui.language.I18nTextEditor.EditMode;
import org.nightlabs.eclipse.ui.dialog.ResizableTrayDialog;
import org.nightlabs.i18n.I18nText;
import org.nightlabs.jfire.dynamictrade.ui.resource.Messages;
import org.nightlabs.util.ScriptParser;

public class ProductNameDialog
extends ResizableTrayDialog
{
	private I18nText productName;
	private I18nTextEditorMultiLine productNameEditor;
	private boolean editable;
	private boolean isScriptable;
	private Label errorLabel;


	public ProductNameDialog(Shell parentShell, I18nText productName, boolean editable)
	{
		this(parentShell,productName,editable,false);
	}

	public ProductNameDialog(Shell parentShell, I18nText productName, boolean editable,boolean isScriptable)
	{
		super(parentShell, Messages.RESOURCE_BUNDLE);
		this.editable = editable;
		this.isScriptable = isScriptable;
		this.productName = productName;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}
		
	@Override
	protected Control createDialogArea(Composite parent)
	{
		getShell().setText(Messages.getString("org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail.ProductNameDialog.title")); //$NON-NLS-1$
		Composite area = (Composite) super.createDialogArea(parent);
		area.setLayout(new GridLayout(1,false));
		this.productNameEditor = new I18nTextEditorMultiLine(area);
		this.productNameEditor.setEditable(editable);
		this.productNameEditor.setI18nText(productName, EditMode.BUFFERED);
		this.productNameEditor.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.errorLabel = new Label(area, SWT.WRAP);
		this.errorLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));	
		this.errorLabel.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
		
		return area;
	}

	@Override
	protected void okPressed()
	{
		if(isScriptable)
			if(!checkScript())
				return;
		
		super.okPressed();
		productNameEditor.copyToOriginal();		
	}

	private boolean checkScript()
	{
		ScriptParser script = new ScriptParser(productNameEditor.getI18nText());
		String err = script.validateContent();
		if(err !=null)
		{
			errorLabel.setText(err);
			productNameEditor.addFocusListener(  new FocusListener(){
				@Override
				public void focusGained(FocusEvent arg0) {
					errorLabel.setText("");	
					productNameEditor.removeFocusListener(this);
				}				
				@Override
				public void focusLost(FocusEvent arg0) {
					// TODO Auto-generated method stub

				}
			});
			return false;
		}
		return true;
	}
}
