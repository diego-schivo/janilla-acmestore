package com.janilla.commerce;

import java.io.IOException;

import com.janilla.frontend.RenderEngine;
import com.janilla.frontend.Renderer;
import com.janilla.web.Handle;
import com.janilla.web.Render;

public class PageWeb {

	@Handle(method = "GET", path = "/(about|terms-conditions|shipping-return-policy|privacy-policy|frequently-asked-questions)")
	public Page gePage(String handle) throws IOException {
		return new Page(handle + ".html");
	}

	@Render
	public record Page(String template) implements com.janilla.commerce.Page, Renderer {

		@Override
		public String description() {
			return "";
		}

		@Override
		public boolean evaluate(RenderEngine engine) {
			record A(Page page) {
			}
			return engine.match(A.class, (i, o) -> {
				o.setTemplate(template);
			});
		}
	}
}
