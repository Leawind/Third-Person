import * as fs from '@leawind/inventory/fs'

/**
 * Language code compatibility mapping: old code → new code.
 * Add more mappings here when introducing new languages with different code formats.
 */
const LANG_COMPAT_MAP: Record<string, string> = {
  'en-US': 'en_us',
  'zh-CN': 'zh_cn',
}

/**
 * VitePress buildEnd hook.
 * Iterates over all generated pages and creates static redirect HTML files
 * at old language code paths, so that visiting old URLs automatically
 * redirects to the new URLs in the browser.
 */
export async function buildEndRedirectOldLangs(siteConfig: any) {
  const { pages, outDir, userConfig } = siteConfig

  // Respect config.ts `base` (e.g. '/Third-Person') so redirect URLs are complete.
  const base = (userConfig?.base || '/').replace(/\/+$/, '')

  for (const page of pages) {
    // pages is an array of strings like "en_us/Details/API.md"
    const pagePath: string = typeof page === 'string' ? page : page?.routePath
    if (!pagePath) { continue }

    for (const [oldLang, newLang] of Object.entries(LANG_COMPAT_MAP)) {
      if (
        pagePath === `${newLang}.md`
        || pagePath.startsWith(`${newLang}/`)
      ) {
        const oldPagePath = pagePath.replace(
          new RegExp(`^${newLang}`),
          oldLang,
        )

        // Compute target URL: base + pagePath without .md and /index
        const pageUrl = pagePath
          .replace(/\.md$/, '')
          .replace(/\/index$/, '')
        const newUrl = base + '/' + pageUrl

        const redirectHtml = `<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <meta http-equiv="refresh" content="0; url=${newUrl}">
  <link rel="canonical" href="${newUrl}">
  <title>Redirecting...</title>
</head>
<body>
  <p>This page has moved to <a href="${newUrl}">${newUrl}</a></p>
  <script>location.replace('${newUrl}')</script>
</body>
</html>`

        const outPath = fs.P`${outDir}/${oldPagePath}`
          .toString()
          .replace(/\.md$/, '.html')
        const parentDir = outPath.substring(0, outPath.lastIndexOf('/'))
        await Deno.mkdir(parentDir, { recursive: true })
        await Deno.writeTextFile(outPath, redirectHtml)
      }
    }
  }
}
