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
import java.util.List;

import com.janilla.persistence.Persistence;
import com.janilla.web.Handle;
import com.janilla.web.Render;

public class HomeWeb {

	private Persistence persistence;

	public void setPersistence(Persistence persistence) {
		this.persistence = persistence;
	}

	@Handle(method = "GET", path = "/")
	public Page getPage() throws IOException {
		ThreeItemGrid c1;
		{
			var c = persistence.getCrud(Collection.class).find("handle", "hidden-homepage-featured-items");
			var ii = persistence.getCrud(Product.class).filter("collections", 0, 3, new Object[] { c }).ids();
			var pp = persistence.getCrud(Product.class).read(ii).toList();
			c1 = new ThreeItemGrid(pp);
		}
		Carousel c2;
		{
			var c = persistence.getCrud(Collection.class).find("handle", "hidden-homepage-carousel");
			var ii = persistence.getCrud(Product.class).filter("collections", new Object[] { c });
			var pp = persistence.getCrud(Product.class).read(ii).toList();
			c2 = new Carousel(pp);
		}
		return new Page(List.of(c1, c2));
	}

	@Render(template = "Home.html")
	public record Page(Iterable<Object> children) implements com.janilla.commerce.Page {

		@Override
		public String description() {
			return "Ecommerce store built with Janilla.";
		}
	}
}
