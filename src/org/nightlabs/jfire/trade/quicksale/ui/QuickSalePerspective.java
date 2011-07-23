package org.nightlabs.jfire.trade.quicksale.ui;

import javax.security.auth.login.LoginException;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IPerspectiveListener4;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PerspectiveAdapter;
import org.nightlabs.base.ui.editor.Editor2PerspectiveRegistry;
import org.nightlabs.base.ui.login.LoginState;
import org.nightlabs.base.ui.notification.NotificationAdapterSWTThreadAsync;
import org.nightlabs.base.ui.notification.SelectionManager;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.base.login.ui.Login;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditorInput;
import org.nightlabs.jfire.trade.ui.detail.ProductTypeDetailView;
import org.nightlabs.jfire.trade.ui.producttype.quicklist.ProductTypeQuickListView;
import org.nightlabs.jfire.trade.ui.transfer.deliver.DeliveryQueueBrowsingView;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 */
public class QuickSalePerspective
implements IPerspectiveFactory
{
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(QuickSalePerspective.class);

	public static final String ID_PERSPECTIVE = QuickSalePerspective.class.getName();

	private static boolean perspectiveListenerAdded = false;
	private static IPerspectiveListener4 quickSalePerspectiveListener = new PerspectiveAdapter()
	{
		@Override
		public void perspectiveActivated(IWorkbenchPage page, final IPerspectiveDescriptor perspective) {
			logger.info("perspectiveActivated "+perspective.getId()); //$NON-NLS-1$
			// perform "checkOrderOpen" in the next event loop cycle, in order to ensure that the perspective switch
			// is completely finished. Otherwise, the page.getActiveEditor() will still return the last active editor
			// (from the last page). Marco.
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					checkOrderOpen(perspective.getId());
				}
			});
		}
		@Override
		public void perspectiveOpened(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
		}
	};

	private static boolean selectionListenerAdded = false;
	private static NotificationListener selectionListener = new NotificationAdapterSWTThreadAsync() {
		public void notify(NotificationEvent notificationEvent) {
			checkOrderOpen(RCPUtil.getActivePerspectiveID());
		}
	};

	static {
		if (Display.getCurrent() == null) {
			Display.getDefault().asyncExec(new Runnable()
			{
				public void run()
				{
					checkPerspectiveListenerAdded();
					checkSelectionListenerAdded();
				}
			});
		}
		else {
			checkPerspectiveListenerAdded();
			checkSelectionListenerAdded();
		}
	}

	public void createInitialLayout(IPageLayout layout)
	{
		layout.setEditorAreaVisible(true);
		layout.addView(ProductTypeDetailView.ID_VIEW, IPageLayout.TOP,
				0.2f, IPageLayout.ID_EDITOR_AREA);
//		layout.addView(ProductTypeQuickListView.ID_VIEW, IPageLayout.RIGHT, 0.7f,
//				IPageLayout.ID_EDITOR_AREA);

		IFolderLayout folder = layout.createFolder("right_bottom", IPageLayout.RIGHT, 0.7f, IPageLayout.ID_EDITOR_AREA); //$NON-NLS-1$
		folder.addView(ProductTypeQuickListView.ID_VIEW);
		// The DeliveryQueueBrowsingView is not required in the quick sale perspective and doesn't have sufficient space here anyway!
		// Commented it out. 2008-10-02 Marco.
//		folder.addView(DeliveryQueueBrowsingView.ID_VIEW);

		layout.addPerspectiveShortcut(ID_PERSPECTIVE);
		layout.addShowViewShortcut(ProductTypeDetailView.ID_VIEW);
		layout.addShowViewShortcut(ProductTypeQuickListView.ID_VIEW);
		layout.addShowViewShortcut(DeliveryQueueBrowsingView.ID_VIEW);

		RCPUtil.addAllPerspectiveShortcuts(layout);
	}

	private static void openEditor(final ArticleContainerEditorInput editorInput, final boolean editor2Perspective)
	{
		if (Display.getCurrent() == null)
			throw new IllegalStateException("Wrong thread!!! This method must be called on the UI thread!"); //$NON-NLS-1$

		try {
			if (editor2Perspective) {
				Editor2PerspectiveRegistry.sharedInstance().openEditor(editorInput, ArticleContainerQuickSaleEditor.ID_EDITOR);
			}
			else {
				if (RCPUtil.getActiveWorkbenchPage() != null)
					RCPUtil.openEditor(editorInput, ArticleContainerQuickSaleEditor.ID_EDITOR);
			}
		} catch (Exception x) {
			throw new RuntimeException(x);
		}
		logger.info("openEditor: editor opened for input="+editorInput); //$NON-NLS-1$
	}

	private static void checkPerspectiveListenerAdded()
	{
		if (Display.getCurrent() == null)
			throw new IllegalStateException("Wrong thread!!! This method must be called on the UI thread!"); //$NON-NLS-1$

		if (!perspectiveListenerAdded) {
			try {
				RCPUtil.getActiveWorkbenchWindow().addPerspectiveListener(quickSalePerspectiveListener);
				perspectiveListenerAdded = true;
				logger.info("perspectiveListener added"); //$NON-NLS-1$
			} catch (Exception e) {
				perspectiveListenerAdded = false;
				logger.error("adding perspectiveListener failed", e); //$NON-NLS-1$
				return;
			}
		}
	}

	/**
	 * This method checks whether there is exactly one editor open in the <code>QuickSalePerspective</code>. If there are multiple
	 * open, all except one will be closed. If there is no editor open, one will be opened.
	 * <p>
	 * This method has no effect, if the passed <code>perspectiveID</code> does not equal the {@link QuickSalePerspective#ID_PERSPECTIVE}.
	 * </p>
	 *
	 * @param perspectiveID The current perspective's ID. If this method is used in a perspective-listener, this identifier is passed to the listener method.
	 *		If it is not known in the current context, it can be obtained by {@link RCPUtil#getActivePerspectiveID()}. Note, that you should always use the
	 *		identifier passed to your listener instead of the <code>RCPUtil</code>'s method, since during boot-up or transitions the listener's perspective-id
	 *		is likely more correct and reliable.
	 */
	public static void checkOrderOpen(String perspectiveID)
	{
		if (Display.getCurrent() == null)
			throw new IllegalStateException("Wrong thread!!! This method must be called on the UI thread!"); //$NON-NLS-1$

		if (perspectiveID != null && perspectiveID.equals(QuickSalePerspective.ID_PERSPECTIVE))
		{
			final IWorkbenchPage page = RCPUtil.getActiveWorkbenchPage();
			// open editor if necessary
			if (page != null)
			{
				IEditorReference[] references = page.getEditorReferences();
				if (references.length == 0) {
					openQuickSaleEditor();
				}
				else {
					boolean articleContainerFound = false;
					for (IEditorReference reference : references) {
						if (ArticleContainerQuickSaleEditor.ID_EDITOR.equals(reference.getId())) {
							articleContainerFound = true;
							break;
						}
					}
					if (!articleContainerFound) {
						openQuickSaleEditor();
					}
				}
			}
			// commented because now with reverse product action additional editor can be opened in QuickSalePerspective
//			if (page != null) {
//				// close additional editors if more than one is open
//				IEditorReference[] references = page.getEditorReferences();
//				if (references.length > 1) {
//					IEditorInput activeInput = null;
//					if (page.getActiveEditor() != null) {
//						activeInput = page.getActiveEditor().getEditorInput();
//					}
//					for (int i=0; i<references.length; i++) {
//						IEditorReference reference = references[i];
//						String editorID = reference.getId();
//						if (!editorID.equals(ArticleContainerQuickSaleEditor.ID_EDITOR)) {
//							logger.info("Closing editor (because it is no ArticleContainerQuickSaleEditor): editorID=" + editorID); //$NON-NLS-1$
//							page.closeEditor(reference.getEditor(false), true);
//						}
//						try {
//							IEditorInput input = reference.getEditorInput();
//							if (activeInput != null && !activeInput.equals(input)) {
//								logger.info("Closing editor (because it is not showing the active one): input=" + input); //$NON-NLS-1$
//								page.closeEditor(reference.getEditor(false), true);
//							}
//						} catch (PartInitException e) {
//							logger.error("PartInitException: " + e.getLocalizedMessage(), e); //$NON-NLS-1$
//						}
//					}
//				}
//			}
		}
	}

	private static void openQuickSaleEditor()
	{
		try {
			Login.getLogin();
		} catch (LoginException e) {
			throw new RuntimeException(e);
		}
		ArticleContainerEditorInput input = ArticleContainerQuickSaleEditorPage.createEditorInput();
		if (input != null) {
			logger.info("Opening QuickSaleEditor: input=" + input); //$NON-NLS-1$
			openEditor(input, false);
		}
		else
			logger.warn("Opening QuickSaleEditor not possible, because input is null!"); //$NON-NLS-1$
	}

	private static void checkSelectionListenerAdded() {
		if (!selectionListenerAdded) {
			try {
				if (Login.sharedInstance().getLoginState() == LoginState.LOGGED_IN) {
					// sometimes causes a ClassNotFoundException for ProductType
					// when trying to switch open the perspective
					SelectionManager.sharedInstance().addNotificationListener(TradePlugin.ZONE_SALE, ProductType.class, selectionListener);
					selectionListenerAdded = true;
					logger.info("selectionListener added"); //$NON-NLS-1$
				}
				else {
					selectionListenerAdded = false;
					logger.info("adding selectionListener failed, because not logined in"); //$NON-NLS-1$
				}
			} catch (Exception e) {
				selectionListenerAdded = false;
				logger.info("adding selectionListener failed"); //$NON-NLS-1$
				return;
			}
		}
	}

}
