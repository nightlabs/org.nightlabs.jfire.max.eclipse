package org.nightlabs.jfire.entityuserset.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.jfire.security.AuthorizedObject;

/**
 * @author Daniel Mazurek - Daniel.Mazurek [dot] nightlabs [dot] de
 *
 */
public abstract class AbstractEntitySection<Entity> 
extends ToolBarSectionPart 
{
	private AbstractEntityTable<Entity> table;
	private EntityUserSetPageControllerHelper<Entity> entityUserSetPageControllerHelper;
	private AuthorizedObject authorizedObject;
	
	/**
	 * @param page
	 * @param parent
	 * @param title
	 */
	public AbstractEntitySection(IFormPage page, Composite parent, String title) {
		super(page, parent, ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED, title);
		table = createTable(getContainer());
		table.addCheckStateChangedListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem tableItem = ((TableItem) e.item);
				boolean checked = tableItem.getChecked();
				Entity entity = (Entity) tableItem.getData();
				Map<Entity, Boolean> entities = entityUserSetPageControllerHelper.getEntities(authorizedObject);
				entities.put(entity, checked);
			}
		});
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
	
	private void setEntities(Map<Entity, Boolean> entities) {
		table.setEntityInput(entities);
		Collection<Entity> checkedElements = new ArrayList<Entity>();
		for (Map.Entry<Entity, Boolean> entry : entities.entrySet()) {
			if (entry.getValue()) {
				checkedElements.add(entry.getKey());
			}
		}
		table.setCheckedElements(checkedElements);
	}
	
	public void setAuthorizedObject(AuthorizedObject authorizedObject) 
	{
		this.authorizedObject = authorizedObject;
		if (entityUserSetPageControllerHelper != null) {
			Map<Entity, Boolean> entities = entityUserSetPageControllerHelper.getEntities(authorizedObject);
			setEntities(entities);
		}
	}
}
