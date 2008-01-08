package org.nightlabs.jfire.issuetracking.ui.issuelink;

import java.util.List;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.extensionpoint.EPProcessorException;
import org.nightlabs.base.ui.tree.TreeContentProvider;
import org.nightlabs.base.ui.wizard.WizardHopPage;

public class IssueLinkObjectWizardPage 
extends WizardHopPage
{
	
	public IssueLinkObjectWizardPage() {
		super("Select an Object", "Select an object to link this issue with.");
		setDescription("Link an object to the issue.");
	}

	@Override
	public Control createPageContents(Composite parent) {
		XComposite mainComposite = new XComposite(parent, SWT.NONE);
		mainComposite.getGridLayout().numColumns = 1;

		TreeViewer treeViewer = new TreeViewer(mainComposite, SWT.NONE);
		treeViewer.setContentProvider(new LinkableObjectContentProvider());
		treeViewer.setLabelProvider(new LinkableObjectLabelProvider());
		
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
		
		return mainComposite;
	}

	class LinkableObjectContentProvider extends TreeContentProvider {
		public Object getParent(Object childElement) {
			IssueLinkHandlerCategory child = (IssueLinkHandlerCategory)childElement;
			return child.getParent();
		}

		public boolean hasChildren(Object element) {
			return true;
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
	
	class LinkableObjectLabelProvider extends LabelProvider {
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
			
			return result==null?element.getClass().getName():result; //$NON-NLS-1$
		}
	}

}
