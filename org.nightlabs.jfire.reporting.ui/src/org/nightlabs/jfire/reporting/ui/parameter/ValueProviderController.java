package org.nightlabs.jfire.reporting.ui.parameter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.base.jdo.GlobalJDOManagerProvider;
import org.nightlabs.jfire.base.ui.jdo.tree.ActiveJDOObjectTreeController;
import org.nightlabs.jfire.jdo.notification.IJDOLifecycleListenerFilter;
import org.nightlabs.jfire.jdo.notification.JDOLifecycleState;
import org.nightlabs.jfire.jdo.notification.TreeLifecycleListenerFilter;
import org.nightlabs.jfire.jdo.notification.TreeNodeParentResolver;
import org.nightlabs.jfire.reporting.parameter.ReportParameterManagerRemote;
import org.nightlabs.jfire.reporting.parameter.ValueProvider;
import org.nightlabs.jfire.reporting.parameter.ValueProviderCategory;
import org.nightlabs.jfire.reporting.parameter.ValueProviderParentResolver;
import org.nightlabs.jfire.reporting.parameter.dao.ValueProviderCategoryDAO;
import org.nightlabs.jfire.reporting.parameter.dao.ValueProviderDAO;
import org.nightlabs.jfire.reporting.parameter.id.ValueProviderCategoryID;
import org.nightlabs.jfire.reporting.parameter.id.ValueProviderID;
import org.nightlabs.jfire.reporting.ui.resource.Messages;
import org.nightlabs.jfire.security.GlobalSecurityReflector;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/**
 * Active controller for {@link ValueProviderCategory}s and {@link ValueProvider}s.
 *
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public abstract class ValueProviderController
extends ActiveJDOObjectTreeController<ObjectID, Object, ValueProviderTreeNode>
{
	public static final String[] CATEGORY_FETCH_GROUPS = new String[] {
		FetchPlan.DEFAULT, ValueProviderCategory.FETCH_GROUP_NAME, ValueProviderCategory.FETCH_GROUP_PARENT_ID
	};

	public static final String[] PROVIDER_FETCH_GROUPS = new String[] {
		FetchPlan.DEFAULT, ValueProvider.FETCH_GROUP_NAME, ValueProvider.FETCH_GROUP_CATEGORY_ID
	};

	@Override
	protected ValueProviderTreeNode createNode() {
		return new ValueProviderTreeNode();
	}

	@Override
	protected TreeNodeParentResolver createTreeNodeParentResolver() {
		return new ValueProviderParentResolver();
	}

	@Override
	protected Class<Object> getJDOObjectClass() {
		throw new UnsupportedOperationException("Should never be called!"); //$NON-NLS-1$
	}

	@Override
	protected IJDOLifecycleListenerFilter createJDOLifecycleListenerFilter(Set<? extends ObjectID> parentObjectIDs) {
		return new TreeLifecycleListenerFilter(
				new Class[] {ValueProviderCategory.class, ValueProvider.class}, true,
				parentObjectIDs, getTreeNodeParentResolver(),
				new JDOLifecycleState[] { JDOLifecycleState.NEW }
			);
	}


	private ChangeListener changeListener;

	@Override
	protected void createRegisterChangeListener() {
		if (changeListener == null) {
			changeListener = new ChangeListener(Messages.getString("org.nightlabs.jfire.reporting.ui.parameter.ValueProviderController.registerChangeListener.name")); //$NON-NLS-1$
			GlobalJDOManagerProvider.sharedInstance().getLifecycleManager().addNotificationListener(new Class[] {ValueProviderCategory.class, ValueProvider.class}, changeListener);
		}
	}

	@Override
	protected void unregisterChangeListener() {
		if (changeListener == null) {
			GlobalJDOManagerProvider.sharedInstance().getLifecycleManager().addNotificationListener(new Class[] {ValueProviderCategory.class, ValueProvider.class}, changeListener);
			changeListener = null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Collection<Object> retrieveChildren(ObjectID parentID, Object parent, ProgressMonitor monitor) {
		ReportParameterManagerRemote reportParameterManager = JFireEjb3Factory.getRemoteBean(
				ReportParameterManagerRemote.class, GlobalSecurityReflector.sharedInstance().getInitialContextProperties());
		if (parentID == null) {
			try {
				Set<ValueProviderCategoryID> topCategories = reportParameterManager.getValueProviderCategoryIDsForParent(null);
				return (Collection)ValueProviderCategoryDAO.sharedInstance().getValueProviderCategories(topCategories, CATEGORY_FETCH_GROUPS, monitor);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		if (parentID instanceof ValueProviderCategoryID) {
			monitor.beginTask(Messages.getString("org.nightlabs.jfire.reporting.ui.parameter.ValueProviderController.task.loadValueProviders"), 100); //$NON-NLS-1$
			try {
				Set<ValueProviderCategoryID> categoryIDs;
				categoryIDs = reportParameterManager.getValueProviderCategoryIDsForParent((ValueProviderCategoryID) parentID);
				Collection<Object> categories = (Collection)ValueProviderCategoryDAO.sharedInstance().getValueProviderCategories(categoryIDs, CATEGORY_FETCH_GROUPS, new SubProgressMonitor(monitor, 50));
				Set<ValueProviderID> providerIDs = reportParameterManager.getValueProviderIDsForParent((ValueProviderCategoryID) parentID);
				Collection<Object> providers = (Collection)ValueProviderDAO.sharedInstance().getValueProviders(providerIDs, PROVIDER_FETCH_GROUPS, new SubProgressMonitor(monitor, 50));
				categories.addAll(providers);
				return categories;
			} catch (Exception e) {
				throw new RuntimeException(e);
			} finally {
				monitor.done();
			}
		}
		else
			return Collections.emptySet();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Collection<Object> retrieveJDOObjects(Set<ObjectID> objectIDs, ProgressMonitor monitor) {
		if (objectIDs.size() <= 0)
			return Collections.emptySet();
		if (objectIDs.iterator().next() instanceof ValueProviderCategoryID) {
			Set<ValueProviderCategoryID> catIDs = ((Set)objectIDs);
			return ((Collection)ValueProviderCategoryDAO.sharedInstance().getValueProviderCategories(catIDs, CATEGORY_FETCH_GROUPS, monitor));
		}
		else {
			Set<ValueProviderID> providerIDs = ((Set)objectIDs);
			return ((Collection)ValueProviderDAO.sharedInstance().getValueProviders(providerIDs, PROVIDER_FETCH_GROUPS, monitor));
		}
	}

	@Override
	protected void sortJDOObjects(List<Object> objects) {
	}

//	@Override
//	protected abstract void onJDOObjectsChanged(JDOTreeNodesChangedEvent<ObjectID, ValueProviderTreeNode> changedEvent);
}
