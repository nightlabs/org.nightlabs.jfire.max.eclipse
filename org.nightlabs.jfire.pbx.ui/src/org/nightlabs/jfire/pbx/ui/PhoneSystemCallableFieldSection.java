package org.nightlabs.jfire.pbx.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
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
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.jfire.organisation.Organisation;
import org.nightlabs.jfire.pbx.PhoneSystem;
import org.nightlabs.jfire.pbx.ui.resource.Messages;
import org.nightlabs.jfire.person.Person;
import org.nightlabs.jfire.prop.Struct;
import org.nightlabs.jfire.prop.StructBlock;
import org.nightlabs.jfire.prop.StructField;
import org.nightlabs.jfire.prop.StructLocal;
import org.nightlabs.jfire.prop.dao.StructLocalDAO;
import org.nightlabs.jfire.prop.id.StructLocalID;
import org.nightlabs.jfire.prop.structfield.PhoneNumberStructField;
import org.nightlabs.progress.ProgressMonitor;

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
				Messages.getString("org.nightlabs.jfire.pbx.ui.PhoneSystemCallableFieldSection.title")); //$NON-NLS-1$
		createClient(getSection(), page.getEditor().getToolkit());
	}

	private void createClient(Section section, FormToolkit toolkit) {
		section.setLayoutData(new GridData(GridData.FILL_BOTH));
		section.setText(Messages.getString("org.nightlabs.jfire.pbx.ui.PhoneSystemCallableFieldSection.sectionText")); //$NON-NLS-1$

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
		addButton.setText(Messages.getString("org.nightlabs.jfire.pbx.ui.PhoneSystemCallableFieldSection.addText")); //$NON-NLS-1$
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
		removeButton.setText(Messages.getString("org.nightlabs.jfire.pbx.ui.PhoneSystemCallableFieldSection.removeText")); //$NON-NLS-1$
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
		addAllButton.setText(Messages.getString("org.nightlabs.jfire.pbx.ui.PhoneSystemCallableFieldSection.addAllText")); //$NON-NLS-1$
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
		removeAllButton.setText(Messages.getString("org.nightlabs.jfire.pbx.ui.PhoneSystemCallableFieldSection.removeAllText")); //$NON-NLS-1$
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
		if (Display.getCurrent() == null)
			throw new IllegalStateException("Thread mismatch! This method must always be called on the SWT UI thread!!!");

		this.phoneSystem = (PhoneSystem) input;
//		createInput(phoneSystem); // WRONG here - must be in refresh!
		return super.setFormInput(input);
	}

//	private void createInput(final PhoneSystem phoneSystem) {
//		/*****Load data*****/
//		StructLocalID personStructID =
//			StructLocalID.create(Organisation.DEV_ORGANISATION_ID, Person.class, Struct.DEFAULT_SCOPE, StructLocal.DEFAULT_SCOPE);
//		final StructLocal personStruct =
//			StructLocalDAO.sharedInstance().getStructLocal(personStructID, new NullProgressMonitor()); // TODO this should be done on a Job thread!!! It is blocking the UI!!!!!
//
//		Display.getDefault().asyncExec(new Runnable() {
//			@Override
//			public void run() {
//				// @Chairat: This is nonsense. The whole point of the configuration is the not use any constant and thus be able to use *every*
//				// PhoneNumberDataField - not only the predefined ones.
//				//					List<StructField<? extends DataField>> availableFields = personStruct.getStructBlock(PersonStruct.PHONE).getStructFields();
//				List<PhoneNumberStructField> availableFields = new ArrayList<PhoneNumberStructField>();
//				for (StructBlock structBlock : personStruct.getStructBlocks()) {
//					for (StructField<?> structField : structBlock.getStructFields()) {
//						if (structField instanceof PhoneNumberStructField)
//							availableFields.add((PhoneNumberStructField) structField);
//					}
//				}
//
//				availableFields.removeAll(phoneSystem.getCallableStructFields());
//
//				availableStructFieldTable.setInput(availableFields);
//				callableStructFieldTable.setInput(phoneSystem.getCallableStructFields());
//
//				updateButtonStates();
//			}
//		});
//	}

	private void addCallableField(boolean isAll) {
		Collection<StructField> callableFields = callableStructFieldTable.getElements();
		if (isAll) {
			callableFields.addAll(availableStructFieldTable.getElements());
		}
		else {
			callableFields.addAll(availableStructFieldTable.getSelectedElements());
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
			availFields.addAll(callableStructFieldTable.getSelectedElements());
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

	private Job loadPersonStructJob;

	@Override
	public void refresh() {
		final Section section = getSection();
		final Display display = section.getDisplay();
		if (Display.getCurrent() != display)
			throw new IllegalStateException("Thread mismatch! This method must always be called on the SWT UI thread!!!");

		availableStructFieldTable.setLoadingMessage("Loading...");
		callableStructFieldTable.setLoadingMessage("Loading...");

		Job job = new Job("Loading person structure") {
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				StructLocalID personStructID = StructLocalID.create(
						Organisation.DEV_ORGANISATION_ID, Person.class, Struct.DEFAULT_SCOPE, StructLocal.DEFAULT_SCOPE
				);
				final StructLocal personStruct = StructLocalDAO.sharedInstance().getStructLocal(personStructID, monitor);

				// @Chairat: This is nonsense. The whole point of the configuration is the not use any constant and thus be able to use *every*
				// PhoneNumberDataField - not only the predefined ones.
				//					List<StructField<? extends DataField>> availableFields = personStruct.getStructBlock(PersonStruct.PHONE).getStructFields();
				final List<PhoneNumberStructField> availableFields = new ArrayList<PhoneNumberStructField>();
				for (StructBlock structBlock : personStruct.getStructBlocks()) {
					for (StructField<?> structField : structBlock.getStructFields()) {
						if (structField instanceof PhoneNumberStructField)
							availableFields.add((PhoneNumberStructField) structField);
					}
				}

				final Job thisJob = this;
				display.asyncExec(new Runnable() {
					@Override
					public void run() {
						if (thisJob != loadPersonStructJob)
							return;

						if (phoneSystem == null) {
							availableStructFieldTable.setInput(null);
							callableStructFieldTable.setInput(null);
							return;
						}

						section.setEnabled(true);

						availableFields.removeAll(phoneSystem.getCallableStructFields());

						availableStructFieldTable.setInput(availableFields);
						callableStructFieldTable.setInput(phoneSystem.getCallableStructFields());

						updateButtonStates();
					}
				});

				return Status.OK_STATUS;
			}
		};
		job.setPriority(Job.INTERACTIVE);
		loadPersonStructJob = job;

		section.setEnabled(false);
		job.schedule();

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