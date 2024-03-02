const langs = [
	'en-US',
	'zh-CN',
];

/**
 * Load locale configs
 */
const locales = async () => {
	const locals = {};
	for (const lang of langs)
		locals[lang] = (await import(`./locales/${lang}.mts`)).default;
	return locals as LocaleConfig;
};

import { LocaleConfig } from "vitepress";

export { langs };
export default locales;
