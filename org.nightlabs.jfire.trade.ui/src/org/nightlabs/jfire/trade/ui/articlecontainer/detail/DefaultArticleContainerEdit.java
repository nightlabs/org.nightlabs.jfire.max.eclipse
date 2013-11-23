/**
 * 
 */
package org.nightlabs.jfire.trade.ui.articlecontainer.detail;

import java.util.Collection;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.IArticleContainerEditActionContributor;

/**
 * This is the default implementation of {@link ArticleContainerEdit}.
 * It uses {@link ArticleContainerEditComposite} or a sub-class and delegates all 
 * work to it. 
 * <p>
 * The creation of the {@link ArticleContainerEditComposite} is done in 
 * {@link #createArticleContainerEditComposite(Composite, ArticleContainerID)} and might
 * be overridden when a sub-class of the composite should be used.
 * </p>
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class DefaultArticleContainerEdit implements ArticleContainerEdit {

	/**
	 * This factory creates instances of {@link DefaultArticleContainerEdit}.
	 */
	public static class Factory implements ArticleContainerEditFactory {
		@Override
		public ArticleContainerEdit createArticleContainerEdit() {
			return new DefaultArticleContainerEdit();
		}
	}
	
	private ArticleContainerID articleContainerID;
	private ArticleContainerEditComposite articleContainerEditComposite;
	
	/**
	 * Create a new {@link DefaultArticleContainerEdit}.  
	 */
	public DefaultArticleContainerEdit() {
	}
	
	@Override
	public void init(ArticleContainerID articleContainerID) {
		this.articleContainerID = articleContainerID;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This implementation will call {@link #createArticleContainerEditComposite(Composite, ArticleContainerID)}
	 * and treat return its result. 
	 * </p>
	 */
	@Override
	public Composite createComposite(Composite parent) {
		if (articleContainerID == null)
			throw new IllegalStateException("createComposite() called before init(). Call init() first to set the articleContainerID."); //$NON-NLS-1$
		articleContainerEditComposite = createArticleContainerEditComposite(parent, articleContainerID);
		return articleContainerEditComposite;
	}
	
	/**
	 * This method creates the {@link ArticleContainerEditComposite} this edit delegates
	 * all work to. It might be overridden but a functional sub-class of {@link ArticleContainerEditComposite}
	 * should be returned then ;-)
	 * 
	 * @param parent The parent to use.
	 * @param articleContainerID The {@link ArticleContainerID} this edit was intialized with.
	 * @return The newly created {@link ArticleContainerEditComposite}.
	 */
	protected ArticleContainerEditComposite createArticleContainerEditComposite(Composite parent, ArticleContainerID articleContainerID) {
		return new ArticleContainerEditComposite(parent, articleContainerID);
	}
	
	@Override
	public ArticleContainerEditComposite getComposite() {
		if (articleContainerEditComposite == null)
			throw new IllegalStateException("getComposite() called before createComposite(). Note that the methods of " + this.getClass().getSimpleName() + " only will work if its composite was created."); //$NON-NLS-1$ //$NON-NLS-2$
		return articleContainerEditComposite;
	}
	
	@Override
	public void addActiveSegmentEditSelectionListener(ActiveSegmentEditSelectionListener listener) {
		getComposite().addActiveSegmentEditSelectionListener(listener);
	}

	@Override
	public SegmentEdit getActiveSegmentEdit() {
		return getComposite().getActiveSegmentEdit();
	}

	@Override
	public ArticleContainer getArticleContainer() {
		return getComposite().getArticleContainer();
	}

	@Override
	public ArticleContainerID getArticleContainerID() {
		return getComposite().getArticleContainerID();
	}

	@Override
	public Collection<Article> getArticles() {
		return getComposite().getArticles();
	}

	@Override
	public ClientArticleSegmentGroupSet getArticleSegmentGroupSet() {
		return getComposite().getArticleSegmentGroupSet();
	}

	@Override
	public void removeActiveSegmentEditSelectionListener(ActiveSegmentEditSelectionListener listener) {
		getComposite().removeActiveSegmentEditSelectionListener(listener);
	}

	@Override
	public void setArticleContainerEditActionContributor(IArticleContainerEditActionContributor actionContributor) {
		getComposite().setArticleContainerEditActionContributor(actionContributor);
	}

	@Override
	public void addArticleChangeListener(ArticleChangeListener articleChangeListener) {
		getComposite().addArticleChangeListener(articleChangeListener);
	}

	@Override
	public void addArticleCreateListener(ArticleCreateListener articleCreateListener) {
		getComposite().addArticleCreateListener(articleCreateListener);
	}

	@Override
	public void removeArticleChangeListener(ArticleChangeListener articleChangeListener) {
		getComposite().removeArticleChangeListener(articleChangeListener);
	}

	@Override
	public void removeArticleCreateListener(ArticleCreateListener articleCreateListener) {
		getComposite().removeArticleCreateListener(articleCreateListener);
	}

	@Override
	public Collection<SegmentEdit> getSegmentEdits() {
		return getComposite().getSegmentEdits();
	}

	@Override
	public IArticleContainerEditActionContributor getArticleContainerEditActionContributor() {
		return getComposite().getArticleContainerEditActionContributor();
	}

	@Override
	public void setShowHeader(boolean showHeader) {
		getComposite().setShowHeader(showHeader);
	}
}
