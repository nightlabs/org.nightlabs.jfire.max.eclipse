/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.textpart;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.base.ui.composite.LabeledText;
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
import org.nightlabs.jfire.reporting.ui.resource.Messages;
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
	private ListenerList changedListeners = new ListenerList();
	boolean showTextPartID = false;
	
	private ModifyListener modifyListener = new ModifyListener() {
		@Override
		public void modifyText(ModifyEvent e) {
			notifyChangedListeners();
		}
	};
	
	private LanguageChangeListener languageListener = new LanguageChangeListener() {
		@Override
		public void languageChanged(LanguageChangeEvent event)
		{
			switchLanguage(event.getNewLanguage().getLanguageID());
		}
	};
	private LabeledText reportTextPartID;
	/**
	 * Create a new {@link ReportTextPartEditComposite}.
	 * 
	 * @param parent The parent {@link Composite} to use.
	 * @param style The style to apply to the {@link Composite}.
	 */
	public ReportTextPartEditComposite(
			Composite parent, int style, ReportTextPart reportTextPart, 
			LanguageChooser languageChooser, boolean showTextPartID) {
		super(parent, style, LayoutMode.LEFT_RIGHT_WRAPPER);
		this.reportTextPart = reportTextPart;
		this.languageChooser = languageChooser;
		this.showTextPartID = showTextPartID;
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
		header.setLayout(layout);
		
		if (showTextPartID) {
			reportTextPartID = new LabeledText(header, Messages.getString("org.nightlabs.jfire.reporting.ui.textpart.ReportTextPartEditComposite.label.identifier"), SWT.READ_ONLY); //$NON-NLS-1$
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = 2;
			reportTextPartID.setLayoutData(gd);
			reportTextPartID.setText(reportTextPart.getReportTextPartID());
		}
		
		Composite nameParent = languageChooser != null ? header : this;
		
		if (languageChooser == null) {
			languageChooser = new LanguageChooserCombo(header);
			((LanguageChooserCombo) languageChooser).setLayoutData(new GridData(GridData.VERTICAL_ALIGN_END));
		}
		languageChooser.addLanguageChangeListener(languageListener);
		addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				languageChooser.removeLanguageChangeListener(languageListener);
			}
		});

		nameEditor = new I18nTextEditor(nameParent, languageChooser, Messages.getString("org.nightlabs.jfire.reporting.ui.textpart.ReportTextPartEditComposite.label.name")); //$NON-NLS-1$
		nameEditor.setI18nText(reportTextPart.getName(), EditMode.BUFFERED);
		nameEditor.addModifyListener(modifyListener);
		editorWrapper = new Composite(this, SWT.NONE);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		editorWrapper.setLayoutData(gd);

		editorWrapperLayout = new StackLayout();
		editorWrapper.setLayout(editorWrapperLayout);
		
		typeCombo = new XComboComposite<ReportTextPart.Type>(
				header, getBorderStyle() | SWT.READ_ONLY,
				Messages.getString("org.nightlabs.jfire.reporting.ui.textpart.ReportTextPartEditComposite.combo.textPartType")); //$NON-NLS-1$
		typeCombo.setLabelProvider(new TableLabelProvider() {
			@Override
			public String getColumnText(Object element, int columnIndex) {
				return ((ReportTextPart.Type) element).toString();
			}
		});
//		typeCombo.setInput(Arrays.asList(new ReportTextPart.Type[] {ReportTextPart.Type.HTML, ReportTextPart.Type.JAVASCRIPT}));
		typeCombo.setInput(Arrays.asList(new ReportTextPart.Type[] {ReportTextPart.Type.JSHTML}));
		typeCombo.setSelection(reportTextPart.getType());
		typeCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				switchMode(typeCombo.getSelectedElement());
				notifyChangedListeners();
			}
		});
		
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
			if (type == ReportTextPart.Type.JSHTML) {
				contentEditor = new ReportTextPartContentEditorDefault(editorWrapper, SWT.NONE); // TODO: integrate rich editor
//			} else if (type == ReportTextPart.Type.HTML) {
//				contentEditor = new ReportTextPartContentEditorDefault(editorWrapper, SWT.NONE);
			}
			if (content == null) {
				if (reportTextPart.getContent().containsLanguageID(language.getLanguageID()))
					content = reportTextPart.getContent().getText(language.getLanguageID());
				else
					content = ""; //$NON-NLS-1$
			}
			contentEditor.setContent(content);
			contentEditor.addModifyListener(modifyListener);
			contentEditors.put(language.getLanguageID(), contentEditor);			
		}
		switchLanguage(languageChooser.getLanguage().getLanguageID());
	}
	

	private void switchLanguage(String languageId) {
		IReportTextPartContentEditor contentEditor = contentEditors.get(languageId);
		if(contentEditor == null)
			throw new IllegalStateException("Editor for language "+languageId+" is unknown"); //$NON-NLS-1$ //$NON-NLS-2$
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
	
	public void addReportTextPartChangedListener(IReportTextPartChangedListener listener) {
		changedListeners.add(listener);
	}
	
	public void removeReportTextPartChangedListener(IReportTextPartChangedListener listener) {
		changedListeners.remove(listener);
	}
	
	protected void notifyChangedListeners() {
		Object[] listeners = changedListeners.getListeners();
		if (listeners.length <= 0)
			return;
		ReportTextPartChangedEvent event = new ReportTextPartChangedEvent(reportTextPart);
		for (Object listener : listeners) {
			if (listener instanceof IReportTextPartChangedListener) {
				((IReportTextPartChangedListener) listener).reportTextPartChanged(event);
			}
		}
	}
	
}
