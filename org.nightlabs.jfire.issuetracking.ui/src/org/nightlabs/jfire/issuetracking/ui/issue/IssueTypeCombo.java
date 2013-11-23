/**
 * 
 */
package org.nightlabs.jfire.issuetracking.ui.issue;

import java.util.List;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComboComposite;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.issue.IssueType;
import org.nightlabs.jfire.issue.dao.IssueTypeDAO;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author abieber
 *
 */
public class IssueTypeCombo extends XComboComposite<IssueType> {

	/**
	 * @param parent
	 * @param comboStyle
	 */
	public IssueTypeCombo(Composite parent, int comboStyle) {
		super(parent, comboStyle);
		init();
	}

	/**
	 * @param parent
	 * @param comboStyle
	 * @param caption
	 */
	public IssueTypeCombo(Composite parent, int comboStyle, String caption) {
		super(parent, comboStyle, caption);
		init();
	}
	
	
	private void init() {
		Job loadJob = new Job("Load IssueTypes") {
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				final List<IssueType> allIssueTypes = IssueTypeDAO.sharedInstance().getAllIssueTypes(new String[] {FetchPlan.DEFAULT, IssueType.FETCH_GROUP_NAME}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
				getDisplay().syncExec(new Runnable() {
					public void run() {
						if (allIssueTypes != null) {
							setInput(allIssueTypes);
							if (allIssueTypes.size() > 0) {
								setSelection(allIssueTypes.get(0));
							}
						}
					}
				});
				return Status.OK_STATUS;
			}
		};
		loadJob.schedule();
		setLabelProvider(new TableLabelProvider() {
			@Override
			public String getColumnText(Object element, int arg1) {
				return ((IssueType) element).getName().getText();
			}
		});
	}
}
