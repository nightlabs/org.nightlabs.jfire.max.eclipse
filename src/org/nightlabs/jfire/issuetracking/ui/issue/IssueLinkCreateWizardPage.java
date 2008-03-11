/**
 * 
 */
package org.nightlabs.jfire.issuetracking.ui.issue;

import java.util.List;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.XComboComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.issue.IssueLinkType;
import org.nightlabs.jfire.issue.dao.IssueLinkTypeDAO;
import org.nightlabs.jfire.issuetracking.ui.overview.IssueEntryListFactory;
import org.nightlabs.jfire.issuetracking.ui.overview.IssueEntryListViewer;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun - chairat at nightlabs dot de
 *
 */
public class IssueLinkCreateWizardPage 
extends WizardHopPage 
{
	private Button createNewCheckBox;
	private Button selectFromCheckBox;
	
	private ObjectID linkedObjectID;
	
	public IssueLinkCreateWizardPage(ObjectID linkedObjectID) {
		super("Create link to issue", "Create link to issue");
		this.linkedObjectID = linkedObjectID;
	}

	@Override
	public Control createPageContents(Composite parent) {
		XComposite wrapper = new XComposite(parent, SWT.NONE);
		XComposite comp = new XComposite(wrapper, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA_HORIZONTAL);
		
		XComposite c = new XComposite(comp, SWT.NONE);
		c.getGridLayout().numColumns = 2;
		new Label(c, SWT.NONE).setText("Issue link types: ");
		final XComboComposite<IssueLinkType> issueLinkTypeCombo = new XComboComposite<IssueLinkType>(c, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessVerticalSpace = true;
		issueLinkTypeCombo.setLayoutData(gridData);
		issueLinkTypeCombo.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof IssueLinkType) {
					IssueLinkType issueLinkType = (IssueLinkType) element;
					return issueLinkType.getName().getText();
				}
				return "";
			}
		});

		issueLinkTypeCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Display.getCurrent().asyncExec(new Runnable() {
			@Override
			public void run() {
				List<IssueLinkType> issueLinkTypes = IssueLinkTypeDAO.sharedInstance().getIssueLinkTypes(new NullProgressMonitor());
				issueLinkTypeCombo.setInput(issueLinkTypes);
				issueLinkTypeCombo.setSelection(0);
			}
		});

		createNewCheckBox = new Button(comp, SWT.RADIO);		
		createNewCheckBox.setText("Create new issue");
		createNewCheckBox.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		selectFromCheckBox = new Button(comp, SWT.RADIO);
		selectFromCheckBox.setText("Select issue");
		selectFromCheckBox.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		createNewCheckBox.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setCreateNewIssue(true, true);
			}
		});
		
		selectFromCheckBox.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setCreateNewIssue(false, true);
			}
		});
		
		IssueEntryListViewer issueEntryViewer = new IssueEntryListViewer(new IssueEntryListFactory().createEntry()) {
			@Override
			protected void addResultTableListeners(AbstractTableComposite tableComposite) {
//				tableComposite.getTableViewer().addDoubleClickListener(new IDoubleClickListener() {
//					@Override
//					public void doubleClick(DoubleClickEvent evt) {
////						notifyIssueLinkDoubleClickListeners();
//					}
//				});
//				
//				tableComposite.addSelectionChangedListener(new ISelectionChangedListener() {
//					public void selectionChanged(SelectionChangedEvent e) {
////						notifyIssueLinkSelectionListeners();
//					}
//				});
			}
		};
		
		Composite issueEntryViewerComposite = issueEntryViewer.createComposite(comp);
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessVerticalSpace = true;
		
		issueEntryViewerComposite.setLayoutData(gridData);
		
		setCreateNewIssue(false, false);
		return wrapper;
	}
	
	@Override
	public boolean isPageComplete() {
		return false;//issueLinkAdder.isComplete(); 
	}
	
	public void setCreateNewIssue(boolean b, boolean updateButtons) {
		createNewCheckBox.setSelection(b);
		selectFromCheckBox.setSelection(!b);
		if (b) {
//			getWizardHop().addHopPage(createPage);
		} else {
//			getWizardHop().removeAllHopPages();
		}
		if (updateButtons)
			getContainer().updateButtons();
	}
}