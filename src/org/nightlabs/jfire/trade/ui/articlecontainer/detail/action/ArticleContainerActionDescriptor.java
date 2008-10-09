package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action;

import java.util.ArrayList;
import java.util.Collection;

import org.nightlabs.base.ui.action.registry.ActionDescriptor;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.util.CollectionUtil;

public class ArticleContainerActionDescriptor
extends ActionDescriptor
{
	private String articleContainerClass;
	public String getArticleContainerClass() {
		return articleContainerClass;
	}
	public void setArticleContainerClass(String articleContainerClass) {
		this.articleContainerClass = articleContainerClass;
	}

	private int calculateInterfaceDistance(Class<?> clazz)
	{
		int interfaceInheritanceLevel = 0;
		// first search in the current interfaceInheritanceLevel
		Class<?>[] interfaces = clazz.getInterfaces();
		while (interfaces.length > 0) {
			int interfaceOrderIndex = -1;
			Collection<Class<?>> superInterfaces = new ArrayList<Class<?>>();
			for (Class<?> iface : interfaces) {
				++interfaceOrderIndex;
				if (articleContainerClass.equals(iface.getName()))
					return interfaceInheritanceLevel * INTERFACE_INHERITANCE_DISTANCE_MULTIPLIER + interfaceOrderIndex;

				superInterfaces.addAll(CollectionUtil.array2ArrayList(iface.getInterfaces()));
			}
			++interfaceInheritanceLevel;
			interfaces = superInterfaces.toArray(new Class<?>[superInterfaces.size()]);
		}
		return -1;
	}

	public int calculateArticleContainerClassMatchDistance(Class<? extends ArticleContainer> clazz)
	{
		int classDistance = 0;
		int interfaceDistance = 0;
		Class<?> currentClass = clazz;
		while (currentClass != null) {
			interfaceDistance = 0;

			if (articleContainerClass.equals(currentClass.getName()))
				return CLASS_DISTANCE_MULTIPLIER * classDistance + interfaceDistance;

			interfaceDistance = calculateInterfaceDistance(currentClass);
			if (interfaceDistance >= 0)
				return CLASS_DISTANCE_MULTIPLIER * classDistance + interfaceDistance;

			++classDistance;
			currentClass = currentClass.getSuperclass();
		}
		return -1;
	}

	private static final int CLASS_DISTANCE_MULTIPLIER = 1 << 16;
	private static final int INTERFACE_INHERITANCE_DISTANCE_MULTIPLIER = 1 << 8;

}
