/**
 * 
 */
package org.nightlabs.jfire.trade.admin.editor;

import org.nightlabs.jfire.store.ProductType;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ProductTypeSaleAccessStatus {

	private boolean published;
	private boolean confirmed;
	private boolean saleable;
	private boolean closed;
	
	/**
	 * 
	 */
	public ProductTypeSaleAccessStatus() {
	}

	/**
	 * Create a sale access status for the given ProductType.
	 */
	public ProductTypeSaleAccessStatus(ProductType productType) {
		setPublished(productType.isPublished());
		setConfirmed(productType.isConfirmed());
		setSaleable(productType.isSaleable());
		setClosed(productType.isClosed());
	}
	
	/**
	 * Create a sale access status status with the given values 
	 */
	public ProductTypeSaleAccessStatus(
			boolean published,
			boolean confirmed,
			boolean saleable,
			boolean closed
	) {
		setPublished(published);
		setConfirmed(confirmed);
		setSaleable(saleable);
		setClosed(closed);
	}
	
	/**
	 * Returns the closed of this ProductTypeSaleAccessStatus.
	 * @return the closed.
	 */
	public boolean isClosed() {
		return closed;
	}

	/**
	 * Sets the closed of this ProductTypeSaleAccessStatus.
	 * @param closed the closed to set.
	 */
	public void setClosed(boolean closed) {
		this.closed = closed;
	}

	/**
	 * Returns the confirmed of this ProductTypeSaleAccessStatus.
	 * @return the confirmed.
	 */
	public boolean isConfirmed() {
		return confirmed;
	}

	/**
	 * Sets the confirmed of this ProductTypeSaleAccessStatus.
	 * @param confirmed the confirmed to set.
	 */
	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}

	/**
	 * Returns the published of this ProductTypeSaleAccessStatus.
	 * @return the published.
	 */
	public boolean isPublished() {
		return published;
	}

	/**
	 * Sets the published of this ProductTypeSaleAccessStatus.
	 * @param published the published to set.
	 */
	public void setPublished(boolean published) {
		this.published = published;
	}

	/**
	 * Returns the saleable of this ProductTypeSaleAccessStatus.
	 * @return the saleable.
	 */
	public boolean isSaleable() {
		return saleable;
	}

	/**
	 * Sets the saleable of this ProductTypeSaleAccessStatus.
	 * @param saleable the saleable to set.
	 */
	public void setSaleable(boolean saleable) {
		this.saleable = saleable;
	}
}
