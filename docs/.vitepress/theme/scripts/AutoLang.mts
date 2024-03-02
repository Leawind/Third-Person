import { langs } from '../../locales.mts';

/**
 * 在url后指定autolang参数后，会自动跳转到当前浏览器语言
 */
async function AutoLang() {
	const url = getURL();
	const bp = new BasedPath(url.pathname, getBase());
	const expectedLanguage = getExpectedLanguage();
	const autolang: boolean = url.searchParams.has('autolang');

	if (!bp.isLangSpecified) {
		jump();
	} else if (autolang && (bp.lang !== expectedLanguage)) {
		jump();
	}

	function jump() {
		bp.lang = expectedLanguage;
		const newUrl = getURL();
		newUrl.searchParams.delete('autolang');
		newUrl.pathname = bp.toString();
		location.replace(newUrl.toString());
	}
};

export default AutoLang;


class BasedPath {
	public base: string = '';
	public lang: string = '';
	public path: string = '';
	public suffix: string = '';
	public isLangSpecified = true;
	public constructor(path: string, base: string) {
		this.suffix = path.endsWith('/') ? '/' : '';
		this.base = base;
		path = path.replace(new RegExp(`^/${base}`), '');
		this.lang = path
			.replace(/^\/*/g, '')
			.replace(/(\/.*)/g, '');
		if (isLangSupported(this.lang)) {
			this.isLangSpecified = true;
			this.path = path.replace(new RegExp(`^/${this.lang}`), '');
		} else {
			this.isLangSpecified = false;
			this.lang = langs[0];
			this.path = path;
		}
	}
	public toString() {
		return pathjoin('/', this.base, this.lang, this.path + this.suffix);
	}
};


function getURL(): URL {
	return new URL(location.href);
}

function isLangSupported(lang: string): boolean {
	return langs.indexOf(lang) !== -1;
}

function getBase() {
	return globalThis.useData().site.getter().base.replace(/\//g, '');
}

function pathjoin(...paths) {
	return paths.join('/').replace(/\/{2,}/g, '/');
}

function getExpectedLanguage() {
	return isLangSupported(navigator.language) ? navigator.language : langs[0];
}
