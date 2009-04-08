package org.nightlabs.jfire.entityuserset.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.jfire.base.admin.ui.editor.authority.AuthorizedObjectTableViewer;
import org.nightlabs.jfire.security.AuthorizedObject;
import org.nightlabs.jfire.security.User;
import org.nightlabs.jfire.security.UserLocal;
import org.nightlabs.jfire.security.id.AuthorizedObjectID;

/**
 * @author Daniel Mazurek - Daniel.Mazurek [dot] nightlabs [dot] de
 *
 */
public class AuthorizedObjectSection<Entity>
extends ToolBarSectionPart
implements ISelectionProvider
{
	class SelectAllAction extends Action 
	{
		public SelectAllAction() {
			super();
			setText("Select All");
			setToolTipText("Check all elements");
			setImageDescriptor(SharedImages.getSharedImageDescriptor(Activator.getDefault(), SelectAllAction.class));
		}
		
		@Override
		public void run() {
			for (Map.Entry<AuthorizedObject, Boolean> entry : authorizedObjects) {
				entry.setValue(true);
			}
			authorizedObjectTable.setInput(authorizedObjects);
			markDirty();
		}
	}

	class DeselectAllAction extends Action 
	{
		public DeselectAllAction() {
			super();
			setText("Deselect All");
			setToolTipText("Uncheck all elements");
			setImageDescriptor(SharedImages.getSharedImageDescriptor(Activator.getDefault(), DeselectAllAction.class));
		}
		
		@Override
		public void run() {
			for (Map.Entry<AuthorizedObject, Boolean> entry : authorizedObjects) {
				entry.setValue(false);
			}
			authorizedObjectTable.setInput(authorizedObjects);
			markDirty();
		}
	}
	
	private AuthorizedObjectTableViewer authorizedObjectTable;
	private List<Map.Entry<AuthorizedObject, Boolean>> authorizedObjects = new ArrayList<Map.Entry<AuthorizedObject,Boolean>>();
	private EntityUserSetPageControllerHelper<Entity> entityUserSetPageControllerHelper;
	private ListenerList selectionChangedListeners = new ListenerList();
	private List<AuthorizedObject> selectedAuthorizedObjects = null;
	private IStructuredSelection selection = null;

	/**
	 * @param page
	 * @param parent
	 */
	public AuthorizedObjectSection(IFormPage page, Composite parent) 
	{
		super(page, parent, ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED, 
				"Authorized Objects");
		
		authorizedObjectTable = new AuthorizedObjectTableViewer(getContainer(), this,
				AbstractTableComposite.DEFAULT_STYLE_SINGLE | XComposite.getBorderStyle(getContainer()));
		authorizedObjectTable.setInput(authorizedObjects);
		authorizedObjectTable.getTable().addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent event) {
				setEntityUserSetPageControllerHelper(null);
			}
		});

		authorizedObjectTable.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				selectedAuthorizedObjects = null;
				selection = null;
				fireSelectionChangedEvent();
			}
		});
		
		getToolBarManager().add(new SelectAllAction());
		getToolBarManager().add(new DeselectAllAction());
		updateToolBarManager();
	}
		
	public EntityUserSetPageControllerHelper<Entity> getEntityUserSetPageControllerHelper() {
		return entityUserSetPageControllerHelper;
	}
	
	/**
	 * Set the {@link EntityUserSetPageControllerHelper} that is used for the current editor page. It is possible to
	 * pass <code>null</code> in order to indicate that there is nothing to be managed right now (and thus to clear
	 * the UI).
	 *
	 * @param entityUserSetPageControllerHelper an instance of <code>EntityUserSetPageControllerHelper</code> or <code>null</code>.
	 */
	public synchronized void setEntityUserSetPageControllerHelper(EntityUserSetPageControllerHelper<Entity> entityUserSetPageControllerHelper) {
		if (this.entityUserSetPageControllerHelper != null) {
			this.entityUserSetPageControllerHelper.removePropertyChangeListener(
					EntityUserSetPageControllerHelper.PROPERTY_NAME_ENTITY_USER_SET_LOADED,
					propertyChangeListenerEntityUserSetLoaded
			);
		}

		this.entityUserSetPageControllerHelper = entityUserSetPageControllerHelper;

		getSection().getDisplay().asyncExec(new Runnable() {
			public void run() {
				entityUserSetChanged();
			}
		});

		if (this.entityUserSetPageControllerHelper != null) {
			this.entityUserSetPageControllerHelper.addPropertyChangeListener(
					EntityUserSetPageControllerHelper.PROPERTY_NAME_ENTITY_USER_SET_LOADED,
					propertyChangeListenerEntityUserSetLoaded
			);
		}
	}

	private PropertyChangeListener propertyChangeListenerEntityUserSetLoaded = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			getSection().getDisplay().asyncExec(new Runnable() {
				public void run() {
					entityUserSetChanged();
				}
			});
		}
	};

	private void entityUserSetChanged()
	{
		authorizedObjects.clear();
		if (entityUserSetPageControllerHelper != null) {
			for (Map.Entry<AuthorizedObject, Boolean> entry : entityUserSetPageControllerHelper.getAuthorizedObjects().entrySet()) {	
				AuthorizedObject authorizedObject = entry.getKey();
				if (authorizedObject instanceof UserLocal) {
					UserLocal userLocal = (UserLocal) authorizedObject;
					if (User.USER_ID_OTHER.equals(userLocal.getUserID()) || User.USER_ID_SYSTEM.equals(userLocal.getUserID())) {
						continue;
					}
				}
				authorizedObjects.add(entry);				
			}
		}

		if (!authorizedObjectTable.getTable().isDisposed())
			authorizedObjectTable.refresh();
	}

	protected void fireSelectionChangedEvent()
	{
		SelectionChangedEvent event = new SelectionChangedEvent(this, getSelection());

		for (Object listener : selectionChangedListeners.getListeners())
			((ISelectionChangedListener)listener).selectionChanged(event);
	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		selectionChangedListeners.add(listener);
	}

	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		selectionChangedListeners.remove(listener);
	}

	/**
	 * Get the selected authorizedObjects. This method provides a more specific API than the general (and not typed)
	 * {@link #getSelection()}, but the returned instances of {@link AuthorizedObject} are the same.
	 *
	 * @return the selected authorizedObjects.
	 */
	public List<AuthorizedObject> getSelectedAuthorizedObjects() {
		getSelection(); // ensure the existence of our data and that we are on the correct thread
		return selectedAuthorizedObjects;
	}

	/**
	 * Get an {@link IStructuredSelection} containing {@link AuthorizedObject} instances. The instances are the same as
	 * returned by {@link #getSelectedAuthorizedObjects()}.
	 *
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked") //$NON-NLS-1$
	@Override
	public ISelection getSelection() {
		if (Display.getCurrent() == null)
			throw new IllegalStateException("Wrong thread! This method must be called on the SWT UI thread!"); //$NON-NLS-1$

		if (selectedAuthorizedObjects == null || selection == null) {
			selectedAuthorizedObjects = new ArrayList<AuthorizedObject>();
			selection = null;
			IStructuredSelection sel = (IStructuredSelection) authorizedObjectTable.getSelection();
			for (Object object : sel.toArray()) {
				Map.Entry<AuthorizedObject, Boolean> me = (Entry<AuthorizedObject, Boolean>) object;
				selectedAuthorizedObjects.add(me.getKey());
			}
			selection = new StructuredSelection(selectedAuthorizedObjects);
		}

		return selection;
	}

	/**
	 * Set an {@link IStructuredSelection} containing {@link AuthorizedObject} or {@link AuthorizedObjectID} instances.
	 */
	@Override
	public void setSelection(ISelection selection) {
		if (!(selection instanceof IStructuredSelection))
			throw new IllegalArgumentException("selection must be an instance of IStructuredSelection!"); //$NON-NLS-1$

		if (Display.getCurrent() == null)
			throw new IllegalStateException("Wrong thread! This method must be called on the SWT UI thread!"); //$NON-NLS-1$

		IStructuredSelection sel = (IStructuredSelection) selection;
		Set<AuthorizedObjectID> selectedAuthorizedObjectIDs = new HashSet<AuthorizedObjectID>(sel.size());
		for (Object object : sel.toArray()) {
			if (object instanceof AuthorizedObjectID)
				selectedAuthorizedObjectIDs.add((AuthorizedObjectID) object);
			else if (object instanceof AuthorizedObject) {
				AuthorizedObjectID authorizedObjectID = (AuthorizedObjectID) JDOHelper.getObjectId(object);
				if (authorizedObjectID == null)
					throw new IllegalArgumentException("The selection contains a AuthorizedObject that has no AuthorizedObjectID assigned!"); // should never happen, since all the authorizedObjects we manage are already persisted and detached. //$NON-NLS-1$

				selectedAuthorizedObjectIDs.add(authorizedObjectID);
			}
			else
				throw new IllegalArgumentException("The selection contains an object that's neither an instance of AuthorizedObjectID nor an instance of AuthorizedObject! The object is: " + object); //$NON-NLS-1$
		}

		// now that we have all AuthorizedObjectIDs that should be selected in our set, we iterate the authorizedObjects that are in our authorizedObjectTable and collect the elements that should be selected
		List<Map.Entry<AuthorizedObject, Boolean>> elementsToBeSelected = new ArrayList<Entry<AuthorizedObject,Boolean>>(selectedAuthorizedObjectIDs.size());
		for (Map.Entry<AuthorizedObject, Boolean> me : authorizedObjects) {
			if (selectedAuthorizedObjectIDs.contains(JDOHelper.getObjectId(me.getKey())))
				elementsToBeSelected.add(me);
		}

		authorizedObjectTable.setSelection(new StructuredSelection(elementsToBeSelected));
	}
	
}
