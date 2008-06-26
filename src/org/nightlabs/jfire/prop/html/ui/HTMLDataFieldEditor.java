package org.nightlabs.jfire.prop.html.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPropertyListener;
import org.nightlabs.eclipse.ui.fckeditor.FCKEditorComposite;
import org.nightlabs.eclipse.ui.fckeditor.FCKEditorInput;
import org.nightlabs.eclipse.ui.fckeditor.IFCKEditor;
import org.nightlabs.eclipse.ui.fckeditor.IFCKEditorInput;
import org.nightlabs.htmlcontent.IFCKEditorContent;
import org.nightlabs.jfire.base.ui.prop.edit.AbstractDataFieldEditor;
import org.nightlabs.jfire.prop.IStruct;
import org.nightlabs.jfire.prop.html.HTMLDataField;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class HTMLDataFieldEditor extends AbstractDataFieldEditor<HTMLDataField>
{
	private Composite control;
	private FCKEditorComposite editorComposite;

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

		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan = 2;
		control.setLayoutData(gd);

		GridLayout gl = new GridLayout();
		gl.marginWidth = 0;
		gl.marginHeight = 0;
		control.setLayout(gl);

		// TODO: add language switcher here...

		IFCKEditorInput editorInput = new FCKEditorInput(getDataField(), getDataField().getStructFieldID());
		editorComposite = new FCKEditorComposite(control, SWT.BORDER, editorInput);
		editorComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		editorComposite.getEditor().addPropertyListener(new IPropertyListener() {
			@Override
			public void propertyChanged(Object src, int propertyId) {
				if(propertyId == IFCKEditor.PROP_DIRTY)
					modifyData();
			}
		});

//
//		contentLabel = new Label(control, SWT.WRAP);
//
//		fileLabel = new Label(control, SWT.WRAP);
//
//		final Button b = new Button(control, SWT.PUSH);
//		b.setText("Edit...");
//		b.addSelectionListener(new SelectionAdapter() {
//			/* (non-Javadoc)
//			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
//			 */
//			@Override
//			public void widgetSelected(SelectionEvent event)
//			{
//				try {
//					IFCKEditorInput editorInput = new FCKEditorInput(getDataField(), getDataField().getStructFieldID());
//					RCPUtil.getActiveWorkbenchPage().openEditor(editorInput, "org.nightlabs.jfire.prop.html.ui.PropFCKEditor");
//					// TEST:
//					modifyData();
//				} catch (Throwable e) {
//					MessageDialog.openError(b.getShell(), "Error", String.format("Editor could not be opened: %s", e.getLocalizedMessage()));
//					e.printStackTrace();
//				}
//			}
//		});

		doRefresh();

		return control;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.prop.edit.AbstractDataFieldEditor#doRefresh()
	 */
	@Override
	public void doRefresh()
	{
		// TODO: handle input change

//		if(!getDataField().isEmpty())
//			contentLabel.setText(getDataField().getHtml());
//		else
//			contentLabel.setText("No content.");
//
//		int count = getDataField().getFiles() == null ? 0 : getDataField().getFiles().size();
//		fileLabel.setText(String.format("%d files", count));
//
//		contentLabel.getParent().layout(true, true);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.prop.edit.DataFieldEditor#getControl()
	 */
	@Override
	public Control getControl()
	{
		return control;
	}

	private class MyWaitListener implements IPropertyListener
	{
		boolean committed = false;
		@Override
		public void propertyChanged(Object source, int propertyId) {
			if(propertyId == IFCKEditor.PROP_DIRTY && source instanceof IFCKEditor && !((IFCKEditor)source).isDirty()) {
				committed = true;
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.prop.edit.DataFieldEditor#updatePropertySet()
	 */
	@Override
	public void updatePropertySet()
	{
		long timeoutMillis = 5000;
		IFCKEditor editor = editorComposite.getEditor();
		MyWaitListener propertyListener = new MyWaitListener();
		if(editor.isDirty()) {
			editor.addPropertyListener(propertyListener);
			try {
				editor.commit();
				// wait on the ui thread until the server commit round-trip 
				// is back and the editor is not dirty anymore:
				long startTimeMillis = System.currentTimeMillis();
				Display display = control.getDisplay();
				while(!display.isDisposed() && !propertyListener.committed) {
					if(System.currentTimeMillis() - startTimeMillis > timeoutMillis)
						throw new RuntimeException("Editor commit timeout");
					if(!display.readAndDispatch())
						display.sleep();
				}
			} finally {
				editor.removePropertyListener(propertyListener);
			}
			// waiting done. Commit into the property set:
			IFCKEditorContent editorContent = editor.getEditorInput().getEditorContent();
			HTMLDataField dataField = getDataField();
			dataField.setHtml(editorContent.getHtml());
			dataField.setFiles(editorContent.getFiles());
			// commit done.
		}
	}
}
