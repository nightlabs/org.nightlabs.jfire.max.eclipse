package org.nightlabs.jfire.issuetracking.ui.issuelink.create;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IWorkbenchPartSite;
import org.nightlabs.base.ui.tree.AbstractTreeComposite;
import org.nightlabs.base.ui.tree.TreeContentProvider;
import org.nightlabs.eclipse.extension.EPProcessorException;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkHandlerCategory;
import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkHandlerFactory;
import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkHandlerFactoryRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 *
 */
public class SelectIssueLinkHandlerFactoryTreeComposite
extends AbstractTreeComposite
implements ISelectionProvider
{
	private static final Logger logger = LoggerFactory.getLogger(SelectIssueLinkHandlerFactoryTreeComposite.class);
	private IWorkbenchPartSite site;

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

	private List<IssueLinkHandlerCategory> categories;
	public List<IssueLinkHandlerCategory> getIssueLinkHandlerCategories() {
		return categories;
	}

	public SelectIssueLinkHandlerFactoryTreeComposite(Composite parent, int style, IWorkbenchPartSite site)
	{
		super(parent, style, true, true, false);
		this.site = site;

		try {
			IssueLinkHandlerFactoryRegistry registry = IssueLinkHandlerFactoryRegistry.sharedInstance();
			categories = registry.getTopLevelCategories();
		} catch (EPProcessorException e) {
			throw new RuntimeException(e);
		}

		getTreeViewer().setInput(categories);

		getTreeViewer().addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent e) {
				Object firstElement = ((TreeSelection)e.getSelection()).getFirstElement();
				issueLinkHandlerCategory = null;
				issueLinkHandlerFactory = null;

				if (firstElement == null) //In case of the check tree, when we click on the arrow, the selection will be null!!!!
					return;

				if (firstElement instanceof IssueLinkHandlerCategory)
					issueLinkHandlerCategory = (IssueLinkHandlerCategory) firstElement;
				else if (firstElement instanceof IssueLinkHandlerFactory)
					issueLinkHandlerFactory = (IssueLinkHandlerFactory<ObjectID, Object>) firstElement;

				ISelection selection = getSelection();
				SelectionChangedEvent selectionChangedEvent =
					new SelectionChangedEvent(SelectIssueLinkHandlerFactoryTreeComposite.this, selection);
				for (Object listener : selectionChangedListeners.getListeners())
					((ISelectionChangedListener)listener).selectionChanged(selectionChangedEvent);
			}
		});
	}

	@Override
	public void createTreeColumns(Tree tree) {
	}

	@Override
	public void setTreeProvider(TreeViewer treeViewer) {
		treeViewer.setContentProvider(new IssueLinkCategoryContentProvider());
		treeViewer.setLabelProvider(new IssueLinkCategoryLabelProvider());
	}

	class IssueLinkCategoryContentProvider extends TreeContentProvider {
		@Override
		public Object getParent(Object childElement) {
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
			if(element instanceof IssueLinkHandlerCategory) {
				IssueLinkHandlerCategory issueLinkHandlerCategory = (IssueLinkHandlerCategory)element;
				return issueLinkHandlerCategory.getImage();
			}

			if(element instanceof IssueLinkHandlerFactory) {
				IssueLinkHandlerFactory issueLinkHandlerFactory = (IssueLinkHandlerFactory)element;
				return issueLinkHandlerFactory.getImage();
			}
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
		getTreeViewer().setSelection(selection);
	}
}