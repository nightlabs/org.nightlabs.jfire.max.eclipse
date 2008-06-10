package org.nightlabs.jfire.prop.html.ui;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.eclipse.ui.fckeditor.FCKEditorInput;
import org.nightlabs.eclipse.ui.fckeditor.IFCKEditorInput;
import org.nightlabs.jfire.base.ui.prop.edit.AbstractDataFieldEditor;
import org.nightlabs.jfire.prop.IStruct;
import org.nightlabs.jfire.prop.html.HTMLDataField;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class HTMLDataFieldEditor extends AbstractDataFieldEditor<HTMLDataField>
{
	private Composite control;  
	private Label contentLabel;
	
	public HTMLDataFieldEditor(IStruct struct, HTMLDataField data) 
	{
		super(struct, data);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.prop.edit.AbstractDataFieldEditor#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control createControl(Composite parent)
	{
		control = new Composite(parent, SWT.NONE);
		control.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout gl = new GridLayout();
		gl.marginWidth = 0;
		gl.marginHeight = 0;
		
		contentLabel = new Label(control, SWT.WRAP);

		Label l2 = new Label(control, SWT.WRAP);
		int count = getDataField().getFiles() == null ? 0 : getDataField().getFiles().size();
		l2.setText(String.format("%d files", count));
		
		final Button b = new Button(control, SWT.PUSH);
		b.setText("Edit...");
		b.addSelectionListener(new SelectionAdapter() {
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent event)
			{
				try {
					IFCKEditorInput editorInput = new FCKEditorInput(getDataField(), getDataField().getStructFieldID());
					RCPUtil.getActiveWorkbenchPage().openEditor(editorInput, "org.nightlabs.jfire.prop.html.ui.PropFCKEditor");
					// TEST:
					modifyData();
				} catch (Throwable e) {
					MessageDialog.openError(b.getShell(), "Error", String.format("Editor could not be opened: %s", e.getLocalizedMessage()));
					e.printStackTrace();
				}
			}
		});
		
		doRefresh();
		
		return control;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.prop.edit.AbstractDataFieldEditor#doRefresh()
	 */
	@Override
	public void doRefresh()
	{
		if(!getDataField().isEmpty())
			contentLabel.setText(getDataField().getHtml());
		else
			contentLabel.setText("No content.");
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.prop.edit.DataFieldEditor#getControl()
	 */
	@Override
	public Control getControl()
	{
		return control;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.prop.edit.DataFieldEditor#updatePropertySet()
	 */
	@Override
	public void updatePropertySet()
	{
		System.out.println("Update property set!");
	}
}
