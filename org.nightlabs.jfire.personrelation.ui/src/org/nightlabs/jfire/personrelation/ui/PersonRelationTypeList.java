package org.nightlabs.jfire.personrelation.ui;

import java.util.Collections;
import java.util.List;

import javax.jdo.FetchGroup;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.personrelation.PersonRelationType;
import org.nightlabs.jfire.personrelation.dao.PersonRelationTypeDAO;
import org.nightlabs.jfire.personrelation.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;

public class PersonRelationTypeList
extends AbstractTableComposite<PersonRelationType>
{
	private static final String[] FETCH_GROUPS_PERSON_RELATION_TYPE = {
		FetchGroup.DEFAULT,
		PersonRelationType.FETCH_GROUP_NAME,
		PersonRelationType.FETCH_GROUP_DESCRIPTION,
		PersonRelationType.FETCH_GROUP_ICON16x16DATA
	};

	public PersonRelationTypeList(Composite parent) {
		super(parent, SWT.NONE);

		setLoadingMessage(Messages.getString("org.nightlabs.jfire.personrelation.ui.PersonRelationTypeList.loadingMessage")); //$NON-NLS-1$

		final Display display = getDisplay();
		Job job = new Job(Messages.getString("org.nightlabs.jfire.personrelation.ui.PersonRelationTypeList.job.loadingPersonRelationTypes.name")) //$NON-NLS-1$
		{
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception
			{
				final List<PersonRelationType> personRelationTypes = PersonRelationTypeDAO.sharedInstance().getPersonRelationTypes(
						FETCH_GROUPS_PERSON_RELATION_TYPE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor
				);

				display.asyncExec(new Runnable() {
					public void run() {
						if (isDisposed())
							return;

						setInput(personRelationTypes);
						if (!personRelationTypes.isEmpty())
							setSelectedElements(Collections.singleton(personRelationTypes.iterator().next()));
					}
				});

				return Status.OK_STATUS;
			}
		};
		job.setPriority(Job.INTERACTIVE);
		job.schedule();
	}

	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) {
		TableColumn tc;

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.jfire.personrelation.ui.PersonRelationTypeList.table.column.name.text")); //$NON-NLS-1$

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.jfire.personrelation.ui.PersonRelationTypeList.table.column.description.text")); //$NON-NLS-1$

		table.setLayout(
				new WeightedTableLayout(
						new int[] {30, 70}
				)
		);
	}

	@Override
	protected void setTableProvider(TableViewer tableViewer) {
		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setLabelProvider(new TableLabelProvider() {
			@Override
			public String getColumnText(Object element, int colIdx) {
				switch (colIdx) {
					case 0:
						if (element instanceof PersonRelationType)
							return ((PersonRelationType)element).getName().getText();

						// TODO delegate
						return null;

					case 1:
						if (element instanceof PersonRelationType)
							return ((PersonRelationType)element).getDescription().getText();

						// TODO delegate
						return null;
					default:
						return null;
				}
			}
			
			@Override
			public Image getColumnImage(Object element, int columnIndex) {
				if (columnIndex == 0 && element instanceof PersonRelationType) {
					return PersonRelationPlugin.getDefault().getPersonRelationTypeIcon((PersonRelationType) element);
				}
				return super.getColumnImage(element, columnIndex);
			}
		});
	}

}
