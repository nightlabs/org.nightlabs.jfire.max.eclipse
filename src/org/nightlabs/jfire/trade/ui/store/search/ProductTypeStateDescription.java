package org.nightlabs.jfire.trade.ui.store.search;

import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.search.ISaleAccessQuery;

/**
 * Simple data container to represent the query entry of one of the states of a ProductType, e.g.
 * {@link ProductType#isConfirmed()} which may be not enabled in the query.
 *
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class ProductTypeStateDescription
{
	private ISaleAccessQuery query;
	private String statusName;
	private String fieldName;

	/**
	 *
	 * @param query
	 * @param statusName
	 * @param fieldName
	 */
	public ProductTypeStateDescription(ISaleAccessQuery query, String statusName, String fieldName)
	{
		assert query != null;
		assert statusName != null;
		assert fieldName != null && fieldName.length() > 0;

		this.query = query;
		this.statusName = statusName;
		this.fieldName = fieldName;
	}

	/**
	 * @return the enable
	 */
	public boolean isEnabled()
	{
		return query.isFieldEnabled(fieldName);
	}

	/**
	 * @param enabled the enable to set
	 */
	public void setEnabled(boolean enabled)
	{
		query.setFieldEnabled(fieldName, enabled);
	}

	/**
	 * @return the value
	 */
	public Boolean getValue()
	{
//		final Boolean value = (Boolean) query.getFieldValue(fieldName);
//		if (value == null)
//			throw new IllegalStateException("A ProductTypeStateDescription with an unknown fieldName!");

		return (Boolean) query.getFieldValue(fieldName);
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(boolean value)
	{
		query.setFieldValue(fieldName, Boolean.valueOf(value));
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return statusName;
	}

}
