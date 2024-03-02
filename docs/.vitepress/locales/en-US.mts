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
			{ text: 'Donate', link: 'https://leawind.github.io/en/donate' },
		],
		sidebar: {
			'/en-US/Q&A': buildSidebar(`/${lang}/Q&A`),
		},
		footer: {
			copyright: 'Copyright © 2024 Leawind',
		},
		editLink: {
			pattern: 'https://github.com/LEAWIND/Third-Person-Docs/edit/main/docs/:path',
			text: 'Edit this page on Github',
		},
		lastUpdated: { text: "Last updated", },
	},
};
