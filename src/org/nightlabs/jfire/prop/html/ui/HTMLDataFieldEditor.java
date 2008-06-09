package org.nightlabs.jfire.prop.html.ui;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.eclipse.ui.fckeditor.FCKEditorInput;
import org.nightlabs.eclipse.ui.fckeditor.IFCKEditorInput;
import org.nightlabs.eclipse.ui.fckeditor.test.TestUtil;
import org.nightlabs.jfire.base.ui.prop.edit.AbstractDataFieldEditor;
import org.nightlabs.jfire.prop.IStruct;
import org.nightlabs.jfire.prop.html.HTMLDataField;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class HTMLDataFieldEditor extends AbstractDataFieldEditor<HTMLDataField>
{
	public HTMLDataFieldEditor(IStruct struct, HTMLDataField data) {
		super(struct, data);
	}

	private Control control;  
	
	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.prop.edit.AbstractDataFieldEditor#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control createControl(Composite parent)
	{
		Label l = new Label(parent, SWT.WRAP);
		if(!getDataField().isEmpty())
			l.setText(getDataField().getHtml());
		else
			l.setText("No content.");
		this.control = l;

		Label l2 = new Label(parent, SWT.WRAP);
		int count = getDataField().getFiles() == null ? 0 : getDataField().getFiles().size();
		l2.setText(String.format("%d files", count));
		
		final Button b = new Button(parent, SWT.PUSH);
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
					RCPUtil.getActiveWorkbenchPage().openEditor(editorInput, "org.nightlabs.eclipse.ui.fckeditor.FCKEditor");
				} catch (Throwable e) {
					MessageDialog.openError(b.getShell(), "Error", "Error: "+e.toString());
					e.printStackTrace();
				}
			}
		});
		
		return l;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.prop.edit.AbstractDataFieldEditor#doRefresh()
	 */
	@Override
	public void doRefresh()
	{
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
	}
}
