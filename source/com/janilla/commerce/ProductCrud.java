package com.janilla.commerce;

import java.util.Map;
import java.util.Map.Entry;

import com.janilla.persistence.Crud;

public class ProductCrud extends Crud<Product> {

	@Override
	protected Map<String, Entry<Object, Object>> getIndexMap(Product entity, long id) {
		var m = super.getIndexMap(entity, id);
		if (entity.isHidden())
			m.keySet().removeIf(k -> k == null || !k.equals("handle"));
		return m;
	}
}
