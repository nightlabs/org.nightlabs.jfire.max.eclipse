//package org.nightlabs.jfire.issuetracking.ui.issue;
//
//import org.eclipse.jface.viewers.DoubleClickEvent;
//import org.eclipse.jface.viewers.IDoubleClickListener;
//import org.eclipse.jface.viewers.ISelectionChangedListener;
//import org.eclipse.jface.viewers.SelectionChangedEvent;
//import org.eclipse.jface.wizard.WizardDialog;
//import org.eclipse.swt.SWT;
//import org.eclipse.swt.widgets.Composite;
//import org.eclipse.swt.widgets.Control;
//import org.nightlabs.base.ui.composite.XComposite;
//import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
//import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
//import org.nightlabs.base.ui.resource.SharedImages;
//import org.nightlabs.base.ui.wizard.WizardHopPage;
//import org.nightlabs.jfire.issuetracking.ui.IssueTrackingPlugin;
//import org.nightlabs.jfire.issuetracking.ui.resource.Messages;
//
///**
// * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
// */
//public class SelectIssueWizardPage
//extends WizardHopPage
//{
////	private Composite issueEntryListViewerComposite;
////	private IssueEntryListViewer issueEntryListViewer;
//
////	private Collection<Issue> selectedIssues;
//
//	public SelectIssueWizardPage() {
//		super(SelectIssueWizardPage.class.getName(), Messages.getString("org.nightlabs.jfire.issuetracking.ui.issuelink.attach.SelectIssueWizardPage.titleDefault"), SharedImages.getWizardPageImageDescriptor(IssueTrackingPlugin.getDefault(), SelectIssueWizardPage.class)); //$NON-NLS-1$
//
//		setTitle(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issuelink.attach.SelectIssueWizardPage.title")); //$NON-NLS-1$
//		setDescription(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issuelink.attach.SelectIssueWizardPage.description")); //$NON-NLS-1$
//
////		selectedIssues = new HashSet<Issue>();
//	}
//
//	private IssueSearchComposite issueSearchComposite;
//	@Override
//	public Control createPageContents(Composite parent) {
//		XComposite mainComposite = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA);
//		mainComposite.getGridLayout().numColumns = 1;
//
//		issueSearchComposite = new IssueSearchComposite(mainComposite, SWT.NONE);
//		issueSearchComposite.getIssueEntryListViewer().getResultTable().addDoubleClickListener(new IDoubleClickListener() {
//			@Override
//			public void doubleClick(DoubleClickEvent evt) {
//				// Do nothing!!!
//				// --> Or maybe we can already react for 'Finish' on the double-click (which will standardise with the rest)? Kai.
//				issueDoubleClick();
//			}
//		});
//
//		issueSearchComposite.getIssueEntryListViewer().getResultTable().addSelectionChangedListener(new ISelectionChangedListener() {
//			public void selectionChanged(SelectionChangedEvent e) {
//				getContainer().updateButtons();
//			}
//		});
//
////		issueEntryListViewer = new IssueEntryListViewer(new IssueEntryListFactory().createEntry()) {
////			@Override
////			protected void addResultTableListeners(AbstractTableComposite<Issue> tableComposite) {
////				tableComposite.addDoubleClickListener(new IDoubleClickListener() {
////					@Override
////					public void doubleClick(DoubleClickEvent evt) {
////						// Do nothing!!!
////						// --> Or maybe we can already react for 'Finish' on the double-click (which will standardise with the rest)? Kai.
////						issueDoubleClick();
////					}
////				});
////
////				tableComposite.addSelectionChangedListener(new ISelectionChangedListener() {
////					public void selectionChanged(SelectionChangedEvent e) {
////						selectedIssues = issueEntryListViewer.getIssueTable().getSelectedElements();
////						getContainer().updateButtons();
////					}
////				});
////			}
////		};
////
////		issueEntryListViewerComposite = issueEntryListViewer.createComposite(mainComposite);
////		GridData gridData = new GridData(GridData.FILL_BOTH);
////		issueEntryListViewerComposite.setLayoutData(gridData);
////
////		Display.getDefault().asyncExec(new Runnable() {
////			public void run() {
////				getShell().layout(true, true);
////				issueEntryListViewer.search();
////			}
////		});
////
//		issueSearchComposite.getIssueEntryListViewer().getResultTable().setIsTableInWizard(true);
//
//		return mainComposite;
//	}
//
//	@Override
//	public boolean isPageComplete() {
//		if (issueSearchComposite.getSelectedIssues().size() > 0)
//			return true;
//		return false;
//	}
//
//	/**
//	 * React on the double-click event on the selected Issue from the table,
//	 */
//	protected void issueDoubleClick() {
//		if (getContainer() instanceof WizardDialog) {
//			if (isPageComplete() && getWizard().performFinish()) {
//				((WizardDialog) getContainer()).close();
//			}
//		}
//	}
//
//	@Override
//	public boolean canFlipToNextPage() {
//		return false;
//	}
//
//	@Override
//	public boolean canBeLastPage() {
//		return true;
//	}
//}