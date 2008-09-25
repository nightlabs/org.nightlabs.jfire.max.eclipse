/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.textpart;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.base.ui.composite.XComboComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.base.ui.language.II18nTextEditor;
import org.nightlabs.base.ui.language.LanguageChangeEvent;
import org.nightlabs.base.ui.language.LanguageChangeListener;
import org.nightlabs.base.ui.language.LanguageChooser;
import org.nightlabs.base.ui.language.LanguageChooserCombo;
import org.nightlabs.base.ui.language.I18nTextEditor.EditMode;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jfire.reporting.textpart.ReportTextPart;
import org.nightlabs.language.LanguageCf;

/**
 * A {@link Composite} to edit a {@link ReportTextPart}. 
 * This includes its {@link ReportTextPart.Type} its name as well as its content.
 * <p>
 * This Composite operates without changing the {@link ReportTextPart} it
 * was instantiated with, to update the {@link ReportTextPart} call 
 * {@link #updateReportTextPart()}. 
 * </p>
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class ReportTextPartEditComposite extends XComposite {

	private ReportTextPart reportTextPart;
	private XComboComposite<ReportTextPart.Type> typeCombo;
	private LanguageChooser languageChooser;
	private Composite editorWrapper;
	private StackLayout editorWrapperLayout;
	private II18nTextEditor nameEditor;
	private Map<String, IReportTextPartContentEditor> contentEditors = new HashMap<String, IReportTextPartContentEditor>();
	
	/**
	 * Create a new {@link ReportTextPartEditComposite}.
	 * 
	 * @param parent The parent {@link Composite} to use.
	 * @param style The style to apply to the {@link Composite}.
	 */
	public ReportTextPartEditComposite(Composite parent, int style, ReportTextPart reportTextPart) {
		super(parent, style, LayoutMode.LEFT_RIGHT_WRAPPER);
		this.reportTextPart = reportTextPart;
		createContents();
	}

	/**
	 * Create a new {@link ReportTextPartEditComposite}.
	 * 
	 * @param parent The parent {@link Composite} to use.
	 * @param style The style to apply to the {@link Composite}.
	 * @param layoutDataMode The {@link LayoutDataMode} to apply.
	 */
	public ReportTextPartEditComposite(Composite parent,
			int style, LayoutDataMode layoutDataMode, ReportTextPart reportTextPart) {
		super(parent, style, LayoutMode.LEFT_RIGHT_WRAPPER, layoutDataMode);
		this.reportTextPart = reportTextPart; 
		createContents();
	}

	/**
	 * Creates the contents of this composite.
	 */
	protected void createContents() {
		
		XComposite header = new XComposite(this, SWT.NONE, LayoutDataMode.GRID_DATA_HORIZONTAL);
		
		GridLayout layout = new GridLayout(2, false);
		XComposite.configureLayout(LayoutMode.TIGHT_WRAPPER, layout);
		setLayout(layout);
		
		header.setLayout(layout);
		
		typeCombo = new XComboComposite<ReportTextPart.Type>(header, SWT.READ_ONLY);
		typeCombo.setLabelProvider(new TableLabelProvider() {
			@Override
			public String getColumnText(Object element, int columnIndex) {
				return ((ReportTextPart.Type) element).toString();
			}
		});
		typeCombo.setInput(Arrays.asList(new ReportTextPart.Type[] {ReportTextPart.Type.HTML, ReportTextPart.Type.JAVASCRIPT}));
		typeCombo.setSelection(reportTextPart.getType());
		typeCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				switchMode(typeCombo.getSelectedElement());
			}
		});
		
		languageChooser = new LanguageChooserCombo(header);
		languageChooser.addLanguageChangeListener(new LanguageChangeListener() {
			@Override
			public void languageChanged(LanguageChangeEvent event)
			{
				switchLanguage(event.getNewLanguage().getLanguageID());
			}
		});

		nameEditor = new I18nTextEditor(this, languageChooser, "Name");
		nameEditor.setI18nText(reportTextPart.getName(), EditMode.BUFFERED);
		
		editorWrapper = new Composite(this, SWT.NONE);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		editorWrapper.setLayoutData(gd);

		editorWrapperLayout = new StackLayout();
		editorWrapper.setLayout(editorWrapperLayout);
		switchMode(typeCombo.getSelectedElement());
	}
	
	private void switchMode(ReportTextPart.Type type) {
		Collection<LanguageCf> languages = languageChooser.getLanguages();
		for(LanguageCf language : languages) {
			IReportTextPartContentEditor contentEditor = contentEditors.get(language.getLanguageID());
			String content = null;
			if (contentEditor != null) {
				content = contentEditor.getContent();
				Control c = contentEditor.getControl();
				if (c != null && !c.isDisposed())
					c.dispose();
			}
			if (type == ReportTextPart.Type.JAVASCRIPT) {
				contentEditor = new ReportTextPartContentEditorDefault(editorWrapper, SWT.NONE);
			} else if (type == ReportTextPart.Type.HTML) {
				contentEditor = new ReportTextPartContentEditorDefault(editorWrapper, SWT.NONE); // TODO: integrate rich editor
			}
			if (content == null) {
				content = reportTextPart.getContent().getText(language.getLanguageID());
			}
			contentEditor.setContent(content);
			contentEditors.put(language.getLanguageID(), contentEditor);			
		}
		switchLanguage(languageChooser.getLanguage().getLanguageID());
	}
	

	private void switchLanguage(String languageId) {
		IReportTextPartContentEditor contentEditor = contentEditors.get(languageId);
		if(contentEditor == null)
			throw new IllegalStateException("Editor for language "+languageId+" is unknown");
		editorWrapperLayout.topControl = contentEditor.getControl();
		editorWrapper.layout();
	}

	/**
	 * Updates the {@link ReportTextPart} this {@link Composite} was instantiated
	 * with so its attributes and content reflect the values in the ui. 
	 */
	public void updateReportTextPart() {
		reportTextPart.setType(typeCombo.getSelectedElement());
		reportTextPart.getName().copyFrom(nameEditor.getI18nText());
		Collection<LanguageCf> languages = languageChooser.getLanguages();
		for(LanguageCf language : languages) {
			IReportTextPartContentEditor contentEditor = contentEditors.get(language.getLanguageID());
			if (contentEditor != null) {
				String content = contentEditor.getContent();
				reportTextPart.getContent().setText(language.getLanguageID(), content);
			}
		}
	}
}
