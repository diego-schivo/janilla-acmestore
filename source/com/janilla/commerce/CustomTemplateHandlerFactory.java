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
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.janilla.commerce.Navbar.MenuItem;
import com.janilla.frontend.RenderEngine;
import com.janilla.frontend.RenderEngine.ObjectAndType;
import com.janilla.http.HttpExchange;
import com.janilla.persistence.Persistence;
import com.janilla.web.TemplateHandlerFactory;

public class CustomTemplateHandlerFactory extends TemplateHandlerFactory {

	private Persistence persistence;

	public void setPersistence(Persistence persistence) {
		this.persistence = persistence;
	}

	@Override
	protected void render(ObjectAndType input, HttpExchange exchange) throws IOException {
		var a = exchange.getRequest().getHeaders().get("Accept");
		if (!a.equals("*/*")) {
			var d = input.getValue();
			var c = ((CommerceApp.Exchange) exchange).getCart(false);
			CartModal m;
			{
//				var hh = exchange.getRequest().getHeaders();
//				var h = hh != null ? hh.get("Cookie") : null;
//				var cc = h != null ? Http.parseCookieHeader(h) : null;
//				var s = cc != null ? cc.get("cart") : null;
//				var i = s != null ? Long.parseLong(s) : 0;
//				var c = i > 0 ? persistence.getCrud(Cart.class).read(i) : null;
				var jj = c != null ? persistence.getCrud(CartItem.class).filter("cart", c.getId()) : null;
				var ii = jj != null
						? persistence.getCrud(CartItem.class).read(jj).collect(Collectors.toCollection(ArrayList::new))
						: null;
				m = new CartModal(c, ii);
			}
			var n = new Navbar(
					List.of(new MenuItem("All", URI.create("/search")),
							new MenuItem("Shirts", URI.create("/search/shirts")),
							new MenuItem("Stickers", URI.create("/search/stickers"))),
					c != null ? c.getTotalQuantity() : 0);
			var l = new Layout(n, d, m);
			input = new ObjectAndType(null, l, null);
		}
		super.render(input, exchange);
	}

	@Override
	protected RenderEngine newRenderEngine() {
		return new CustomRenderEngine();
	}
}
