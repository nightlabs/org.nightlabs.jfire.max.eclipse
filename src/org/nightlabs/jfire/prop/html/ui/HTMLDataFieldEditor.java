package org.nightlabs.jfire.prop.html.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPropertyListener;
import org.nightlabs.base.ui.language.LanguageChangeEvent;
import org.nightlabs.base.ui.language.LanguageChangeListener;
import org.nightlabs.base.ui.language.LanguageChooserCombo;
import org.nightlabs.eclipse.ui.fckeditor.FCKEditorComposite;
import org.nightlabs.eclipse.ui.fckeditor.FCKEditorInput;
import org.nightlabs.eclipse.ui.fckeditor.IFCKEditor;
import org.nightlabs.eclipse.ui.fckeditor.IFCKEditorInput;
import org.nightlabs.htmlcontent.IFCKEditorContent;
import org.nightlabs.jfire.base.ui.prop.edit.AbstractDataFieldEditor;
import org.nightlabs.jfire.base.ui.prop.edit.DataFieldEditorLayoutData;
import org.nightlabs.jfire.prop.IStruct;
import org.nightlabs.jfire.prop.html.HTMLDataField;
import org.nightlabs.language.LanguageCf;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class HTMLDataFieldEditor extends AbstractDataFieldEditor<HTMLDataField>
{
	private Composite control;
	private LanguageChooserCombo languageChooser;
	private Map<String, FCKEditorComposite> editorComposites;
	private Composite editorWrapper;
	private StackLayout editorWrapperLayout;

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

		languageChooser = new LanguageChooserCombo(control);
		languageChooser.addLanguageChangeListener(new LanguageChangeListener() {
			@Override
			public void languageChanged(LanguageChangeEvent event)
			{
				switchLanguage(event.getNewLanguage().getLanguageID());
			}
		});

		editorWrapper = new Composite(control, SWT.NONE);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		editorWrapper.setLayoutData(gd);

		editorWrapperLayout = new StackLayout();
		editorWrapper.setLayout(editorWrapperLayout);

		List<LanguageCf> languages = languageChooser.getLanguages();
		editorComposites = new HashMap<String, FCKEditorComposite>(languages.size());
		for(LanguageCf language : languages) {
			IFCKEditorContent contentWrapper = getDataField().getContent(language.getLanguageID());
			System.out.println("CONTENT: "+contentWrapper.getHtml());
			IFCKEditorInput editorInput = new FCKEditorInput(contentWrapper, getDataField().getStructFieldID());
			FCKEditorComposite editorComposite = new FCKEditorComposite(editorWrapper, SWT.BORDER, editorInput);
			editorComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			editorComposite.getEditor().addPropertyListener(new IPropertyListener() {
				@Override
				public void propertyChanged(Object src, int propertyId) {
					if(propertyId == IFCKEditor.PROP_DIRTY)
						notifyChangeListeners();
				}
			});
			editorComposites.put(language.getLanguageID(), editorComposite);
		}
		switchLanguage(languageChooser.getLanguage().getLanguageID());

		doRefresh();

		return control;
	}

	private void switchLanguage(String languageId)
	{
		FCKEditorComposite editorComposite = editorComposites.get(languageId);
		if(editorComposite == null)
			throw new IllegalStateException("Editor for language "+languageId+" is unknown");
		editorWrapperLayout.topControl = editorComposite;
		editorWrapper.layout();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.prop.edit.AbstractDataFieldEditor#getLayoutData()
	 */
	@Override
	public DataFieldEditorLayoutData getLayoutData()
	{
		DataFieldEditorLayoutData ld = new DataFieldEditorLayoutData(DataFieldEditorLayoutData.FILL_BOTH);
		ld.minimumHeight = 450;
		return ld;
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
		for(Map.Entry<String, FCKEditorComposite> entry : editorComposites.entrySet()) {
			String languageId = entry.getKey();
			FCKEditorComposite editorComposite = entry.getValue();
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
				dataField.setText(languageId, editorContent.getHtml());
				dataField.setFiles(editorContent.getFiles());
				// commit done.
			}
		}
	}
}
