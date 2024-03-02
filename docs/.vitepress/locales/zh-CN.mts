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
			{ text: '🕗更新日志', link: `/${lang}/changelog` },
			{ text: '👁详细特性', link: `/${lang}/Features/` },
			{ text: '💬Q&A', link: `/${lang}/Q&A/` },
			{ text: '杂项', link: `/${lang}/Misc/` },
			{ text: '💰捐赠', link: 'https://leawind.github.io/zh-CN/donate' },
		],
		sidebar: {
			'/zh-CN/Features': buildSidebar(`/${lang}/Features`),
			'/zh-CN/Q&A': buildSidebar(`/${lang}/Q&A`),
			'/zh-CN/Misc': buildSidebar(`/${lang}/Misc`),
		},
		footer: {
			copyright: 'Copyright © 2024 Leawind',
		},
		editLink: {
			pattern: 'https://github.com/LEAWIND/Third-Person/edit/Documentation/docs/:path',
			text: '在 Github 上编辑此页',
		},
		lastUpdated: { text: "上次更新", },
	},
};
