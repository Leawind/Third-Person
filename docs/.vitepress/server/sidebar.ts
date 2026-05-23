import log from '@leawind/inventory/log'
import { Path, type PathLike } from '@leawind/inventory/fs'
import * as fs from '@leawind/inventory/fs'
import * as frontMatter from 'jsr:@std/front-matter@1.0.9'
import { DefaultTheme } from 'vitepress/theme'

/** 去掉路径片段中的 `NN-` 序号前缀 */
function stripPrefix(segment: string): string {
  return segment.replace(/^\d+-/, '')
}

/**
 * 检测同一目录下，去掉前缀后 base-name 重复的文件。
 * 发现冲突时打印错误并退出构建。
 */
function checkDuplicateBaseNames(entries: Path[]): void {
  const baseNameCount = new Map<string, string[]>()
  for (const entry of entries) {
    if (!entry.isFileSync() || entry.name === 'index.md') { continue }
    const base = stripPrefix(entry.nameNoExt)
    if (!baseNameCount.has(base)) { baseNameCount.set(base, []) }
    baseNameCount.get(base)!.push(entry.name)
  }
  let hasConflict = false
  for (const [base, files] of baseNameCount) {
    if (files.length > 1) {
      log.error(
        `文件名冲突: ${files.join(', ')} 去掉前缀后都映射到 "${base}"`,
      )
      hasConflict = true
    }
  }
  if (hasConflict) { Deno.exit(1) }
}

export function buildSidebars(
  base: PathLike,
  lang: string,
): DefaultTheme.SidebarMulti {
  const sidebars: DefaultTheme.SidebarMulti = {}

  const langRoot = fs.P`${base}/${lang}`

  for (const dir of langRoot.listSync()) {
    if (dir.isDirectorySync()) {
      sidebars[`/${lang}/${dir.name}`] = buildSidebar({ dir, base })
    }
  }

  return sidebars
}

export type Options = {
  /**
   * Relative to cwd or absolute
   */
  dir: PathLike
  base?: PathLike
}
const DEFAULT_OPTIONS = {
  base: 'docs',
}

export function buildSidebar(options: Options): DefaultTheme.SidebarItem[] {
  const opts = Object.assign({}, DEFAULT_OPTIONS, options)
  const dir = Path.from(opts.dir)
  const base = Path.from(opts.base)

  const sidebar = {
    ...buildSidebarRecursive(options),
    collapsed: false,
    link: getLink(dir, base),
  }

  const result: DefaultTheme.SidebarItem[] = [sidebar]
  if (sidebar.items) {
    let i = 0
    while (i < sidebar.items.length) {
      const item = sidebar.items[i]
      if ('items' in item) {
        sidebar.items.splice(i, 1)
        result.push(item)
        continue
      }
      i++
    }
  }
  return result
}

function buildSidebarRecursive(options: Options): DefaultTheme.SidebarItem {
  const opts = Object.assign({}, DEFAULT_OPTIONS, options)
  const dir = Path.from(opts.dir)
  const base = Path.from(opts.base)

  // get dir title and body
  let dirTitle: string
  let dirHasBody: boolean
  {
    const indexFile = dir.join('index.md')
    const indexInfo = parseFile(indexFile)
    dirTitle = indexInfo.title || dir.nameNoExt.replace(/^\d+-/, '')
    dirHasBody = indexInfo.body.trim() !== ''
  }

  const entries = dir.listSync()
  checkDuplicateBaseNames(entries)

  return {
    text: dirTitle,
    collapsed: true,
    ...(dirHasBody ? { link: getLink(dir, base) } : {}),
    items: entries
      .sort((a, b) => {
        const num = (name: string) => {
          const match = name.match(/^(\d+)-/)
          return match ? parseInt(match[1], 10) : Infinity
        }
        return num(a.name) - num(b.name)
      })
      .map((entry) => {
        if (entry.isFileSync()) {
          if (entry.name === 'index.md') {
            return undefined
          }
          return {
            text: parseFile(entry).title
              || entry.nameNoExt.replace(/^\d+-/, ''),
            link: getLink(entry, base),
          }
        } else {
          return buildSidebarRecursive({ dir: entry, base })
        }
      })
      .filter((item) => item !== undefined),
  }
}

export function parseFile(file: PathLike): {
  /**
   * be undefined if:
   *
   * - file does not exist
   * - file exist but no attributes
   */
  title?: string
  attrs: any
  body: string
} {
  const path = Path.from(file)

  if (path.existsSync()) {
    try {
      const content = path.readTextSync()
      const fm = frontMatter.extractYaml(content)
      const attrs: any = fm.attrs

      return {
        ...('title' in attrs ? { title: attrs.title } : {}),
        attrs: attrs,
        body: fm.body.trim(),
      }
    } catch (e) {
      // File may not have YAML frontmatter — treat entire content as body
      try {
        const content = path.readTextSync()
        return { attrs: {}, body: content.trim() }
      } catch {
        log.error(`Error reading file ${file}: ${e}`)
      }
    }
  }

  return { attrs: {}, body: '' }
}

function getLink(file: PathLike, base: PathLike) {
  file = Path.from(file)
  base = Path.from(base)

  return '/' + file.relative(base)
    .toString()
    .replace(/(^\/*)|(\/*$)/g, '') // remove leading and trailing slashes
    .split('/')
    .map(stripPrefix)
    .join('/')
    .replace(/\.md$/g, '') // remove .md suffix
    .replace(/\.html?$/g, '') // remove .htm(l) suffix
}

/**
 * 扫描语言目录下所有 .md 文件，生成 VitePress rewrites 映射表。
 * 同时检测去掉前缀后 base-name 重复的文件，发现冲突则报错退出。
 */
export function buildRewrites(
  base: PathLike,
  langs: string[],
): Record<string, string> {
  const rewrites: Record<string, string> = {}
  const basePath = Path.from(base)
  let hasConflict = false

  for (const lang of langs) {
    const langRoot = fs.P`${basePath}/${lang}`
    scanDir(langRoot)
  }

  if (hasConflict) { Deno.exit(1) }
  return rewrites

  function scanDir(dir: Path): void {
    const fileMap = new Map<string, string[]>() // baseName -> [originalNames]

    for (const entry of dir.listSync()) {
      if (entry.isFileSync()) {
        if (entry.name === 'index.md') { continue }

        const baseName = stripPrefix(entry.nameNoExt)
        const relativePath = entry.relative(basePath).toString()

        rewrites[relativePath] = relativePath
          .split('/')
          .map(stripPrefix)
          .join('/')

        // 冲突检测
        if (!fileMap.has(baseName)) {
          fileMap.set(baseName, [])
        }
        fileMap.get(baseName)!.push(entry.name)
      } else {
        scanDir(entry)
      }
    }

    // 检查冲突
    for (const [baseName, files] of fileMap) {
      if (files.length > 1) {
        log.error(
          `文件名冲突: ${files.join(', ')} 去掉前缀后都映射到 "${baseName}"`,
        )
        hasConflict = true
      }
    }
  }
}
