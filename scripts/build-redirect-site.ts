const OUTPUT_DIR = new URL('../dist/', import.meta.url)
const SOURCE_DIR = new URL('../docs/', import.meta.url)

const TARGETS = {
  zh: 'https://leawind.github.io/zh_cn/Third-Person/',
  en: 'https://leawind.github.io/en_us/Third-Person/',
} as const

function redirectPage(language?: keyof typeof TARGETS): string {
  const fixedTarget = language ? JSON.stringify(TARGETS[language]) : 'null'
  const fallbackTarget = language ? TARGETS[language] : TARGETS.zh

  return `<!doctype html>
<html lang="${language ?? 'zh-CN'}">
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="robots" content="noindex, follow">
    <title>Documentation moved / 文档已迁移</title>
    <script>
      const targets = ${JSON.stringify(TARGETS)};
      const fixedTarget = ${fixedTarget};
      const path = location.pathname.toLowerCase();
      const pathLanguage = /\\/(en_us|en-us)(?:\\/|$)/.test(path)
        ? 'en'
        : /\\/(zh_cn|zh-cn)(?:\\/|$)/.test(path) ? 'zh' : null;
      const browserLanguage = navigator.language.toLowerCase().startsWith('zh')
        ? 'zh'
        : 'en';
      location.replace(fixedTarget ?? targets[pathLanguage ?? browserLanguage]);
    </script>
    <meta http-equiv="refresh" content="1; url=${fallbackTarget}">
  </head>
  <body>
    <p>
      文档已迁移：<a href="${TARGETS.zh}">简体中文</a>
      / Documentation moved: <a href="${TARGETS.en}">English</a>
    </p>
  </body>
</html>
`
}

async function write(relativePath: string, content: string): Promise<void> {
  const destination = new URL(relativePath, OUTPUT_DIR)
  await Deno.mkdir(new URL('.', destination), { recursive: true })
  await Deno.writeTextFile(destination, content)
}

async function collectLegacyPages(
  directory: URL,
  relativeDirectory = '',
): Promise<string[]> {
  const pages: string[] = []

  for await (const entry of Deno.readDir(directory)) {
    if (entry.name.startsWith('.')) { continue }

    const relativePath = relativeDirectory + entry.name
    if (entry.isDirectory) {
      pages.push(
        ...await collectLegacyPages(
          new URL(`${entry.name}/`, directory),
          `${relativePath}/`,
        ),
      )
    } else if (entry.isFile && entry.name.endsWith('.md')) {
      pages.push(relativePath.replace(/\.md$/, '.html'))
    }
  }

  return pages
}

if (import.meta.main) {
  await Deno.remove(OUTPUT_DIR, { recursive: true }).catch((error) => {
    if (!(error instanceof Deno.errors.NotFound)) { throw error }
  })

  const legacyPages = await collectLegacyPages(SOURCE_DIR)
  const pages = new Map<string, string>([
    ['index.html', redirectPage()],
    ['404.html', redirectPage()],
    ['.nojekyll', ''],
  ])

  for (const legacyPage of legacyPages) {
    if (legacyPage.startsWith('zh_cn/')) {
      pages.set(legacyPage, redirectPage('zh'))
      pages.set(legacyPage.replace(/^zh_cn\//, 'zh-CN/'), redirectPage('zh'))
    } else if (legacyPage.startsWith('en_us/')) {
      pages.set(legacyPage, redirectPage('en'))
      pages.set(legacyPage.replace(/^en_us\//, 'en-US/'), redirectPage('en'))
    }
  }

  await Promise.all(
    [...pages].map(([relativePath, content]) => write(relativePath, content)),
  )
}
