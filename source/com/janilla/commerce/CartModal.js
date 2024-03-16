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
class CartModal {

	selector;

	listen = () => {
		addEventListener('cartrequest', this.handleCartRequest);
		const e = this.selector();
		e.querySelector('.close').addEventListener('click', this.handleCloseClick);
		e.querySelectorAll('.delete-div form').forEach(x => x.addEventListener('submit', this.handleDeleteSubmit));
		e.querySelectorAll('.quantity-div form').forEach(x => x.addEventListener('submit', this.handleQuantitySubmit));
	}
	
	handleCartRequest = e => {
		if (e.detail) {
			this.selector().outerHTML = e.detail.html;
			const q = [...this.selector().querySelectorAll('.quantity-div span')].map(x => parseInt(x.innerText, 10)).reduce((a, b) => a + b, 0);
			document.querySelector('.cart-div [data-quantity]').setAttribute('data-quantity', q);
			this.listen();
		}
		document.documentElement.style.overflow = 'hidden';
		this.selector().style.display = '';
	}

	handleCloseClick = () => {
		this.selector().style.display = 'none';
		document.documentElement.style.overflow = '';
	}

	handleDeleteSubmit = async e => {
		const f = e.currentTarget;
		e.preventDefault();
		const s = await fetch('/cart/remove', {
			method: 'POST',
			body: new URLSearchParams(new FormData(f))
		});
		dispatchEvent(new CustomEvent('cartrequest', { detail: { html: await s.text() } }));
	}

	handleQuantitySubmit = async e => {
		const f = e.currentTarget;
		e.preventDefault();
		const s = await fetch('/cart/set', {
			method: 'POST',
			body: new URLSearchParams(new FormData(f))
		});
		dispatchEvent(new CustomEvent('cartrequest', { detail: { html: await s.text() } }));
	}
}

export default CartModal;
