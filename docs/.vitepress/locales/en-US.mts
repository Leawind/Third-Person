import { buildSidebar } from "../builders.mts";

const lang = 'en-US';

export default {
	label: 'English',
	lang: lang,
	title: "Leawind's Third Person",
	titleTemplate: ":title | Leawind's Third Person",
	description: "Documentation for minecraft mod Leawind's Thrid Person",
	themeConfig: {
		nav: [
			{ text: '🕗Changelog', link: `/${lang}/Changelog/` },
			{ text: '👁Features', link: `/${lang}/Features/` },
			{ text: '💬Q&A', link: `/${lang}/Q&A/` },
			{ text: 'Misc', link: `/${lang}/Misc/` },
			{ text: '💰Donate', link: 'https://leawind.github.io/en/donate' },
		],
		sidebar: {
			'/en-US/Changelog': buildSidebar(`${lang}/Changelog`),
			'/en-US/Features': buildSidebar(`/${lang}/Features`),
			'/en-US/Q&A': buildSidebar(`/${lang}/Q&A`),
			'/en-US/Misc': buildSidebar(`/${lang}/Misc`),
		},
		footer: {
			copyright: 'Copyright © 2024 Leawind',
		},
		editLink: {
			pattern: 'https://github.com/LEAWIND/Third-Person/edit/gh-pages/docs/:path',
			text: 'Edit this page on Github',
		},
		lastUpdated: { text: "Last updated", },
	},
};
