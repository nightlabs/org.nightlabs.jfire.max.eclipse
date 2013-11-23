package org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail;

import javax.script.ScriptException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.composite.MessageComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.language.I18nTextEditorMultiLine;
import org.nightlabs.base.ui.language.I18nTextEditor.EditMode;
import org.nightlabs.base.ui.message.MessageType;
import org.nightlabs.eclipse.ui.dialog.ResizableTrayDialog;
import org.nightlabs.i18n.I18nText;
import org.nightlabs.jfire.dynamictrade.ui.resource.Messages;
import org.nightlabs.script.JSHTMLExecuter;


/**
 * 
 * @author Fitas Amine <!-- fitas[at]nightlabs[dot]de -->
 */
public class ProductNameDialog
extends ResizableTrayDialog
{
			

	private I18nTextEditorMultiLine productNameEditor;
	private boolean editable;
	private Button previewEditButton;
	private MessageComposite statusMessageLabel;
	private ProductNameDialogType productNameDialogType;
	private I18nText productName;
	XComposite previewEditButtonComposite;
	
	
	public I18nText getProductName() {
		return productName;
	}
	public void setProductName(I18nText productName) {
		this.productName = productName;
	}

	public ProductNameDialog(Shell parentShell, I18nText productName, boolean editable)
	{
		this(parentShell,productName,editable,ProductNameDialogType.TEXT_EDIT);
	}


	public ProductNameDialog(Shell parentShell, I18nText productName, boolean editable,ProductNameDialogType productNameDialogType)
	{
		super(parentShell, Messages.RESOURCE_BUNDLE);
		this.editable = editable;
		this.productName = productName;
		this.productNameDialogType = productNameDialogType;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}
	
	@Override
	protected Control createDialogArea(Composite parent)
	{
		getShell().setText(Messages.getString("org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail.ProductNameDialog.title")); //$NON-NLS-1$
		Composite area = (Composite) super.createDialogArea(parent);
		area.setLayout(new GridLayout(1,false));
		if(this.productNameDialogType != ProductNameDialogType.TEXT_EDIT)
		{
			this.statusMessageLabel = new MessageComposite(area, SWT.NONE, "", MessageType.INFORMATION); //$NON-NLS-1$
			this.statusMessageLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			this.statusMessageLabel.setMessage(Messages.getString("org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail.ProductNameDialog.message"),MessageType.INFORMATION);		 //$NON-NLS-1$	
		}
		this.productNameEditor = new I18nTextEditorMultiLine(area);
		this.productNameEditor.setEditable(editable);
		this.productNameEditor.setI18nText(productName, EditMode.BUFFERED);
		this.productNameEditor.setLayoutData(new GridData(GridData.FILL_BOTH));
		// adds preview button in the case of Script Editing
		if(this.productNameDialogType != ProductNameDialogType.TEXT_EDIT)
		{			
			previewEditButtonComposite = new XComposite(area, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
			previewEditButtonComposite.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL));
			RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL);
			rowLayout.wrap = false;
			rowLayout.pack = false;
			rowLayout.justify = true;
			previewEditButtonComposite.setLayout(rowLayout);
			previewEditButton = new Button(previewEditButtonComposite, SWT.PUSH);
			//previewEditButton.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL));
			previewEditButton.setText(Messages.getString("org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail.ProductNameDialog.preview"));  //$NON-NLS-1$
			//previewEditButton.setSize(new Point(200,20));
			previewEditButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent arg0)
				{
					toggleDialogScriptMode();
				}
				});
		}
		setDialogMode(productNameDialogType);
		return area;
	}

	@Override
	protected void okPressed()
	{
		if(this.productNameDialogType != ProductNameDialogType.TEXT_EDIT)
			if(!validateScript())
				return;
		super.okPressed();
		productNameEditor.copyToOriginal();
	}

	
	private void toggleDialogScriptMode()
	{	
		switch (productNameDialogType) {
		case SCRIPT_EDIT:
			setDialogMode(ProductNameDialogType.SCRIPT_PREVIEW);
			break;
		case SCRIPT_PREVIEW:
			setDialogMode(ProductNameDialogType.SCRIPT_EDIT);
			break;
		}
	}
	
	private void setDialogMode(ProductNameDialogType type)
	{
		// you can not switch from Text Mode to Script !!!
		if(productNameDialogType == ProductNameDialogType.TEXT_EDIT)
			return;		
		this.productNameDialogType = type;
		switch (type) {
		case SCRIPT_EDIT:
			this.productNameEditor.setEditable(editable);
			this.productNameEditor.setI18nText(getProductName(), EditMode.BUFFERED);			
			this.previewEditButton.setText(Messages.getString("org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail.ProductNameDialog.preview"));  //$NON-NLS-1$	
			this.statusMessageLabel.setMessage(Messages.getString("org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail.ProductNameDialog.message"),MessageType.INFORMATION);		 //$NON-NLS-1$	
			previewEditButton.setFocus();
			break;
		case SCRIPT_PREVIEW:
			JSHTMLExecuter script = new JSHTMLExecuter(productNameEditor.getI18nText());
			if(!(script.containsValidScript() > 0))
				return;
			// store the previous contents
			getProductName().copyFrom(productNameEditor.getI18nText());
			try{
				I18nText result = script.execute();	
				this.productNameEditor.setI18nText(result, EditMode.BUFFERED);
				this.productNameEditor.setEditable(false);
				this.previewEditButton.setText(Messages.getString("org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail.ProductNameDialog.edit"));  //$NON-NLS-1$	
				this.statusMessageLabel.setMessage("Script preview",MessageType.INFORMATION);
				previewEditButton.setFocus();
			}			
			catch (ScriptException e) {	
				this.productNameDialogType = ProductNameDialogType.SCRIPT_EDIT;
				productNameEditor.setEditable(editable);
				previewEditButton.setSelection(false);
				previewEditButton.setFocus();
				statusMessageLabel.setMessage(e.getMessage(),MessageType.ERROR);
				productNameEditor.addFocusListener(  new FocusListener(){
					@Override
					public void focusGained(FocusEvent arg0) {
						statusMessageLabel.setMessage(Messages.getString("org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail.ProductNameDialog.message"),MessageType.INFORMATION);		 //$NON-NLS-1$
						productNameEditor.removeFocusListener(this);
					}
					@Override
					public void focusLost(FocusEvent arg0) {
						// TODO Auto-generated method stub	
					}
				});
			}	
			break;
		}
		previewEditButtonComposite.pack();
	}

	private boolean validateScript()
	{
		JSHTMLExecuter script = new JSHTMLExecuter(productNameEditor.getI18nText());
		String err = script.validateContent();
		if(err != null)
		{
			statusMessageLabel.setMessage(err,MessageType.ERROR);
			productNameEditor.addFocusListener(  new FocusListener(){
				@Override
				public void focusGained(FocusEvent arg0) {
					statusMessageLabel.setMessage(Messages.getString("org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail.ProductNameDialog.message"),MessageType.INFORMATION);		 //$NON-NLS-1$
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
