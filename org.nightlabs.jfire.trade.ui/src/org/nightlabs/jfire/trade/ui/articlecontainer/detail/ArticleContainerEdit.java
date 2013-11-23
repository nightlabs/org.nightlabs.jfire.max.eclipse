package org.nightlabs.jfire.trade.ui.articlecontainer.detail;

import java.util.Collection;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.IArticleContainerEditActionContributor;

/**
 * {@link ArticleContainerEdit}s are used to edit an {@link ArticleContainer}.
 * Implementations of this interface (or better the factories that create these: {@link ArticleContainerEditFactory})
 * can be registered as extensions to the point <code>org.nightlabs.jfire.trade.ui.articleContainerEditFactory</code>.
 * <p> 
 * The registration includes the class-name of the ArticleContainer the edit can handle,
 * so you can have custom edits for sublcasses of the {@link ArticleContainer}s known to the
 * JFire base. Note that the registry that process this extensions applies inheritance resolving
 * when searching the edit for a particular {@link ArticleContainer}. {@link DefaultArticleContainerEdit}
 * uses this as it is registered on the interface {@link ArticleContainer} directly and
 * will therefore be the fallback for all article containers. 
 * </p>
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public interface ArticleContainerEdit
{
	/**
	 * This method is called to initialize the edit with an {@link ArticleContainer}.
	 * The edit is itself responsible to query the {@link ArticleContainer} from the
	 * server and detach it correctly to be able to implement the other methods.  
	 * 
	 * @param articleContainerID The id of the {@link ArticleContainer} this edit is for.
	 */
	void init(ArticleContainerID articleContainerID);
	
	/**
	 * Returns the {@link ArticleContainer} this edit was opened for.
	 * Note, that you should not attempt to access the {@link Article}s
	 * using getArticleContainer().getArticles() as the container is
	 * not detached with the appropriate fetch-groups. Rather use
	 * {@link #getArticles()}.
	 *  
	 * @return The {@link ArticleContainer} this edit was opened for.
	 */
	ArticleContainer getArticleContainer();
	
	/**
	 * This is a convenience method for
	 * <pre>
	 * JDOHelper.getObjectId(getArticleContainer())
	 * </pre>
	 * 
	 * @return The id-object of the {@link ArticleContainer} this edit was opened for.
	 */
	ArticleContainerID getArticleContainerID();
	
	/**
	 * Returns the {@link Article}s of the {@link ArticleContainer} of this edit.
	 * Note that these should always be accessed using this method and not via
	 * getArticleContainer().getArticles() as the {@link ArticleContainer}
	 * is not detached with the articles collection. The {@link Article}s are
	 * managed independently.
	 * 
	 * @return The {@link Article}s of the {@link ArticleContainer} of this edit.
	 */
	Collection<Article> getArticles();

	/**
	 * Create the composite of this {@link ArticleEdit}
	 * 
	 * @param parent The parent to use.
	 * @return The newly create composite.
	 */
	Composite createComposite(Composite parent);
	
	/**
	 * This method returns the {@link Control} that is the parent for all ui
	 * that is used to edit the {@link Article}s of the current {@link ArticleContainer}
	 * of this edit. Note that this is not necessarily the top Control of the edit.
	 *  
	 * @return The ui that is used to edit the current {@link ArticleContainer}s articles.
	 */
	Composite getComposite();

	/**
	 * @return The {@link ClientArticleSegmentGroupSet} that was created to track 
	 *         changes to the current {@link ArticleContainer}.
	 */
	ClientArticleSegmentGroupSet getArticleSegmentGroupSet();

	/**
	 * Add the given {@link ActiveSegmentEditSelectionListener} that will be notified
	 * when the user changes the segment selection. 
	 * @param listener The listener to add.
	 */
	void addActiveSegmentEditSelectionListener(ActiveSegmentEditSelectionListener listener);

	/**
	 * Remove the given {@link ActiveSegmentEditSelectionListener}.
	 * @param listener The listener to remove.
	 */
	void removeActiveSegmentEditSelectionListener(ActiveSegmentEditSelectionListener listener);
	
	void addArticleChangeListener(ArticleChangeListener articleChangeListener);
	void removeArticleChangeListener(ArticleChangeListener articleChangeListener);
	
	void addArticleCreateListener(ArticleCreateListener articleCreateListener);
	void removeArticleCreateListener(ArticleCreateListener articleCreateListener);
	
	SegmentEdit getActiveSegmentEdit();
	Collection<SegmentEdit> getSegmentEdits();
	
	void setArticleContainerEditActionContributor(IArticleContainerEditActionContributor actionContributor);
	IArticleContainerEditActionContributor getArticleContainerEditActionContributor();
	
	void setShowHeader(boolean showHeader);
}
