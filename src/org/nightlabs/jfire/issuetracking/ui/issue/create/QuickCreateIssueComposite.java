package org.nightlabs.jfire.issuetracking.ui.issue.create;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issuetracking.ui.department.DepartmentComboComposite;
import org.nightlabs.jfire.issuetracking.ui.project.ProjectComboComposite;
/**
 * A composite that contains UIs for adding {@link Issue}.
 * 
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 */
public class QuickCreateIssueComposite 
extends XComposite
{
	/**
	 * Contructs a composite used for adding {@link Issue}.
	 * 
	 * @param parent -the parent composite
	 * @param style - the SWT style flag 
	 */
	public QuickCreateIssueComposite(Composite parent, int style) {
		super(parent, style, LayoutMode.TIGHT_WRAPPER);

		createComposite();
	}

	private void createComposite() {
		getGridLayout().numColumns = 2;
		getGridLayout().makeColumnsEqualWidth = false;
		getGridData().grabExcessHorizontalSpace = true;
		
		XComposite mainComposite = new XComposite(this, SWT.NONE,
				LayoutMode.TIGHT_WRAPPER);
		
		ProjectComboComposite projectComboComposite = new ProjectComboComposite(mainComposite, SWT.None);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		projectComboComposite.setLayoutData(gridData);
		
		DepartmentComboComposite departmentComboComposite = new DepartmentComboComposite(mainComposite, SWT.None);
		gridData = new GridData(GridData.FILL_BOTH);
		departmentComboComposite.setLayoutData(gridData);
	}
}