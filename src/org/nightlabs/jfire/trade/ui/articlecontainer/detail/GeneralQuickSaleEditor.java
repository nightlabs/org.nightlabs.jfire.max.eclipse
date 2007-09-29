package org.nightlabs.jfire.trade.ui.articlecontainer.detail;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.trade.ui.QuickSalePerspective;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class GeneralQuickSaleEditor 
extends EditorPart
implements IGeneralEditor
{
	public static final String ID_EDITOR = GeneralQuickSaleEditor.class.getName();
	
	private GeneralQuickSaleEditorComposite generalQuickSaleEditorComposite;
	public GeneralEditorComposite getGeneralEditorComposite() {
		return generalQuickSaleEditorComposite.getGeneralEditorComposite();
	}
	
	private GeneralEditorInput input;
	
	@Override
	public void doSave(IProgressMonitor monitor) {

	}

	@Override
	public void doSaveAs() {

	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
	throws PartInitException 
	{
		if (!(input instanceof GeneralEditorInput))
			throw new PartInitException("Invalid Input: Must be an instance of GeneralEditorInput! But is: " + (input == null ? null : input.getClass().getName())); //$NON-NLS-1$
		
		this.input = (GeneralEditorInput) input;

		setSite(site);
		setInput(input);

		setPartName(input.getName());
		ImageDescriptor img = input.getImageDescriptor();
		if (img != null)
			setTitleImage(img.createImage());
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		generalQuickSaleEditorComposite = new GeneralQuickSaleEditorComposite(getSite(), parent, input);
		RCPUtil.getActiveWorkbenchPage().addPartListener(quickSaleEditorListener);
	}

	@Override
	public void setFocus() {

	}

	private IPartListener quickSaleEditorListener = new IPartListener()
	{
		public void partClosed(IWorkbenchPart part) 
		{
			if (part.equals(GeneralQuickSaleEditor.this)) {
				if (RCPUtil.getActiveWorkbenchPage() != null) {
					QuickSalePerspective.checkOrderOpen(RCPUtil.getActivePerspectiveID());
				}
			}
		}		
		public void partOpened(IWorkbenchPart part) {			
		}	
		public void partDeactivated(IWorkbenchPart part) {			
		}		
		public void partBroughtToTop(IWorkbenchPart part) {			
		}	
		public void partActivated(IWorkbenchPart part) {			
		}		
	};
}
