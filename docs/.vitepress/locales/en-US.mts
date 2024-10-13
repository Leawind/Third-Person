import { buildSidebar } from "../builders.mts";

const lang = 'en-US';

export default {
	label: 'English',
	lang: lang,
	title: "Leawind's Third Person",
	titleTemplate: ":title | Leawind's Third Person",
	description: "Documentation of minecraft mod Leawind's Thrid Person",
	themeConfig: {
		nav: [
			{ text: '💬Q&A', link: `https://github.com/Leawind/Third-Person/discussions/categories/q-a` },
			{ text: '🔬Details', link: `/${lang}/Details/` },
			{ text: '⚔️Compatibility', link: `/${lang}/compatibility` },
			{ text: '🕗Changelog', link: `/${lang}/changelog` },
			{ text: '💰Donate', link: `/${lang}/donate` },
		],
		sidebar: {
			'/en-US/Details': buildSidebar(`/${lang}/Details`),
		},
		footer: {
			copyright: 'Copyright © 2024 Leawind',
		},
		editLink: {
			pattern: 'https://github.com/Leawind/Third-Person/edit/gh-pages/docs/:path',
			text: 'Edit this page on Github',
		},
		lastUpdated: { text: "Last updated", },
	},
};
