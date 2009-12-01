package org.nightlabs.jfire.pbx.ui;

import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.jfire.organisation.Organisation;
import org.nightlabs.jfire.pbx.PhoneSystem;
import org.nightlabs.jfire.pbx.ui.resource.Messages;
import org.nightlabs.jfire.person.Person;
import org.nightlabs.jfire.person.PersonStruct;
import org.nightlabs.jfire.prop.DataField;
import org.nightlabs.jfire.prop.Struct;
import org.nightlabs.jfire.prop.StructField;
import org.nightlabs.jfire.prop.dao.StructDAO;
import org.nightlabs.jfire.prop.exception.StructBlockNotFoundException;
import org.nightlabs.jfire.prop.id.StructID;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 */
public class PhoneSystemCallableFieldSection
extends ToolBarSectionPart
{
	private Button addButton;
	private Button removeButton;
	private Button addAllButton;
	private Button removeAllButton;
	
	private StructFieldTable availableStructFieldTable;
	private StructFieldTable callableStructFieldTable;
	
	public PhoneSystemCallableFieldSection(FormPage page, Composite parent) {
		super(
				page, parent,
				ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR,
				Messages.getString("org.nightlabs.jfire.pbx.uihoneSystemCallableFieldSection.title")); //$NON-NLS-1$
		createClient(getSection(), page.getEditor().getToolkit());
	}

	private void createClient(Section section, FormToolkit toolkit) {
		section.setLayoutData(new GridData(GridData.FILL_BOTH));
		section.setText(Messages.getString("org.nightlabs.jfire.pbx.uihoneSystemCallableFieldSection.sectionText")); //$NON-NLS-1$
		
		Composite container = getContainer();//EntityEditorUtil.createCompositeClient(toolkit, section, 1);
		GridLayout gridLayout = new GridLayout(3, false);
		container.setLayout(gridLayout);
		
		availableStructFieldTable = new StructFieldTable(container, SWT.NONE);
		availableStructFieldTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		availableStructFieldTable.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent e) {
				updateButtonStates();
			}
		});
		
		XComposite buttonComposite = new XComposite(container, SWT.NONE);
		buttonComposite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		addButton = new Button(buttonComposite, SWT.NONE);
		addButton.setText(Messages.getString("org.nightlabs.jfire.pbx.uihoneSystemCallableFieldSection.addText")); //$NON-NLS-1$
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		addButton.setLayoutData(gridData);
		addButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addCallableField(false);
				updateButtonStates();
				markDirty();
			}
		});
		
		removeButton = new Button(buttonComposite, SWT.NONE);
		removeButton.setText(Messages.getString("org.nightlabs.jfire.pbx.uihoneSystemCallableFieldSection.removeText")); //$NON-NLS-1$
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		removeButton.setLayoutData(gridData);
		removeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				removeCallableField(false);
				updateButtonStates();
				markDirty();
			}
		});
		
		addAllButton = new Button(buttonComposite, SWT.NONE);
		addAllButton.setText(Messages.getString("org.nightlabs.jfire.pbx.uihoneSystemCallableFieldSection.addAllText")); //$NON-NLS-1$
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		addAllButton.setLayoutData(gridData);
		addAllButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addCallableField(true);
				updateButtonStates();
				markDirty();
			}
		});
		
		removeAllButton = new Button(buttonComposite, SWT.NONE);
		removeAllButton.setText(Messages.getString("org.nightlabs.jfire.pbx.uihoneSystemCallableFieldSection.removeAllText")); //$NON-NLS-1$
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		removeAllButton.setLayoutData(gridData);
		removeAllButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				removeCallableField(true);
				updateButtonStates();
				markDirty();
			}
		});
		
		callableStructFieldTable = new StructFieldTable(container, SWT.NONE);
		callableStructFieldTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		callableStructFieldTable.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent e) {
				updateButtonStates();
			}
		});
	}

	@Override
	public boolean setFormInput(Object input) {
		this.phoneSystem = (PhoneSystem) input;
		createInput(phoneSystem);
		return super.setFormInput(input);
	}

	private void createInput(final PhoneSystem phoneSystem) {
		/*****Load data*****/
		StructID personStructID = 
			StructID.create(Organisation.DEV_ORGANISATION_ID, Person.class, Struct.DEFAULT_SCOPE);
		final Struct personStruct = 
			StructDAO.sharedInstance().getStruct(personStructID, new NullProgressMonitor());
		
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				try {
					List<StructField<? extends DataField>> availableFields = personStruct.getStructBlock(PersonStruct.PHONE).getStructFields();
					availableFields.removeAll(phoneSystem.getCallableStructFields());
					
					availableStructFieldTable.setInput(availableFields);
					callableStructFieldTable.setInput(phoneSystem.getCallableStructFields());
					
					updateButtonStates();
				} catch (StructBlockNotFoundException e) {
					throw new RuntimeException(e);
				}
			}
		});
	}
	
	private void addCallableField(boolean isAll) {
		Collection<StructField> callableFields = callableStructFieldTable.getElements();
		if (isAll) {
			callableFields.addAll(availableStructFieldTable.getElements());
		}
		else {
			callableFields.addAll((Collection<? extends StructField<? extends DataField>>) availableStructFieldTable.getSelectedElements());
		}
		
		Collection<StructField> availFields = availableStructFieldTable.getElements();
		availFields.removeAll(callableFields);
		
		availableStructFieldTable.setInput(availFields);
		callableStructFieldTable.setInput(callableFields);	
	}
	
	private void removeCallableField(boolean isAll) {
		Collection<StructField> availFields = availableStructFieldTable.getElements();
		if (isAll) {
			availFields.addAll(callableStructFieldTable.getElements());
		}
		else {
			availFields.addAll((Collection<? extends StructField<? extends DataField>>) callableStructFieldTable.getSelectedElements());
		}
		
		Collection<StructField> callableFields = callableStructFieldTable.getElements();
		callableFields.removeAll(availFields);
		
		availableStructFieldTable.setInput(availFields);
		callableStructFieldTable.setInput(callableFields);	
	}
	
	private void updateButtonStates() {
		addButton.setEnabled(!availableStructFieldTable.getSelectedElements().isEmpty());
		addAllButton.setEnabled(!availableStructFieldTable.getElements().isEmpty());

		removeButton.setEnabled(!callableStructFieldTable.getSelectedElements().isEmpty());
		removeAllButton.setEnabled(!callableStructFieldTable.getElements().isEmpty());
	}
	
	@Override
	public void refresh() {
		super.refresh();
	}

	@Override
	public void commit(boolean onSave) {
		if (phoneSystem != null) {
			phoneSystem.removeCallableStructFields(availableStructFieldTable.getElements());
			phoneSystem.addCallableStructFields(callableStructFieldTable.getElements());
		}
		super.commit(onSave);
	}

	private PhoneSystem phoneSystem;
	
	public Collection<StructField> getCallableStructFields() {
		return callableStructFieldTable.getSelectedElements();
	}
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