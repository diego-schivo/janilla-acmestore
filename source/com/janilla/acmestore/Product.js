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
class Product {

	selector;

	listen = () => {
		const e = this.selector();
		e.querySelectorAll('button[name]').forEach(f => f.addEventListener('click', this.handleOptionClick));
		e.querySelector('.add-to-cart').addEventListener('submit', this.handleAddToCartSubmit);
	}

	handleOptionClick = async e => {
		const b = e.currentTarget;
		const u = new URL(location.href);
		u.searchParams.set(b.name, b.value);
		dispatchEvent(new CustomEvent('urlrequest', { detail: { url: u } }));
	}

	handleAddToCartSubmit = async e => {
		const f = e.currentTarget;
		e.preventDefault();
		const s = await fetch('/cart/add', {
			method: 'POST',
			body: new URLSearchParams(new FormData(f))
		});
		dispatchEvent(new CustomEvent('cartrequest', { detail: { html: await s.text() } }));
	}
}

export default Product;
