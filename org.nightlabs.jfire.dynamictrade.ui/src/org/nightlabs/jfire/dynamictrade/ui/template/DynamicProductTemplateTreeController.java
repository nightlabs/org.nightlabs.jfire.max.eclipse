package org.nightlabs.jfire.dynamictrade.ui.template;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.jdo.tree.ActiveJDOObjectTreeController;
import org.nightlabs.jfire.base.ui.jdo.tree.JDOObjectTreeNode;
import org.nightlabs.jfire.dynamictrade.dao.DynamicProductTemplateDAO;
import org.nightlabs.jfire.dynamictrade.template.DynamicProductTemplate;
import org.nightlabs.jfire.dynamictrade.template.DynamicProductTemplateParentResolver;
import org.nightlabs.jfire.dynamictrade.template.id.DynamicProductTemplateID;
import org.nightlabs.jfire.jdo.notification.TreeNodeParentResolver;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.util.NLLocale;

public class DynamicProductTemplateTreeController
extends ActiveJDOObjectTreeController<
	DynamicProductTemplateID,
	DynamicProductTemplate,
	JDOObjectTreeNode<DynamicProductTemplateID, DynamicProductTemplate, DynamicProductTemplateTreeController>
>
{
	public static String[] FETCH_GROUPS_DYNAMIC_PRODUCT_TEMPLATE = {
		FetchPlan.DEFAULT,
		DynamicProductTemplate.FETCH_GROUP_NAME,
		DynamicProductTemplate.FETCH_GROUP_PARENT_CATEGORY_ID,
	};

	@Override
	protected JDOObjectTreeNode<DynamicProductTemplateID, DynamicProductTemplate, DynamicProductTemplateTreeController> createNode() {
		return new JDOObjectTreeNode<DynamicProductTemplateID, DynamicProductTemplate, DynamicProductTemplateTreeController>();
	}

	@Override
	protected TreeNodeParentResolver createTreeNodeParentResolver() {
		return new DynamicProductTemplateParentResolver();
	}

	@Override
	protected Class<? extends DynamicProductTemplate> getJDOObjectClass() {
		return DynamicProductTemplate.class;
	}

	@Override
	protected Collection<DynamicProductTemplate> retrieveChildren(DynamicProductTemplateID parentCategoryID, DynamicProductTemplate parent, ProgressMonitor monitor) {
		return DynamicProductTemplateDAO.sharedInstance().getChildDynamicProductTemplates(
				parentCategoryID, FETCH_GROUPS_DYNAMIC_PRODUCT_TEMPLATE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor
		);
	}

	@Override
	protected Collection<DynamicProductTemplate> retrieveJDOObjects(Set<DynamicProductTemplateID> objectIDs, ProgressMonitor monitor) {
		return DynamicProductTemplateDAO.sharedInstance().getDynamicProductTemplates(
				objectIDs, FETCH_GROUPS_DYNAMIC_PRODUCT_TEMPLATE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor
		);
	}

	@Override
	protected void sortJDOObjects(List<DynamicProductTemplate> objects) {
		Collections.sort(objects, new Comparator<DynamicProductTemplate>() {
			private String languageID = NLLocale.getDefault().getLanguage();

			@Override
			public int compare(DynamicProductTemplate o1, DynamicProductTemplate o2) {
				return o1.getName().getText(languageID).compareTo(o2.getName().getText(languageID));
			}
		});
	}

}
