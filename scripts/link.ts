/**
 * @module link
 *
 * Make a symlink at ${SITE_ROOT} pointing to the dist directory, so that
 * the built documentation gets served under leawind.github.io/Third-Person.
 */
import { DIST_DIR, SITE_ROOT } from './lib/consts.ts'
import * as fs from '@leawind/inventory/fs'
import log from '@leawind/inventory/log'

if (import.meta.main) {
  if (!SITE_ROOT) {
    log.error('Environment variable `SITE_ROOT` is not set in .env')
    Deno.exit(1)
  }

  const LINK_PATH = fs.P`${SITE_ROOT}`.absolute()

  if (await LINK_PATH.isSymlink()) {
    if (
      LINK_PATH.targetSync().absolute().toString()
        !== DIST_DIR.toString()
    ) {
      log.error('Symlink points to the wrong directory, removing...')
      await LINK_PATH.remove()
    } else {
      log.info('Symlink already points to the correct directory')
      Deno.exit(0)
    }
  }

  if (await LINK_PATH.isDirectory()) {
    log.error(
      `Target path "${LINK_PATH}" exists as a regular directory. `
        + `Please remove it first, then run this script again.`,
    )
    Deno.exit(1)
  }

  Deno.symlinkSync(DIST_DIR.toString(), LINK_PATH.toString(), { type: 'dir' })
  log.info(`Created symlink: ${LINK_PATH} → ${DIST_DIR}`)
}
