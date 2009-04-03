package org.nightlabs.jfire.entityuserset.ui;

import java.util.List;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageControllerModifyEvent;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress;
import org.nightlabs.jfire.security.AuthorizedObject;

/**
 * @author Daniel Mazurek - Daniel.Mazurek [dot] nightlabs [dot] de
 *
 */
public abstract class AbstractEntityUserSetPage<Entity> 
extends EntityEditorPageWithProgress 
{
	private EntityUserSetSection<Entity> entityUserSetSection;
	private AuthorizedObjectSection<Entity> authorizedObjectSection;
	private AbstractEntitySection<Entity> entitySection;
	
	/**
	 * @param editor
	 * @param id
	 * @param name
	 */
	public AbstractEntityUserSetPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}

	/**
	 * Delegate to your page-controller and return the helper it uses. Usually, your implementation
	 * of this method simply looks like the following code:<br/><br/>
	 * <code>
	 * return ((EntityUserSetPageControllerHelper)getPageController()).getEntityUserSetPageControllerHelper();
	 * </code>
	 *
	 * @return the <code>EntityUserSetPageControllerHelper</code> used for managing your entityUserSet-page.
	 */	
	protected abstract EntityUserSetPageControllerHelper<Entity> getEntityUserSetPageControllerHelper();
	
	/**
	 * Creates the implementation of {@link AbstractEntitySection} which should be used for this page for
	 * showing the entities.
	 * 
	 * @param formPage the IFormPage for the section
	 * @param parent the parent Composite for the section
	 * @return the implementation of {@link AbstractEntitySection} which should be used for this page.
	 */
	protected abstract AbstractEntitySection<Entity> createEntitySection(IFormPage formPage, Composite parent);
	
	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress#addSections(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void addSections(Composite parent) 
	{
		entityUserSetSection = new EntityUserSetSection<Entity>(this, parent);
		getManagedForm().addPart(entityUserSetSection);
		
		authorizedObjectSection = new AuthorizedObjectSection<Entity>(this, parent);
		getManagedForm().addPart(authorizedObjectSection);
		
		entitySection = createEntitySection(this, parent);
		getManagedForm().addPart(entitySection);
		
		authorizedObjectSection.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				List<AuthorizedObject> selectedAuthorizedObjects = authorizedObjectSection.getSelectedAuthorizedObjects();
				AuthorizedObject selectedAuthorizedObject = null;
				if (!selectedAuthorizedObjects.isEmpty())
					selectedAuthorizedObject = selectedAuthorizedObjects.get(0);

				entitySection.setAuthorizedObject(selectedAuthorizedObject);
			}
		});
	}

	@Override
	protected void handleControllerObjectModified(EntityEditorPageControllerModifyEvent modifyEvent) 
	{
		EntityUserSetPageControllerHelper<Entity> controller = getEntityUserSetPageControllerHelper();
		entityUserSetSection.setEntityUserSetPageControllerHelper(controller);
		authorizedObjectSection.setEntityUserSetPageControllerHelper(controller);
		entitySection.setEntityUserSetPageControllerHelper(controller);
		switchToContent();
	}
}
