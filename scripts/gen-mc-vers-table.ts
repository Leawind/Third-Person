import {
  compareVersion,
  loadModVersionsFromChangelog,
  ModRelease,
  Table,
} from './util.ts'

const TABLE_MD_PATH = (lang: string) => `./docs/${lang}/Details/McVersions.md`
const CHANGELOG_PATH = './docs/zh_cn/changelog.md'
const TABLE_REGEX = /(?<=<div id="mv">)(.|\n)*(?=<\/div>)/
const YES_CHAR = '\u2705'
const BLANK_CHAR = ' '

generate('zh_cn')
generate('en_us')

function generate(lang: string) {
  const versions = loadModVersionsFromChangelog(CHANGELOG_PATH)
  const table = new Table()

  table.insertColumn(0, 'Mod')

  // Write table
  versions.forEach((v) => {
    let rowId = table.rowIdOf(
      (rowId) => table.get(rowId, 'Mod') === v.modVersion,
    )
    // Row
    if (rowId < 0) {
      rowId = table.insertRow(0, [])
    }

    for (const mc_version of v.mcVersions) {
      // Column
      if (!table.hasColumn(mc_version)) {
        table.insertColumn(-1, mc_version)
      }

      table.set(rowId, mc_version, YES_CHAR)
    }
    table.set(rowId, 'Mod', `${v.modVersion}`)
  })

  // Sort
  {
    table.sortColsByHeader((a, b) => {
      if (a === 'Mod') { return -1 }
      if (b === 'Mod') { return -1 }
      return compareVersion(a, b)
    })
    table.sortRows('Mod', (a, b) => -compareVersion(a, b))
  }

  {
    for (let rowId = 0; rowId < table.rows; rowId++) {
      let v = table.get(rowId, 'Mod') || ''
      v = '`' + v + '`'
      table.set(rowId, 'Mod', v)
    }
  }

  const TableMarkdownPath = TABLE_MD_PATH(lang)
  let md = Deno.readTextFileSync(TableMarkdownPath)
  md = md.replace(
    TABLE_REGEX,
    '\n\n' + table.toMarkdown(BLANK_CHAR) + '\n\n',
  )
  Deno.writeTextFileSync(TableMarkdownPath, md)
}
