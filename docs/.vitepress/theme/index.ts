import DefaultTheme from 'vitepress/theme'
import { h } from 'vue'
import AutolangRedirect from './AutolangRedirect.vue'

export default {
  extends: DefaultTheme,
  Layout() {
    return h(DefaultTheme.Layout, null, {
      'layout-top': () => h(AutolangRedirect),
    })
  },
}
