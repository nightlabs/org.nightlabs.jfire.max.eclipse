/**
 *
 */
package org.nightlabs.jfire.reporting.admin.ui.category.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.base.ui.language.I18nTextEditorMultiLine;
import org.nightlabs.base.ui.language.I18nTextEditor.EditMode;
import org.nightlabs.jfire.reporting.layout.ReportRegistryItem;

/**
 * Section that shows and edits name description of a {@link ReportRegistryItem}.
 *
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ReportDetailSection extends ToolBarSectionPart {

	private final I18nTextEditor name;
	private final I18nTextEditorMultiLine description;

	/**
	 * @param page
	 * @param parent
	 * @param style
	 * @param title
	 */
	public ReportDetailSection(IFormPage page, Composite parent) {
		super(page, parent, ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED, "General");
		getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		getSection().setLayout(new GridLayout());

		XComposite client = new XComposite(getSection(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		client.getGridLayout().numColumns = 1;

		getSection().setClient(client);

		name = new I18nTextEditor(client, "Name");
		name.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				markDirty();
			}
		});
		description = new I18nTextEditorMultiLine(client, name.getLanguageChooser(), "Description");
		description.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				markDirty();
			}
		});
		description.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	public void setReportRegistryItem(ReportRegistryItem reportRegistryItem) {
		name.setI18nText(reportRegistryItem.getName(), EditMode.BUFFERED);
		description.setI18nText(reportRegistryItem.getDescription(), EditMode.BUFFERED);
	}

	@Override
	public void commit(boolean onSave) {
		super.commit(onSave);
		name.copyToOriginal();
		description.copyToOriginal();
	}

}
