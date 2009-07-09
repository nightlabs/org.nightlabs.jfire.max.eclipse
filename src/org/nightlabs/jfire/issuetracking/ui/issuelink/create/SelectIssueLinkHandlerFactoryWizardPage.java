package org.nightlabs.jfire.issuetracking.ui.issuelink.create;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.extensionpoint.EPProcessorException;
import org.nightlabs.base.ui.tree.TreeContentProvider;
import org.nightlabs.base.ui.wizard.DynamicPathWizardPage;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkHandlerCategory;
import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkHandlerFactory;
import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkHandlerFactoryRegistry;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class SelectIssueLinkHandlerFactoryWizardPage
extends DynamicPathWizardPage
implements ISelectionProvider
{
	private TreeViewer treeViewer;

	/**
	 * The currently selected category or <code>null</code>. If a category is selected, the {@link #issueLinkHandlerFactory} is <code>null</code>,
	 * if a factory is selected, this field is <code>null</code>. If nothing is selected, both fields are <code>null</code>.
	 */
	private IssueLinkHandlerCategory issueLinkHandlerCategory;

	public IssueLinkHandlerCategory getIssueLinkHandlerCategory() {
		return issueLinkHandlerCategory;
	}

	/**
	 * The currently selected factory or <code>null</code>.
	 * @see #issueLinkHandlerCategory
	 */
	private IssueLinkHandlerFactory<ObjectID, Object> issueLinkHandlerFactory;

	public IssueLinkHandlerFactory<ObjectID, Object> getIssueLinkHandlerFactory() {
		return issueLinkHandlerFactory;
	}

	public SelectIssueLinkHandlerFactoryWizardPage() {
		super(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issuelink.create.SelectIssueLinkHandlerFactoryWizardPage.title"), Messages.getString("org.nightlabs.jfire.issuetracking.ui.issuelink.create.SelectIssueLinkHandlerFactoryWizardPage.descriptionDefault")); //$NON-NLS-1$ //$NON-NLS-2$
		setDescription(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issuelink.create.SelectIssueLinkHandlerFactoryWizardPage.description")); //$NON-NLS-1$
	}

	@Override
	public Control createPageContents(Composite parent) {
		XComposite mainComposite = new XComposite(parent, SWT.NONE);
		mainComposite.getGridLayout().numColumns = 1;

		treeViewer = new TreeViewer(mainComposite, mainComposite.getBorderStyle());
		treeViewer.setContentProvider(new IssueLinkCategoryContentProvider());
		treeViewer.setLabelProvider(new IssueLinkCategoryLabelProvider());
		treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent e) {
				Object firstElement = ((TreeSelection)e.getSelection()).getFirstElement();
				if (firstElement instanceof IssueLinkHandlerFactory)
					getContainer().showPage(getNextPage());
				else
					treeViewer.expandToLevel(firstElement, 1);
			}
		});

		List<IssueLinkHandlerCategory> categoryItems = null;
		try {
			IssueLinkHandlerFactoryRegistry registry = IssueLinkHandlerFactoryRegistry.sharedInstance();
			categoryItems = registry.getTopLevelCategories();
		} catch (EPProcessorException e) {
			throw new RuntimeException(e);
		}

		treeViewer.setInput(categoryItems);

		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;

		treeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));

		treeViewer.addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent e) {
				Object firstElement = ((TreeSelection)e.getSelection()).getFirstElement();
				issueLinkHandlerCategory = null;
				issueLinkHandlerFactory = null;

				if (firstElement instanceof IssueLinkHandlerCategory)
					issueLinkHandlerCategory = (IssueLinkHandlerCategory) firstElement;
				else if (firstElement instanceof IssueLinkHandlerFactory)
					issueLinkHandlerFactory = (IssueLinkHandlerFactory<ObjectID, Object>) firstElement;

				getContainer().updateButtons();

				SelectionChangedEvent selectionChangedEvent = new SelectionChangedEvent(SelectIssueLinkHandlerFactoryWizardPage.this, getSelection());
				for (Object listener : selectionChangedListeners.getListeners())
					((ISelectionChangedListener)listener).selectionChanged(selectionChangedEvent);
			}
		});

		treeViewer.expandAll();	// Since, so far, we dont have that many items for linking; and they can all be comforably displayed in the tree. Kai.
		return mainComposite;
	}

//	private void setLinkClass(Object factory) {
//		getWizardHop().removeAllHopPages();
//		if(factory instanceof IssueLinkHandlerFactory) {
//			IssueLinkHandlerFactory iFactory = (IssueLinkHandlerFactory)factory;
//
//			IssueLinkAdder adder = iFactory.createIssueLinkAdder();
//
//			WizardHopPage page = createAdderWizardPage(adder);
//			page.setTitle(iFactory.getLinkedObjectClass().getSimpleName());
//			page.setDescription("Select the " + iFactory.getLinkedObjectClass().getSimpleName() + "(s) to link to the issue.");
//			createIssueLinkWizard.setLinkedClass(iFactory.getLinkedObjectClass());
//			getWizardHop().addHopPage(page);
//		}
//
//		getContainer().updateButtons();
//	}

//	private SelectLinkedObjectPage selectLinkedObjectPage;
//	private WizardHopPage createAdderWizardPage(IssueLinkAdder issueLinkAdder){
//		selectLinkedObjectPage = new SelectLinkedObjectPage(createIssueLinkWizard, issueLinkAdder);
//		return selectLinkedObjectPage;
//	}

	@Override
	public boolean isPageComplete() {
		return issueLinkHandlerFactory != null;
//		return getWizardHop().getHopPages() != null && getWizardHop().getHopPages().size() != 0;
	}

	class IssueLinkCategoryContentProvider extends TreeContentProvider {
		@Override
		public Object getParent(Object childElement) {
			// this method is not used since we don't use a DrillDownAdapter
//			if(childElement instanceof IssueLinkHandlerCategory) {
//				IssueLinkHandlerCategory issueLinkHandlerCategory = (IssueLinkHandlerCategory)childElement;
//				return issueLinkHandlerCategory.getParent();
//			}
//			if(childElement instanceof IssueLinkHandlerFactory) {
//				IssueLinkHandlerFactory issueLinkHandlerFactory = (IssueLinkHandlerFactory)childElement;
//				return issueLinkHandlerFactory.getCategoryId(); // WRONG. Needs to return the Category (i.e. the object in the tree) and not its ID.
//			}
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			if(element instanceof IssueLinkHandlerCategory) {
				IssueLinkHandlerCategory issueLinkHandlerCategory = (IssueLinkHandlerCategory)element;
				return issueLinkHandlerCategory.getChildFactories().size() > 0 || issueLinkHandlerCategory.getChildCategories().size() > 0;
			}

			return false;
		}

		public Object[] getElements(final Object inputElement) {
			if (inputElement instanceof Collection) {
				return ((Collection)inputElement).toArray();
			}

			return new Object[] { inputElement };
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			if(parentElement instanceof IssueLinkHandlerCategory) {
				IssueLinkHandlerCategory issueLinkHandlerCategory = (IssueLinkHandlerCategory)parentElement;
				return issueLinkHandlerCategory.getChildObjects().toArray(new Object[0]);
			}
			return null;
		}
	}

	class IssueLinkCategoryLabelProvider extends LabelProvider {
		@Override
		public Image getImage(Object element) {
			return null;
		}

		@Override
		public String getText(Object element) {
			if(element instanceof IssueLinkHandlerCategory) {
				IssueLinkHandlerCategory issueLinkHandlerCategory = (IssueLinkHandlerCategory)element;
				return issueLinkHandlerCategory.getName();
			}

			if(element instanceof IssueLinkHandlerFactory) {
				IssueLinkHandlerFactory issueLinkHandlerFactory = (IssueLinkHandlerFactory)element;
				return issueLinkHandlerFactory.getName();
			}

			return String.valueOf(element);
		}
	}

	private ListenerList selectionChangedListeners = new ListenerList();

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		selectionChangedListeners.add(listener);
	}

	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		selectionChangedListeners.remove(listener);
	}

	@Override
	public ISelection getSelection() {
		if (issueLinkHandlerFactory != null)
			return new StructuredSelection(issueLinkHandlerFactory);

		return new StructuredSelection(issueLinkHandlerCategory);
	}

	@Override
	public void setSelection(ISelection selection) {
		treeViewer.setSelection(selection);
	}
}
