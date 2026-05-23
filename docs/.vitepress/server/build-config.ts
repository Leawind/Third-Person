import { LocaleConfig } from 'vitepress'
import { DefaultTheme } from 'vitepress'
import localeMeta from '../shared/locale-meta.ts'
import { buildSidebars } from './sidebar.ts'

type LocaleConfigValue<ThemeConfig = any> = LocaleConfig<ThemeConfig>[string]

const BASE = 'docs'
const EDIT_LINK_PATTERN =
  'https://github.com/Leawind/Third-Person/edit/gh-pages/docs/:path'

/**
 * Assemble VitePress locale configs from raw locale data.
 */
export default Object.fromEntries(
  Object.entries(localeMeta).map(([key, loc]) => [
    key,
    {
      lang: loc.lang,
      bcp47: loc.bcp47,
      label: loc.label,
      title: loc.title,
      titleTemplate: `:title | ${loc.title}`,
      description: loc.description,
      themeConfig: {
        nav: loc.nav,
        sidebar: buildSidebars(BASE, loc.lang),
        editLink: {
          pattern: EDIT_LINK_PATTERN,
          text: loc.editLinkText,
        },
        search: {
          provider: 'local',
          options: {
            locales: {
              ...(loc.search ? { [loc.lang]: loc.search } : {}),
            },
          },
        },
        lastUpdated: { text: loc.lastUpdatedText },
      },
    } satisfies LocaleConfigValue<DefaultTheme.Config> & { bcp47: string },
  ]),
)
