package org.nightlabs.jfire.issuetracking.ui.issuelink.create;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.wizard.DynamicPathWizardPage;
import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkHandlerFactory;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;

/**
 *
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 * @author Frederik Loeser <!-- frederik [AT] nightlabs [DOT] de -->
 */
public class SelectIssueLinkHandlerFactoryWizardPage extends DynamicPathWizardPage implements ISelectionProvider {

	private SelectIssueLinkHandlerFactoryTreeComposite linkHandlerFactoryTreeComposite;

	public SelectIssueLinkHandlerFactoryTreeComposite getLinkHandlerFactoryTreeComposite() {
		return linkHandlerFactoryTreeComposite;
	}

	public SelectIssueLinkHandlerFactoryWizardPage() {
		super(
			Messages.getString(
				"org.nightlabs.jfire.issuetracking.ui.issuelink.create.SelectIssueLinkHandlerFactoryWizardPage.title"), //$NON-NLS-1$
			Messages.getString(
				"org.nightlabs.jfire.issuetracking.ui.issuelink.create.SelectIssueLinkHandlerFactoryWizardPage.descriptionDefault")); //$NON-NLS-1$
		setDescription(Messages.getString(
			"org.nightlabs.jfire.issuetracking.ui.issuelink.create.SelectIssueLinkHandlerFactoryWizardPage.description")); //$NON-NLS-1$
	}

	@Override
	public Control createPageContents(Composite parent) {
		XComposite mainComposite = new XComposite(parent, SWT.NONE);
		mainComposite.getGridLayout().numColumns = 1;

		linkHandlerFactoryTreeComposite = new SelectIssueLinkHandlerFactoryTreeComposite(mainComposite,
			mainComposite.getBorderStyle(), null);

		for (Object l : selectionChangedListeners.getListeners())
			linkHandlerFactoryTreeComposite.addSelectionChangedListener((ISelectionChangedListener) l);

		linkHandlerFactoryTreeComposite.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent e) {
				if (e.getSelection() instanceof TreeSelection) {
					Object firstElement = ((TreeSelection) e.getSelection()).getFirstElement();
					if (firstElement instanceof IssueLinkHandlerFactory<?,?>)
						getContainer().showPage(getNextPage());
					else {
						TreeViewer tv = linkHandlerFactoryTreeComposite.getTreeViewer();
						if (tv.getExpandedState(firstElement)) {
							tv.collapseToLevel(firstElement, 1);
						} else {
							tv.expandToLevel(firstElement, 1);
						}
					}
				}
			}
		});
		linkHandlerFactoryTreeComposite.getTreeViewer().expandAll();

		return mainComposite;
	}

	@Override
	public boolean isPageComplete() {
		return linkHandlerFactoryTreeComposite.getIssueLinkHandlerFactory() != null;
	}

	private ListenerList selectionChangedListeners = new ListenerList();

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		selectionChangedListeners.add(listener);
	}

	@Override
	public ISelection getSelection() {
		return linkHandlerFactoryTreeComposite.getSelection();
	}

	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		selectionChangedListeners.remove(listener);
	}

	@Override
	public void setSelection(ISelection selection) {
		linkHandlerFactoryTreeComposite.setSelection(selection);
	}
}
