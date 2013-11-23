/**
 * 
 */
package org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.property.prop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jfire.base.ui.prop.structedit.StructEditorUtil;
import org.nightlabs.jfire.prop.Struct;
import org.nightlabs.jfire.prop.StructLocal;
import org.nightlabs.jfire.prop.dao.StructLocalDAO;
import org.nightlabs.jfire.prop.id.StructLocalID;
import org.nightlabs.jfire.reporting.admin.ui.resource.Messages;
import org.nightlabs.jfire.reporting.oda.jfs.JFSQueryPropertySet;
import org.nightlabs.jfire.reporting.scripting.javaclass.prop.PropertySet;
import org.nightlabs.jfire.security.SecurityReflector;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class StructLocalTableComposite extends AbstractTableComposite<StructLocal> {

	private List<StructLocal> structLocals;
	private JFSQueryPropertySet queryPropertySet;
	
	public static final String[] FETCH_GROUPS_STRUCT_LOCAL = {
		FetchPlan.DEFAULT,
		StructLocal.FETCH_GROUP_NAME
	};
	
	/**
	 * @param parent
	 * @param style
	 */
	public StructLocalTableComposite(Composite parent, int style) {
		super(parent, style);
		setLoadingMessage(Messages.getString("org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.property.prop.StructLocalTableComposite.loadingMessage")); //$NON-NLS-1$
		Job loadJob = new Job(Messages.getString("org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.property.prop.StructLocalTableComposite.job.loadStructLocal")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				Collection<StructLocalID> structLocalIDs = StructEditorUtil.getAvailableStructLocalIDs();
				List<StructLocal> _structLocals = new ArrayList<StructLocal>(structLocalIDs.size());
				for (StructLocalID structLocalID : structLocalIDs) {
					_structLocals.add(
							StructLocalDAO.sharedInstance().getStructLocal(structLocalID, FETCH_GROUPS_STRUCT_LOCAL, monitor));
				}
				synchronized (this) {
					structLocals = _structLocals;
					getDisplay().asyncExec(new Runnable() {
						public void run() {
							setInput(structLocals);
						}
					});
				}
				
				if (queryPropertySet != null)
					setSelection();
				
				return Status.OK_STATUS;
			}
		};
		loadJob.schedule();
	}
	
	private void setSelection() {
		if (queryPropertySet != null) {
			String linkClass = queryPropertySet.getProperties().get(PropertySet.PROPERTY_NAME_LINK_CLASS);
			if (linkClass == null || "".equals(linkClass)) //$NON-NLS-1$
				return;
			String structScope = queryPropertySet.getProperties().get(PropertySet.PROPERTY_NAME_STRUCT_SCOPE);
			if (structScope == null || "".equals(structScope)) //$NON-NLS-1$
				structScope = Struct.DEFAULT_SCOPE;
			String structLocalScope = queryPropertySet.getProperties().get(PropertySet.PROPERTY_NAME_STRUCT_LOCAL_SCOPE);
			if (structLocalScope == null || "".equals(structLocalScope)) //$NON-NLS-1$
				structLocalScope = StructLocal.DEFAULT_SCOPE;
			StructLocalID structLocalID = StructLocalID.create(
					SecurityReflector.getUserDescriptor().getOrganisationID(),
					linkClass, structScope, structLocalScope);
			for (final StructLocal structLocal : new HashSet<StructLocal>(structLocals)) {
				if (JDOHelper.getObjectId(structLocal).equals(structLocalID)) {
					getDisplay().asyncExec(new Runnable() {
						public void run() {
							getTableViewer().setSelection(new StructuredSelection(structLocal), true);
						}
					});
					return;
				}
			}
		}
	}
	
	public void setJFSQueryPropertySet(JFSQueryPropertySet queryPropertySet) {
		synchronized (this) {
			this.queryPropertySet = queryPropertySet;
			if (structLocals == null) {
				return;
			}
		}
		setSelection();
	}

	public Map<String, String> getProperties() {
		Map<String, String> result = new HashMap<String, String>();
		StructLocal structLocal = getFirstSelectedElement();
		if (structLocal != null) {
			StructLocalID structLocalID = (StructLocalID) JDOHelper.getObjectId(structLocal);
			result.put(PropertySet.PROPERTY_NAME_LINK_CLASS, structLocalID.linkClass);
			result.put(PropertySet.PROPERTY_NAME_STRUCT_SCOPE, structLocalID.structScope);
			result.put(PropertySet.PROPERTY_NAME_STRUCT_LOCAL_SCOPE, structLocalID.structLocalScope);
		}
		return result;
	}
	
	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.table.AbstractTableComposite#createTableColumns(org.eclipse.jface.viewers.TableViewer, org.eclipse.swt.widgets.Table)
	 */
	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) {
		TableColumn col = new TableColumn(table, SWT.LEFT);
		col.setText(Messages.getString("org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui.property.prop.StructLocalTableComposite.column.structLocal")); //$NON-NLS-1$
		table.setLayout(new WeightedTableLayout(new int[]{1}));
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.table.AbstractTableComposite#setTableProvider(org.eclipse.jface.viewers.TableViewer)
	 */
	@Override
	protected void setTableProvider(TableViewer tableViewer) {
		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setLabelProvider(new TableLabelProvider() {
			@Override
			public String getColumnText(Object element, int columnIndex) {
				if (element instanceof StructLocal) {
					if (columnIndex == 0)
						return ((StructLocal) element).getName().getText();
				}
				return ""; //$NON-NLS-1$
			}
		});
	}

}
