// .vitepress/theme/index.ts
import { h } from 'vue';
import type { Theme } from 'vitepress';
import DefaultTheme from 'vitepress/theme';
import TopLayout from './TopLayout.vue';
export default {
	extends: DefaultTheme,
	Layout: () => h(DefaultTheme.Layout, null, {
		"layout-top": () => h(TopLayout),
	}),
} satisfies Theme;
