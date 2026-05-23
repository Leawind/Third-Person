---
head:
  - - meta
    - http-equiv: refresh
      content: '0;url=/Third-Person/en_us/'
---

[Redirecting, if it doesn't work, please click here](./en_us)

<script setup>
import { getRedirectLocale } from './.vitepress/shared/redirect.ts'

if (typeof window !== 'undefined') {
  const params = new URLSearchParams(window.location.search)
  const useAutolang = params.has('autolang')
  params.delete('autolang')
  const locale = useAutolang ? getRedirectLocale() : 'en_us'
  const qs = params.toString()
  window.location.replace('/Third-Person/' + locale + '/' + (qs ? '?' + qs : ''))
}
</script>
