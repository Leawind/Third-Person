<script setup lang="ts">
import { onMounted } from 'vue'
import { useData } from 'vitepress'
import { getRedirectLocale, pathHasLocale } from '../shared/redirect.ts'

const { site } = useData()

onMounted(() => {
  if (typeof window === 'undefined') return

  const params = new URLSearchParams(window.location.search)
  if (!params.has('autolang')) return

  const rawPath = window.location.pathname
  const base = site.value.base.replace(/\/$/, '') // "/docs" or ""
  const path = rawPath.startsWith(base) ? rawPath.slice(base.length) || '/' : rawPath
  const browserLocale = getRedirectLocale()

  params.delete('autolang')
  const qs = params.toString()
  const qsPart = qs ? '?' + qs : ''

  if (!pathHasLocale(path)) {
    // No locale in path — prepend browser locale.
    const newPath = base + '/' + browserLocale + (path === '/' ? '/' : path)
    window.location.replace(newPath + qsPart)
    return
  }

  const currentLocale = path.split('/')[1]

  if (browserLocale !== currentLocale) {
    // Replace the first segment (the current locale) safely.
    const rest = path.slice(currentLocale.length + 1) // +1 for the leading '/'
    const newPath = base + '/' + browserLocale + rest
    window.location.replace(newPath + qsPart)
  } else {
    // Already on the correct locale — just clean the URL.
    window.history.replaceState(null, '', rawPath + qsPart)
  }
})
</script>

<template>
  <div style="display: none" />
</template>
