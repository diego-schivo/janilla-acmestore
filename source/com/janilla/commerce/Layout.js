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
import CartModal from './CartModal.js';
import Home from './Home.js';
import Navbar from './Navbar.js';
import Product from './Product.js';
import Search from './Search.js';

class Layout {

	navbar;

	page;
	
	cartModal;
	
	constructor() {
		this.navbar = new Navbar();
		this.navbar.selector = () => document.body.firstElementChild;
		this.cartModal = new CartModal();
		this.cartModal.selector = () => document.body.lastElementChild;
	}

	listen = () => {
		addEventListener('urlrequest', this.handleUrlRequest);
		document.querySelectorAll('a').forEach(x => x.addEventListener('click', this.handleLinkClick));
		this.navbar.listen();
		this.page?.listen();
		this.cartModal?.listen();
	}
	
	handleUrlRequest = async e => {
		const u = e.detail.url;
		history.pushState({}, '', u);
		const f = document.querySelector('main');
		f.innerHTML = '';
		const s = await fetch(u);
		f.innerHTML = await s.text();

		const p = u instanceof URL ? u.pathname : u.toString();
		if (p === '/')
			this.page = new Home();
		else if (p.startsWith('/product/'))
			this.page = new Product();
		else if (p === '/search' || p.startsWith('/search/'))
			this.page = new Search();
		this.page.selector = () => document.querySelector('main');

		this.listen();
	}

	handleLinkClick = e => {
		e.preventDefault();
		const u = e.currentTarget.getAttribute('href');
		dispatchEvent(new CustomEvent('urlrequest', { detail: { url: u } }));
	}
}

export default Layout;
