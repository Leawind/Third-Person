import localeMeta from './locale-meta.ts'

/**
 * Get the best-matching locale code for the given BCP 47 language tag.
 *
 * Matching strategy (tried in order):
 * 1. Exact BCP 47 match (case-insensitive)
 * 2. Primary language subtag match (the part before the first `-`)
 * 3. Fallback to the first locale in the map
 *
 * @param browserLang  e.g. navigator.language ('zh-CN', 'en-GB', 'ja', …)
 * @returns locale code e.g. 'zh_cn', 'en_us'
 */
export function getRedirectLocale(browserLang?: string): string {
  const lang = browserLang
    || (typeof navigator !== 'undefined' ? navigator.language : undefined)
    // `userLanguage` is IE-specific; fallback after `navigator.language`.
    || (typeof navigator !== 'undefined'
      ? (navigator as any).userLanguage
      : undefined)

  if (!lang) { return Object.keys(localeMeta)[0] }

  // 1. Exact BCP 47 match
  for (const [code, loc] of Object.entries(localeMeta)) {
    if (loc.bcp47.toLowerCase() === lang.toLowerCase()) { return code }
  }

  // 2. Primary language subtag match
  const primaryTag = lang.split('-')[0].toLowerCase()
  for (const [code, loc] of Object.entries(localeMeta)) {
    if (loc.bcp47.split('-')[0].toLowerCase() === primaryTag) { return code }
  }

  // 3. Fallback to first locale
  return Object.keys(localeMeta)[0]
}

/**
 * Check whether `pathname` already starts with a known locale prefix.
 */
export function pathHasLocale(pathname: string): boolean {
  return Object.keys(localeMeta).some((code) =>
    pathname.startsWith('/' + code + '/') || pathname === '/' + code
  )
}
