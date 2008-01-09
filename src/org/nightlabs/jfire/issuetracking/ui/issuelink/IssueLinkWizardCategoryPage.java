package org.nightlabs.jfire.issuetracking.ui.issuelink;

import java.util.List;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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
import org.nightlabs.base.ui.wizard.WizardHop;
import org.nightlabs.base.ui.wizard.WizardHopPage;

public class IssueLinkWizardCategoryPage 
extends WizardHopPage
{
	private IssueLinkWizard iWizard;
	
	public IssueLinkWizardCategoryPage(IssueLinkWizard iWizard) {
		super("Select an Object", "Select an object to link this issue with.");
		setDescription("Link an object to the issue.");
		this.iWizard = iWizard;
		
		new WizardHop(this);
	}

	@Override
	public Control createPageContents(Composite parent) {
		XComposite mainComposite = new XComposite(parent, SWT.NONE);
		mainComposite.getGridLayout().numColumns = 1;

		TreeViewer categoryTreeViewer = new TreeViewer(mainComposite, SWT.NONE);
		categoryTreeViewer.setContentProvider(new IssueLinkCategoryContentProvider());
		categoryTreeViewer.setLabelProvider(new IssueLinkCategoryLabelProvider());
		
		List<IssueLinkHandlerCategory> categoryItems = null;
		try {
			IssueLinkHandlerFactoryRegistry registry = IssueLinkHandlerFactoryRegistry.sharedInstance();
			categoryItems = registry.getTopLevelCategories();
		} catch (EPProcessorException e) {
			throw new RuntimeException(e);
		}
		
		categoryTreeViewer.setInput(categoryItems);

		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		
		categoryTreeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		
		categoryTreeViewer.addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent e) {
				setLinkClass(((TreeSelection)e.getSelection()).getFirstElement());
			}
		});
		
		return mainComposite;
	}
	
	private void setLinkClass(Object factory) {
		getWizardHop().removeAllHopPages();
		if(factory instanceof IssueLinkHandlerFactory) {
			IssueLinkHandlerFactory iFactory = (IssueLinkHandlerFactory)factory;

			IssueLinkAdder adder = iFactory.createIssueLinkAdder();
			
			WizardHopPage page = createAdderWizardPage(adder);
			getWizardHop().addHopPage(page);
		}
		
		getContainer().updateButtons();
	}

	private IssueLinkWizardListPage issueLinkWizardListPage;
	private WizardHopPage createAdderWizardPage(IssueLinkAdder adder){
		issueLinkWizardListPage = new IssueLinkWizardListPage(iWizard, adder);
		return issueLinkWizardListPage;
	}
	
	@Override
	public boolean isPageComplete() {
		return getWizardHop().getHopPages() != null && getWizardHop().getHopPages().size() != 0;
	}
	
	/**
	 * TODO
	 */
	class IssueLinkCategoryContentProvider extends TreeContentProvider {
		public Object getParent(Object childElement) {
			if(childElement instanceof IssueLinkHandlerCategory) {
				IssueLinkHandlerCategory issueLinkHandlerCategory = (IssueLinkHandlerCategory)childElement;
				return issueLinkHandlerCategory.getParent();
			}
//			if(childElement instanceof IssueLinkHandlerFactory) {
//				IssueLinkHandlerFactory issueLinkHandlerFactory = (IssueLinkHandlerFactory)childElement;
//				return issueLinkHandlerFactory.getCategoryId();
//			}
			return null;
		}

		public boolean hasChildren(Object element) {
			boolean result = false;
			if(element instanceof IssueLinkHandlerCategory) {
				IssueLinkHandlerCategory issueLinkHandlerCategory = (IssueLinkHandlerCategory)element;
				result = issueLinkHandlerCategory.getChildFactories().size() > 0 || issueLinkHandlerCategory.getChildCategories().size() > 0;
			}

			return result;
		}

		public Object[] getElements(final Object inputElement) {
			if (inputElement instanceof List) {
				return ((List)inputElement).toArray();
			}
			
			return new String[] {""}; 
		}
		
		@Override
		public Object[] getChildren(Object parentElement) {
			if(parentElement instanceof IssueLinkHandlerCategory) {
				IssueLinkHandlerCategory issueLinkHandlerCategory = (IssueLinkHandlerCategory)parentElement;
				return (Object[])issueLinkHandlerCategory.getChildObjects().toArray(new Object[0]);
			}
			return null;
		}
	}
	
	class IssueLinkCategoryLabelProvider extends LabelProvider {
		@Override
		public Image getImage(Object element) {
			Image image = null;
//			if(element instanceof){
//				ImageDescriptor descriptor = node.getImageDescriptor();
//
//				//obtain the cached image corresponding to the descriptor
//				image = imageCache.get(descriptor);
//				if (image == null && descriptor != null) {
//					image = descriptor.createImage();
//					imageCache.put(descriptor, image);
//				}
//			}//if
			return image;
		}
		
		@Override
		public String getText(Object element) {
			String result = null;
			
			if(element instanceof IssueLinkHandlerCategory) {
				IssueLinkHandlerCategory issueLinkHandlerCategory = (IssueLinkHandlerCategory)element;
				result = issueLinkHandlerCategory.getName();
			}
			
			if(element instanceof IssueLinkHandlerFactory) {
				IssueLinkHandlerFactory issueLinkHandlerFactory = (IssueLinkHandlerFactory)element;
				result = issueLinkHandlerFactory.getName();
			}
			
			return result==null?element.toString():result; //$NON-NLS-1$
		}
	}
}
