package org.nightlabs.jfire.pbx.ui;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.nightlabs.base.ui.editor.RestorableSectionPart;
import org.nightlabs.base.ui.entity.editor.EntityEditorUtil;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.jfire.organisation.Organisation;
import org.nightlabs.jfire.pbx.PhoneSystem;
import org.nightlabs.jfire.person.Person;
import org.nightlabs.jfire.person.PersonStruct;
import org.nightlabs.jfire.prop.Struct;
import org.nightlabs.jfire.prop.StructField;
import org.nightlabs.jfire.prop.dao.StructDAO;
import org.nightlabs.jfire.prop.exception.StructBlockNotFoundException;
import org.nightlabs.jfire.prop.exception.StructFieldNotFoundException;
import org.nightlabs.jfire.prop.id.StructID;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 */
public class PhoneSystemCallableFieldSection
extends RestorableSectionPart
{
	private StructFieldTable structFieldTable;
	public PhoneSystemCallableFieldSection(FormPage page, Composite parent) {
		super(parent, page.getEditor().getToolkit(), ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
		createClient(getSection(), page.getEditor().getToolkit());
	}

	private void createClient(Section section, FormToolkit toolkit) {
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		section.setText("Callable Fields");

		Composite container = EntityEditorUtil.createCompositeClient(toolkit, section, 1);

		structFieldTable = new StructFieldTable(container, SWT.NONE);
	}

	@Override
	public boolean setFormInput(Object input) {
		this.phoneSystem = (PhoneSystem) input;
		
		StructID personStructID = 
			StructID.create(Organisation.DEV_ORGANISATION_ID, Person.class, Struct.DEFAULT_SCOPE);
		Struct personStruct = 
			StructDAO.sharedInstance().getStruct(personStructID, new NullProgressMonitor());
		try {
			phoneSystem.addCallableStructField(personStruct.getStructField(PersonStruct.PHONE_PRIMARY));
		} catch (Exception ex) {
			//
		}
		structFieldTable.setInput(phoneSystem.getCallableStructFields());
		return super.setFormInput(input);
	}

	@Override
	public void refresh() {
	}

	@Override
	public void commit(boolean onSave) {
		if (phoneSystem != null) {
		}
		super.commit(onSave);
	}

	private PhoneSystem phoneSystem;
}

class StructFieldTable extends AbstractTableComposite<StructField>
{
	public StructFieldTable(Composite parent, int style) {
		super(parent, style);
		setHeaderVisible(false);
	}

	@Override
	protected void createTableColumns(TableViewer tableViewer, final Table table) {
		TableViewerColumn viewerColumn = new TableViewerColumn(tableViewer, SWT.LEFT);
		table.setLayout(new WeightedTableLayout(new int[] { 1 }));
	}

	@Override
	public void refresh() {
		super.refresh();
	}

	@Override
	protected void setTableProvider(TableViewer tableViewer) {
		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				StructField structField = (StructField)element;
				return structField.getName().getText();
			}
		});
	}
}