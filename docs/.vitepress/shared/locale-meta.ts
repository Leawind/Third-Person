/**
 * All locale-related definitions — the single source of truth.
 *
 * Adding a new language: just add an entry to `locales`.
 * BCP47_MAP is auto-derived — no separate bcp47.ts needed.
 */

export type NavItem =
  | { text: string; link: string }
  | { text: string; items: NavItem[] }

export interface LocaleEntry {
  lang: string
  /** BCP 47 language tag (e.g. 'zh-CN', 'en-US') */
  bcp47: string
  label: string
  title: string
  description: string
  nav: NavItem[]
  editLinkText: string
  lastUpdatedText: string
  search?: Record<string, unknown>
}

const locales: Record<string, LocaleEntry> = {
  zh_cn: {
    lang: 'zh_cn',
    bcp47: 'zh-CN',
    label: '简体中文 (中国大陆)',
    title: 'Leawind的第三人称',
    description: '一个Minecraft模组 Leawind的第三人称 的文档',
    nav: [
      { text: '详细', link: '/zh_cn/Details/' },
      { text: '兼容性', link: '/zh_cn/compatibility' },
      { text: '更新日志', link: '/zh_cn/changelog' },
      { text: '捐赠', link: '/zh_cn/donate' },
      {
        text: 'Q&A',
        link:
          'https://github.com/Leawind/Third-Person/discussions/categories/q-a',
      },
      {
        text: '下载',
        items: [
          {
            text: 'CurseForge',
            link:
              'https://www.curseforge.com/minecraft/mc-mods/leawind-third-person',
          },
          {
            text: 'Modrinth',
            link: 'https://modrinth.com/mod/leawind-third-person',
          },
          {
            text: 'Github Release',
            link: 'https://github.com/LEAWIND/Third-Person/releases',
          },
        ],
      },
    ],
    editLinkText: '在 Github 上编辑此页',
    lastUpdatedText: '上次更新',
    search: {
      translations: {
        button: {
          buttonText: '搜索',
          buttonAriaLabel: '搜索',
        },
        modal: {
          displayDetails: '显示详细列表',
          resetButtonTitle: '重置搜索',
          backButtonTitle: '关闭搜索',
          noResultsText: '没有结果',
          footer: {
            selectText: '选择',
            selectKeyAriaLabel: '输入',
            navigateText: '导航',
            navigateUpKeyAriaLabel: '上箭头',
            navigateDownKeyAriaLabel: '下箭头',
            closeText: '关闭',
            closeKeyAriaLabel: 'Esc',
          },
        },
      },
    },
  },
  en_us: {
    lang: 'en_us',
    bcp47: 'en-US',
    label: 'English (US)',
    title: "Leawind's Third Person",
    description: "Documentation of minecraft mod Leawind's Third Person",
    nav: [
      { text: 'Details', link: '/en_us/Details/' },
      { text: 'Compatibility', link: '/en_us/compatibility' },
      { text: 'Changelog', link: '/en_us/changelog' },
      { text: 'Donate', link: '/en_us/donate' },
      {
        text: 'Q&A',
        link:
          'https://github.com/Leawind/Third-Person/discussions/categories/q-a',
      },
      {
        text: 'Download',
        items: [
          {
            text: 'CurseForge',
            link:
              'https://www.curseforge.com/minecraft/mc-mods/leawind-third-person',
          },
          {
            text: 'Modrinth',
            link: 'https://modrinth.com/mod/leawind-third-person',
          },
          {
            text: 'Github Release',
            link: 'https://github.com/LEAWIND/Third-Person/releases',
          },
        ],
      },
    ],
    editLinkText: 'Edit this page on Github',
    lastUpdatedText: 'Last updated',
    search: {
      translations: {
        button: {
          buttonText: 'Search',
          buttonAriaLabel: 'Search',
        },
        modal: {
          displayDetails: 'Display detailed list',
          resetButtonTitle: 'Reset search',
          backButtonTitle: 'Close search',
          noResultsText: 'No results',
          footer: {
            selectText: 'Select',
            selectKeyAriaLabel: 'Enter',
            navigateText: 'Navigate',
            navigateUpKeyAriaLabel: 'Up arrow',
            navigateDownKeyAriaLabel: 'Down arrow',
            closeText: 'Close',
            closeKeyAriaLabel: 'Esc',
          },
        },
      },
    },
  },
}

export default locales
