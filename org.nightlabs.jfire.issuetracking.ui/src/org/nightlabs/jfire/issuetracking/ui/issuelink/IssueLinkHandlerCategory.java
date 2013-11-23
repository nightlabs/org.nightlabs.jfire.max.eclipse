/**
 * 
 */
package org.nightlabs.jfire.issuetracking.ui.issuelink;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.graphics.Image;

/**
 * @author chairat
 *
 */
public class IssueLinkHandlerCategory {

	/**
	 * categoryID
	 */
	private String categoryId;
	public String getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	/**
	 * parent category
	 */
	private IssueLinkHandlerCategory parent;
	public IssueLinkHandlerCategory getParent() {
		return parent;
	}
	public void setParent(IssueLinkHandlerCategory parent) {
		this.parent = parent;
	}

	/**
	 * name of category
	 */
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * child categories
	 */
	private List<IssueLinkHandlerCategory> childCategories = new ArrayList<IssueLinkHandlerCategory>();
	public List<IssueLinkHandlerCategory> getChildCategories() {
		return Collections.unmodifiableList(childCategories);
	}
	public void addChildCategory(IssueLinkHandlerCategory child) {
		childCategories.add(child);
	}
	public void removeChildCategory(IssueLinkHandlerCategory child) {
		childCategories.remove(child);
	}

	/**
	 * child factories
	 */
	private List<IssueLinkHandlerFactory> childFactories = new ArrayList<IssueLinkHandlerFactory>();
	public List<IssueLinkHandlerFactory> getChildFactories() {
		return Collections.unmodifiableList(childFactories);
	}
	public void addChildFactory(IssueLinkHandlerFactory child) {
		childFactories.add(child);
	}
	public void removeChildFactory(IssueLinkHandlerFactory child) {
		childFactories.remove(child);
	}

	/**
	 * parent category
	 */
	private String parentCategoryId;
	public String getParentCategoryId() {
		return parentCategoryId;
	}
	public void setParentCategoryId(String parentCategoryId) {
		this.parentCategoryId = parentCategoryId;
	}

	/**
	 * image
	 */
	private Image image;
	public Image getImage() {
		return image;
	}
	public void setImage(Image image) {
		this.image = image;
	}
	
	/**
	 * Gets all child categories and factories	
	 * @return
	 */
	public List<Object> getChildObjects() {
		List<Object> objs = new ArrayList<Object>();
		objs.addAll(childCategories);
		objs.addAll(childFactories);
		return objs;
	}
}
