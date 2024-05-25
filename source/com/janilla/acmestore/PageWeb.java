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
import java.util.regex.Pattern;

import com.janilla.persistence.Persistence;
import com.janilla.web.Handle;
import com.janilla.web.Render;

public class PageWeb {

	private Persistence persistence;

	public void setPersistence(Persistence persistence) {
		this.persistence = persistence;
	}

	@Handle(method = "GET", path = "/([a-z-]+)")
	public Page gePage(String handle) throws IOException {
		var c = persistence.crud(com.janilla.acmestore.Page.class);
		var i = c.find("handle", handle);
		var p = c.read(i);
		return new Page(p);
	}

	protected static Pattern HTML_TAG = Pattern.compile("<.*?>");

	protected static Pattern NEWLINE_SPACE = Pattern.compile("\\s*\\n\\s*");

	@Render("Page.html")
	public record Page(com.janilla.acmestore.Page page) implements Layout.Page {

		@Override
		public SEO getSEO() {
			var s = page.getSEO();
			var t = s != null ? s.title() : null;
			if (t == null || t.isEmpty())
				t = page.getTitle();
			t = t + " | Acme Store";

			var d = s != null ? s.description() : null;
			if (d == null || d.isEmpty())
				d = page.getBody();
			if (d != null) {
				d = HTML_TAG.matcher(d).replaceAll("");
				d = NEWLINE_SPACE.matcher(d).replaceAll(" ").trim();
				if (d.length() > 150)
					d = d.substring(0, 147) + "...";
			}
			return new SEO(t, d);
		}
	}
}
