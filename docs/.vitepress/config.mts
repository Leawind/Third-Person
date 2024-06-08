import { defineConfig } from 'vitepress';
import locales from './locales.mts';

const GOOGLE_ANALYTICS_ID = 'G-TS4SMQ5GZY';

export default defineConfig({
	base: '/Third-Person/',
	srcDir: '.',
	cleanUrls: true,
	router: {
		prefetchLinks: true,
	},
	head: [
		[
			'script',
			{
				async: true,
				src: `https://www.googletagmanager.com/gtag/js?id=${GOOGLE_ANALYTICS_ID}`,
			},
		],
		[
			'script',
			{},
			`window.dataLayer = window.dataLayer || [];\nfunction gtag(){dataLayer.push(arguments);}\ngtag('js', new Date());\ngtag('config', '${GOOGLE_ANALYTICS_ID}');`,
		],
		// ['link', { rel: 'icon', href: '/favicon.ico' }]
	],
	themeConfig: {
		logo: '/logo.svg',
		externalLinkIcon: true,
		socialLinks: [
			{ link: 'https://github.com/LEAWIND/Third-Person', icon: 'github' },
		],
		search: {
			provider: 'local',
			options: {
				miniSearch: {
					options: {
						processTerm: term => {
							term = term.toLowerCase()
								.replace(/([\u4e00-\u9fff])/g, '$1 ')
								.trim().replace(/\s+/g, ' ');
							const terms = term.split(' ');
							return terms.length === 1 ? term : terms;
						},
					},
					searchOptions: {
					},
				},
			},
		},
	},
	locales: await locales(),
});

