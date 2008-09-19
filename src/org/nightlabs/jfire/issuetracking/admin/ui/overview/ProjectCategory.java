package org.nightlabs.jfire.issuetracking.admin.ui.overview;

import javax.jdo.JDOHelper;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.base.ui.overview.CategoryFactory;
import org.nightlabs.jfire.base.ui.overview.CustomCompositeCategory;
import org.nightlabs.jfire.issue.id.IssueTypeID;
import org.nightlabs.jfire.issue.project.Project;
import org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty.IssueTypeEditor;
import org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty.IssueTypeEditorInput;
import org.nightlabs.jfire.issuetracking.admin.ui.project.ProjectEditor;
import org.nightlabs.jfire.issuetracking.admin.ui.project.ProjectEditorInput;
import org.nightlabs.jfire.issuetracking.ui.project.ProjectTreeComposite;

/**
 * @author Chairat Kongarayawetchakun chairat[at] NightLabs [dot] de
 */
public class ProjectCategory
extends CustomCompositeCategory {
	/**
	 * @param categoryFactory
	 */
	public ProjectCategory(CategoryFactory categoryFactory) {
		super(categoryFactory);
	}
	
	@Override
	public Composite createComposite(Composite composite) {
		XComposite wrapper = new XComposite(composite, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		final ProjectTreeComposite pc = new ProjectTreeComposite(wrapper);
		
		pc.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent e) {
				Project project = pc.getFirstSelectedElement();
				
				try {
					RCPUtil.openEditor(new ProjectEditorInput(project.getObjectId()),
							ProjectEditor.EDITOR_ID);
				} catch (Exception ex) {
					throw new RuntimeException(ex);
				}
			}
		});
		return pc;
	}
}
