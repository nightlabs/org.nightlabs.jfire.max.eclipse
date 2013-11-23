package org.nightlabs.jfire.scripting.ui.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.nightlabs.jfire.base.jdo.GlobalJDOManagerProvider;
import org.nightlabs.jfire.base.ui.jdo.tree.ActiveJDOObjectTreeController;
import org.nightlabs.jfire.jdo.notification.TreeNodeParentResolver;
import org.nightlabs.jfire.scripting.ScriptCategory;
import org.nightlabs.jfire.scripting.ScriptRegistryItem;
import org.nightlabs.jfire.scripting.ScriptRegistryItemParentResolver;
import org.nightlabs.jfire.scripting.dao.ScriptRegistryItemDAO;
import org.nightlabs.jfire.scripting.id.ScriptRegistryItemID;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Fitas Amine - fitas [at] nightlabs [dot] de
 */
public class ActiveScriptRegistryItemTreeController extends ActiveJDOObjectTreeController<ScriptRegistryItemID, ScriptRegistryItem, ScriptRegistryItemNode>
{
	
	public static final String[] DEFAULT_SCRIPT_FETCH_GROUPS = new String[] {
		FetchPlan.DEFAULT,
		ScriptRegistryItem.FETCH_GROUP_NAME,
		ScriptRegistryItem.FETCH_GROUP_PARAMETER_SET,
		ScriptRegistryItem.FETCH_GROUP_PARENT,
		ScriptRegistryItem.FETCH_GROUP_DESCRIPTION
	};


	@Override
	protected ScriptRegistryItemNode createNode() {
		return new ScriptRegistryItemNode();
	}

	@Override
	protected TreeNodeParentResolver createTreeNodeParentResolver() {
		// TODO Auto-generated method stub
		return new ScriptRegistryItemParentResolver();
	}

	@Override
	protected Class<? extends ScriptRegistryItem> getJDOObjectClass() {
		return ScriptRegistryItem.class;
	}

	@Override
	protected Collection<ScriptRegistryItem> retrieveChildren(
			ScriptRegistryItemID parentID, ScriptRegistryItem parent,
			ProgressMonitor monitor) {
		Collection<ScriptRegistryItemID> scriptItemIDs = null;
		if (parentID == null) {
			try {
				scriptItemIDs = ScriptRegistryItemDAO.sharedInstance().getTopLevelScriptRegistryItemIDs();
					} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		else {
			try {
				if (ScriptCategory.class.equals(GlobalJDOManagerProvider.sharedInstance().getObjectID2PCClassMap().getPersistenceCapableClass(parentID))) 
					scriptItemIDs =  ScriptRegistryItemDAO.sharedInstance().getScriptRegistryItemIDsForParent(parentID);	
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		if (scriptItemIDs == null) {
			return Collections.emptyList();
		}
		return ScriptRegistryItemDAO.sharedInstance().getScriptRegistryItems(new ArrayList<ScriptRegistryItemID>(scriptItemIDs),
				DEFAULT_SCRIPT_FETCH_GROUPS,
				monitor
			);
		
	}

	@Override
	protected Collection<ScriptRegistryItem> retrieveJDOObjects(
			Set<ScriptRegistryItemID> objectIDs, ProgressMonitor monitor) {		
		return ScriptRegistryItemDAO.sharedInstance().getScriptRegistryItems(new ArrayList<ScriptRegistryItemID>(objectIDs), 
				DEFAULT_SCRIPT_FETCH_GROUPS, 
				monitor);
	}

	@Override
	protected void sortJDOObjects(List<ScriptRegistryItem> objects) {
		// TODO Auto-generated method stub
		
	}

}
