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
package com.janilla.acmestore;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;

import com.janilla.http.Http;
import com.janilla.http.HttpExchange;
import com.janilla.http.HttpHeader;
import com.janilla.io.IO;
import com.janilla.persistence.Persistence;

public class CustomExchange extends HttpExchange {

	public Persistence persistence;

	private boolean createCart;

	private IO.Supplier<Cart> cart = IO.Lazy.of(() -> {
		Cart c;
		{
			var hh = getRequest().getHeaders();
			var h = hh != null
					? hh.stream().filter(x -> x.name().equals("Cookie")).map(HttpHeader::value).findFirst().orElse(null)
					: null;
			var cc = h != null ? Http.parseCookieHeader(h) : null;
			var s = cc != null ? cc.get("cart") : null;
			var i = s != null ? Long.parseLong(s) : 0;
			c = i > 0 ? persistence.crud(Cart.class).read(i) : null;
		}
		if (c == null && createCart) {
			c = new Cart();
			c.setSubtotalAmount(BigDecimal.ZERO);
			c.setTotalTaxAmount(BigDecimal.ZERO);
			c.setTotalAmount(BigDecimal.ZERO);
			persistence.crud(Cart.class).create(c);
			getResponse().getHeaders().add(new HttpHeader("Set-Cookie",
					Http.formatSetCookieHeader("cart", String.valueOf(c.getId()), null, "/", "strict")));
		}
		return c;
	});

	public Cart getCart(boolean create) {
		createCart = create;
		try {
			return cart.get();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
