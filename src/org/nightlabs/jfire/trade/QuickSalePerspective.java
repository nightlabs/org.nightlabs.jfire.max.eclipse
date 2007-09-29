package org.nightlabs.jfire.trade;

import java.util.Set;

import javax.security.auth.login.LoginException;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IPerspectiveListener4;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PerspectiveAdapter;
import org.nightlabs.base.ui.editor.Editor2PerspectiveRegistry;
import org.nightlabs.base.ui.notification.SelectionManager;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.trade.articlecontainer.detail.GeneralEditorInput;
import org.nightlabs.jfire.trade.articlecontainer.detail.GeneralQuickSaleEditor;
import org.nightlabs.jfire.trade.articlecontainer.detail.GeneralQuickSaleEditorComposite;
import org.nightlabs.jfire.trade.detail.ProductTypeDetailView;
import org.nightlabs.jfire.trade.producttype.quicklist.ProductTypeQuickListView;
import org.nightlabs.notification.NotificationAdapterCallerThread;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * 
 */
public class QuickSalePerspective 
implements IPerspectiveFactory 
{
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(QuickSalePerspective.class);

	public static final String ID_PERSPECTIVE = QuickSalePerspective.class.getName();

	public QuickSalePerspective() 
	{
		try {
			Login.getLogin();
		} catch (LoginException e) {
			logger.error("login failed", e); //$NON-NLS-1$
		}
		checkPerspectiveListenerAdded();
		checkSelectionListenerAdded();		
	}

	public void createInitialLayout(IPageLayout layout) 
	{
		layout.setEditorAreaVisible(true);
		layout.addView(ProductTypeDetailView.ID_VIEW, IPageLayout.TOP,
				0.2f, IPageLayout.ID_EDITOR_AREA);
		layout.addView(ProductTypeQuickListView.ID_VIEW, IPageLayout.RIGHT, 0.7f,
				IPageLayout.ID_EDITOR_AREA);

		layout.addPerspectiveShortcut(ID_PERSPECTIVE);
		layout.addShowViewShortcut(ProductTypeDetailView.ID_VIEW);
		layout.addShowViewShortcut(ProductTypeQuickListView.ID_VIEW);
		RCPUtil.addAllPerspectiveShortcuts(layout);

		GeneralEditorInput input = GeneralQuickSaleEditorComposite.createEditorInput(); 
		checkOrderOpen(ID_PERSPECTIVE);
		checkPerspectiveListenerAdded();
		checkSelectionListenerAdded();
	}

	public static void openEditor(final GeneralEditorInput editorInput, final boolean editor2Perspective) 
	{
			Display.getDefault().asyncExec(new Runnable(){				
					public void run() {
						try {						
							if (editor2Perspective) {
								Editor2PerspectiveRegistry.sharedInstance().openEditor(
										editorInput, GeneralQuickSaleEditor.ID_EDITOR);				
							}
							else {
								if (RCPUtil.getActiveWorkbenchPage() != null)
									RCPUtil.openEditor(
											editorInput, GeneralQuickSaleEditor.ID_EDITOR);
							}
						} catch (Exception x) {
							throw new RuntimeException(x);
						} 					
						logger.info("openEditor "+editorInput);								 //$NON-NLS-1$
					}			
			});
	}

	private static boolean perspectiveListenerAdded = false;
	public static synchronized void checkPerspectiveListenerAdded() {
		if (!perspectiveListenerAdded) {
			try {
				RCPUtil.getActiveWorkbenchWindow().addPerspectiveListener(quickSalePerspectiveListener);
				perspectiveListenerAdded = true;
				logger.info("perspectiveListener added"); //$NON-NLS-1$
			} catch (IllegalStateException e) {
				perspectiveListenerAdded = false;
				logger.info("perspectiveListener added failed"); //$NON-NLS-1$
				return;
			}
		}
	}
	
	private static IPerspectiveListener4 quickSalePerspectiveListener = new PerspectiveAdapter() 
	{		
		@Override
		public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
			logger.info("perspectiveActivated "+perspective.getId()); //$NON-NLS-1$
			checkOrderOpen(perspective.getId());
		}
		@Override
		public void perspectiveOpened(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
//			logger.info("perspectiveOpened "+perspective.getId());
//			checkOrderOpen(perspective.getId());
		}
	};
			
	public static synchronized void checkOrderOpen(String perspectiveID) 
	{
		if (perspectiveID != null && perspectiveID.equals(QuickSalePerspective.ID_PERSPECTIVE)) 
		{			
			final IWorkbenchPage page = RCPUtil.getActiveWorkbenchPage();
			// open editor if necessary
			if (page != null && page.getActiveEditor() == null && 
					page.getEditorReferences().length == 0) 
			{
				GeneralEditorInput input = GeneralQuickSaleEditorComposite.createEditorInput(); 
				if (input != null) {
					openEditor(input, false);
				}
			}
			if (page != null) {
				Display.getDefault().asyncExec(new Runnable(){
					public void run() {
						// close additional editors if more than one is open
						IEditorReference[] references = page.getEditorReferences();
						if (references.length > 1) {
							IEditorInput activeInput = null;
							if (page.getActiveEditor() != null) {
								activeInput = page.getActiveEditor().getEditorInput();
							}				
							for (int i=0; i<references.length; i++) {
								IEditorReference reference = references[i];
								String editorID = reference.getId();
								if (!editorID.equals(GeneralQuickSaleEditor.ID_EDITOR)) {
									page.closeEditor(reference.getEditor(false), true);
								}
								try {
									IEditorInput input = reference.getEditorInput();
									if (activeInput != null && !activeInput.equals(input)) {
										page.closeEditor(reference.getEditor(false), true);
									}
								} catch (PartInitException e) {
									logger.error(e);
								}						
							}
						}						
					}				
				});
			}				
		}			
	}
	private static boolean selectionListenerAdded = false;
	public static void checkSelectionListenerAdded() {
		if (!selectionListenerAdded) {
			try {
				SelectionManager.sharedInstance().addNotificationListener(TradePlugin.ZONE_SALE, 
						ProductType.class, selectionListener);	
				selectionListenerAdded = true;
				logger.info("selectionListenerAdded added"); //$NON-NLS-1$
			} catch (Exception e) {
				selectionListenerAdded = false;
				logger.info("selectionListenerAdded added failed"); //$NON-NLS-1$
				return;
			}
		}
	}	
	
	private static NotificationListener selectionListener = new NotificationAdapterCallerThread(){
		public void notify(NotificationEvent notificationEvent) {
			if (notificationEvent.getSource() instanceof ProductTypeQuickListView) { 
				Set subjects = notificationEvent.getSubjects();
				checkOrderOpen(RCPUtil.getActivePerspectiveID());
			}
		}	
	};
}
