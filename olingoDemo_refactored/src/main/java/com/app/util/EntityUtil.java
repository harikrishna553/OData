package com.app.util;

import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.edm.EdmEntitySet;

import com.app.service.DemoEdmProvider;

/**
 * Responsible to perform CRUD operations on entities.
 * 
 * @author Krishna
 *
 */
public class EntityUtil {

	public static EntityCollection getData(EdmEntitySet edmEntitySet) {
		if (DemoEdmProvider.ES_PRODUCTS_NAME.equals(edmEntitySet.getName())) {
			return ProductUtil.getProductsData();
		}

		return null;
	}
}
