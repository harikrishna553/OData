package com.app.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;

import com.app.model.Product;

public class ProductUtil {
	private static List<Product> products = new ArrayList<>();

	static {
		Product prod1 = new Product(1, "Washing Machine by AVC", "Good for morth east countries", 876544);
		Product prod2 = new Product(2, "XYZ Camera", "Catch pics while travelling", 965987);

		products.add(prod1);
		products.add(prod2);
	}

	private static Entity getProductEntity(final Product product) {
		Entity entity = new Entity().addProperty(new Property(null, "ID", ValueType.PRIMITIVE, product.getId()))
				.addProperty(new Property(null, "Name", ValueType.PRIMITIVE, product.getName()))
				.addProperty(new Property(null, "Description", ValueType.PRIMITIVE, product.getDescription()))
				.addProperty(new Property(null, "Price", ValueType.PRIMITIVE, product.getPrice()));
		entity.setId(createId("Products", product.getId()));
		return entity;
	}

	public static EntityCollection getProductsData() {
		EntityCollection dataToSend = new EntityCollection();
		List<Entity> productList = dataToSend.getEntities();

		for (Product prod : products) {
			productList.add(getProductEntity(prod));
		}

		return dataToSend;
	}

	private static URI createId(final String entitySetName, final Object id) {
		try {
			return new URI(entitySetName + "(" + String.valueOf(id) + ")");
		} catch (URISyntaxException e) {
			throw new ODataRuntimeException("Unable to create id for entity: " + entitySetName, e);
		}
	}
}
