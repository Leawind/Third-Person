import 'jsr:@std/dotenv@0.225.6/load'
import * as fs from '@leawind/inventory/fs'

export const SITE_ROOT = Deno.env.get('SITE_ROOT')
export const DIST_DIR = fs.P`dist/`.absolute()
