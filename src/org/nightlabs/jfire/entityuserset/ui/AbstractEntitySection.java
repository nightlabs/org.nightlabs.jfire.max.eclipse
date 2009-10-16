package org.nightlabs.jfire.entityuserset.ui;

import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.jfire.entityuserset.ui.resource.Messages;
import org.nightlabs.jfire.security.AuthorizedObject;

/**
 * @author Daniel Mazurek - Daniel.Mazurek [dot] nightlabs [dot] de
 *
 */
public abstract class AbstractEntitySection<Entity>
extends ToolBarSectionPart
{
	class SelectAllAction extends Action
	{
		public SelectAllAction() {
			super();
			setText(Messages.getString("org.nightlabs.jfire.entityuserset.ui.AbstractEntitySection.action.selectAll.text")); //$NON-NLS-1$
			setToolTipText(Messages.getString("org.nightlabs.jfire.entityuserset.ui.AbstractEntitySection.action.selectAll.tooltip")); //$NON-NLS-1$
			setImageDescriptor(SharedImages.getSharedImageDescriptor(Activator.getDefault(), SelectAllAction.class));
		}

		@Override
		public void run() {
			if (entities != null) { //IMHO: I think this is better than initializing an empty map, Chairat
				for (Map.Entry<Entity, Boolean> entry : entities.entrySet()) {
					entry.setValue(true);
				}
				table.setEntityInput(entities);
				markDirty();
			}
		}
	}

	class DeselectAllAction extends Action
	{
		public DeselectAllAction() {
			super();
			setText(Messages.getString("org.nightlabs.jfire.entityuserset.ui.AbstractEntitySection.action.deselectAll.text")); //$NON-NLS-1$
			setToolTipText(Messages.getString("org.nightlabs.jfire.entityuserset.ui.AbstractEntitySection.action.deselectAll.tooltip")); //$NON-NLS-1$
			setImageDescriptor(SharedImages.getSharedImageDescriptor(Activator.getDefault(), DeselectAllAction.class));
		}

		@Override
		public void run() {
			if (entities != null) {
				for (Map.Entry<Entity, Boolean> entry : entities.entrySet()) {
					entry.setValue(false);
				}
				table.setEntityInput(entities);
				markDirty();
			}
		}
	}

	private AbstractEntityTable<Entity> table;
	private EntityUserSetPageControllerHelper<Entity> entityUserSetPageControllerHelper;
	private Map<Entity, Boolean> entities;

	/**
	 * @param page
	 * @param parent
	 * @param title
	 */
	public AbstractEntitySection(IFormPage page, Composite parent, String title) {
		super(page, parent, ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED, title);
		table = createTable(getContainer());
		getToolBarManager().add(new SelectAllAction());
		getToolBarManager().add(new DeselectAllAction());
		updateToolBarManager();
	}

	/**
	 * Creates an implementation of {@link AbstractTableComposite} which shows the entities.
	 * @param parent the parent Composite
	 * @return the implementation of {@link AbstractTableComposite} which shows the entities.
	 */
	protected abstract AbstractEntityTable<Entity> createTable(Composite parent);

	public void setEntityUserSetPageControllerHelper(EntityUserSetPageControllerHelper<Entity> entityUserSetPageControllerHelper) {
		this.entityUserSetPageControllerHelper = entityUserSetPageControllerHelper;
	}

	public void setAuthorizedObject(AuthorizedObject authorizedObject)
	{
		if (entityUserSetPageControllerHelper != null) {
			entities = entityUserSetPageControllerHelper.getEntities(authorizedObject);
			table.setEntityInput(entities);
		}
	}
}
