import 'jsr:@std/dotenv@0.225.6/load'
import { DefaultTheme, defineConfig, UserConfig } from 'vitepress'
import { withMermaid } from 'vitepress-plugin-mermaid'
import * as fs from '@leawind/inventory/fs'
import localesConfig from './server/build-config.ts'
import { buildRewrites } from './server/sidebar.ts'
import { buildEndRedirectOldLangs } from './server/build-redirects.ts'

const BASE = '/Third-Person'
const isDev = Deno.args.includes('dev')

function file(rpath: string): { svg: string } {
  const fp = fs.P`docs/public/${rpath}`
  return { svg: fp.readTextSync() }
}

let config: UserConfig = {
  base: BASE,
  srcDir: '.',
  outDir: '../dist',
  cleanUrls: true,
  rewrites: buildRewrites('docs', Object.keys(localesConfig)),

  title: "Leawind's Third Person",
  description: "Documentation of minecraft mod Leawind's Third Person",

  markdown: {
    math: true,
  },
  ignoreDeadLinks: true,

  head: [
    ['link', { rel: 'icon', href: '/logo.svg' }],
  ],

  lastUpdated: true,

  themeConfig: {
    logo: '/logo.svg',
    externalLinkIcon: true,
    socialLinks: [
      {
        link: 'https://github.com/LEAWIND/Third-Person',
        icon: 'github',
      },
      {
        link:
          'https://www.curseforge.com/minecraft/mc-mods/leawind-third-person',
        icon: file('/icons/curseforge.svg'),
      },
      {
        link: 'https://modrinth.com/mod/leawind-third-person',
        icon: file('/icons/modrinth.svg'),
      },
    ],
    search: {
      provider: 'local',
      options: {
        miniSearch: {
          options: {
            processTerm: (term: string) => {
              term = term
                .toLowerCase()
                .replace(/([\u4e00-\u9fff])/g, '$1 ')
                .trim()
                .replace(/\s+/g, ' ')
              const terms = term.split(' ')
              return terms.length === 1 ? term : terms
            },
          },
          searchOptions: {},
        },
      },
    },
  },

  locales: localesConfig,
  buildEnd: buildEndRedirectOldLangs,
} satisfies UserConfig<DefaultTheme.Config>

if (!isDev) {
  config = withMermaid(config)
}

export default defineConfig(config)
