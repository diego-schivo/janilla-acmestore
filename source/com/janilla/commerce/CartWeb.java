/*
 * MIT License
 *
 * Copyright (c) 2024 Diego Schivo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.janilla.commerce;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.janilla.persistence.Persistence;
import com.janilla.reflect.Parameter;
import com.janilla.web.Handle;

public class CartWeb {

	private Persistence persistence;

	public void setPersistence(Persistence persistence) {
		this.persistence = persistence;
	}

	@Handle(method = "POST", path = "/cart/add")
	public CartModal addItem(@Parameter(name = "variant") long variant, CommerceApp.Exchange exchange)
			throws IOException {
		var v = persistence.getCrud(ProductVariant.class).read(variant);
		if (v == null)
			throw new RuntimeException();
		var c = exchange.getCart(true);
		var ii = getItems(c);
		CartItem i = ii.stream().filter(x -> x.getVariant() == v.getId()).findFirst().orElse(null);
		var z = i == null;
		if (z) {
			i = new CartItem();
			i.setCart(c.getId());
			var p = persistence.getCrud(Product.class).read(v.getProduct());
			i.setTitle1(p.getTitle());
			i.setTitle2(v.getTitle());
			i.setImage(p.getFeaturedImage());
			i.setHandle(p.getHandle());
			i.setSelectedOptions(v.getSelectedOptions());
			i.setVariant(v.getId());
			persistence.getCrud(CartItem.class).create(i);
		}
		i = persistence.getCrud(CartItem.class).update(i.getId(), x -> {
			x.setQuantity(x.getQuantity() + 1);
			x.setTotalAmount(v.getPrice().multiply(BigDecimal.valueOf(x.getQuantity())));
		});
		if (z)
			ii.add(i);
		c = update(c, ii);
		return new CartModal(c, ii);
	}

	@Handle(method = "POST", path = "/cart/remove")
	public CartModal removeItem(@Parameter(name = "variant") long variant, CommerceApp.Exchange exchange)
			throws IOException {
		var v = persistence.getCrud(ProductVariant.class).read(variant);
		if (v == null)
			throw new RuntimeException();
		var c = exchange.getCart(true);
		var ii = getItems(c);
		var j = IntStream.range(0, ii.size()).filter(x -> ii.get(x).getVariant() == v.getId()).findFirst().getAsInt();
		persistence.getCrud(CartItem.class).delete(ii.get(j).getId());
		ii.remove(j);
		c = update(c, ii);
		return new CartModal(c, ii);
	}

	@Handle(method = "POST", path = "/cart/set")
	public CartModal setItem(@Parameter(name = "variant") long variant, @Parameter(name = "quantity") int quantity,
			CommerceApp.Exchange exchange) throws IOException {
		var v = persistence.getCrud(ProductVariant.class).read(variant);
		if (v == null)
			throw new RuntimeException();
		var c = exchange.getCart(true);
		var ii = getItems(c);
		var j = IntStream.range(0, ii.size()).filter(x -> ii.get(x).getVariant() == v.getId()).findFirst().getAsInt();
		var i = persistence.getCrud(CartItem.class).update(ii.get(j).getId(), x -> {
			x.setQuantity(quantity);
			x.setTotalAmount(v.getPrice().multiply(BigDecimal.valueOf(x.getQuantity())));
		});
		ii.set(j, i);
		c = update(c, ii);
		return new CartModal(c, ii);
	}

//	protected Cart getCart(HttpExchange exchange, boolean create) throws IOException {
//		Cart c;
//		{
//			var hh = exchange.getRequest().getHeaders();
//			var h = hh != null ? hh.get("Cookie") : null;
//			var cc = h != null ? Http.parseCookieHeader(h) : null;
//			var s = cc != null ? cc.get("cart") : null;
//			var i = s != null ? Long.parseLong(s) : 0;
//			c = i > 0 ? persistence.getCrud(Cart.class).read(i) : null;
//		}
//		if (c == null) {
//			c = new Cart();
//			c.setSubtotalAmount(BigDecimal.ZERO);
//			c.setTotalTaxAmount(BigDecimal.ZERO);
//			c.setTotalAmount(BigDecimal.ZERO);
//			persistence.getCrud(Cart.class).create(c);
//			exchange.getResponse().getHeaders().add("Set-Cookie",
//					Http.formatSetCookieHeader("cart", String.valueOf(c.getId()), null, "/", "strict"));
//		}
//		return c;
//	}

	protected List<CartItem> getItems(Cart cart) throws IOException {
		var ii = persistence.getCrud(CartItem.class).filter("cart", cart.getId());
		return persistence.getCrud(CartItem.class).read(ii).collect(Collectors.toCollection(ArrayList::new));
	}

	protected Cart update(Cart cart, List<CartItem> items) throws IOException {
		return persistence.getCrud(Cart.class).update(cart.getId(), x -> {
			x.setTotalQuantity(items.stream().mapToInt(CartItem::getQuantity).sum());
			x.setSubtotalAmount(items.stream().map(CartItem::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
			x.setTotalTaxAmount(BigDecimal.ZERO);
			x.setTotalAmount(x.getSubtotalAmount().add(x.getTotalTaxAmount()));
		});
	}
}
