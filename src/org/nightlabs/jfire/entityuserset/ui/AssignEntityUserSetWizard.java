package org.nightlabs.jfire.entityuserset.ui;

import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jfire.entityuserset.EntityUserSet;
import org.nightlabs.jfire.entityuserset.id.EntityUserSetID;
import org.nightlabs.jfire.entityuserset.ui.resource.Messages;

/**
 * @author Daniel Mazurek - Daniel.Mazurek [dot] nightlabs [dot] de
 *
 */
public class AssignEntityUserSetWizard<Entity> 
extends DynamicPathWizard 
{
	private EntityUserSetID entityUserSetID;
	private EntityUserSetPageControllerHelper<Entity> entityUserSetPageControllerHelper;
	private SelectEntityUserSetPage<Entity> selectEntityUserSetPage;

	/**
	 * @param entityUserSetID
	 * @param inheritedEntityUserSetResolver
	 */
	public AssignEntityUserSetWizard(EntityUserSetID entityUserSetID,
			EntityUserSetPageControllerHelper<Entity> entityUserSetPageControllerHelper) 
	{
		super();
		this.entityUserSetID = entityUserSetID;
		this.entityUserSetPageControllerHelper = entityUserSetPageControllerHelper;
		setWindowTitle(String.format(Messages.getString("org.nightlabs.jfire.entityuserset.ui.AssignEntityUserSetWizard.window.title"), entityUserSetPageControllerHelper.getEntityUserSetName())); //$NON-NLS-1$
	}

	@Override
	public void addPages() {
		selectEntityUserSetPage = new SelectEntityUserSetPage<Entity>(entityUserSetID, entityUserSetPageControllerHelper);
		addPage(selectEntityUserSetPage);
	}

	public EntityUserSetID getEntityUserSetID() {
		return selectEntityUserSetPage.getEntityUserSetID();
	}

	public EntityUserSet<Entity> getNewEntityUserSet() {
		return selectEntityUserSetPage.getNewEntityUserSet();
	}

	public boolean isEntityUserSetIDInherited() {
		return SelectEntityUserSetPage.Action.inherit == selectEntityUserSetPage.getAction();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		return true;
	}
}
