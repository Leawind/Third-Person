import { buildSidebar } from "../builders.mts";

const lang = 'zh-CN';

export default {
	label: '简体中文',
	lang: lang,
	title: "Leawind的第三人称",
	titleTemplate: ":title | Leawind的第三人称",
	description: "一个Minecraft模组 Leawind的第三人称 的文档",
	themeConfig: {
		nav: [
			{ text: '🔬详细', link: `/${lang}/Details/` },
			{ text: '⚔️兼容性', link: `/${lang}/compatibility` },
			{ text: '🕗更新日志', link: `/${lang}/changelog` },
			{ text: '徽章', link: `/${lang}/badges` },
			{ text: '💬疑问', link: `https://github.com/Leawind/Third-Person/discussions/categories/q-a` },
			{ text: '💰捐赠', link: 'https://leawind.github.io/donate' },
		],
		sidebar: {
			'/zh-CN/Details': buildSidebar(`/${lang}/Details`),
		},
		footer: {
			copyright: 'Copyright © 2024 Leawind',
		},
		editLink: {
			pattern: 'https://github.com/Leawind/Third-Person/edit/gh-pages/docs/:path',
			text: '在 Github 上编辑此页',
		},
		lastUpdated: { text: "上次更新", },
	},
};
